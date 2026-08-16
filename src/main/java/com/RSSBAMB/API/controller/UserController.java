package com.RSSBAMB.API.controller;

import com.RSSBAMB.API.DTO.PasswordResetDTO;
import com.RSSBAMB.API.DTO.UserCreateDTO;
import com.RSSBAMB.API.DTO.UserUpdateDTO;
import com.RSSBAMB.API.Service.UserService;
import com.RSSBAMB.API.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for User Management
 * Provides endpoints for CRUD operations on users
 */
@Slf4j
@CrossOrigin(origins="http://localhost:3000")
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "APIs for managing users with Keycloak integration")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Create a new user
     * POST /api/users
     */
    @PostMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create a new user", description = "Creates a new user in Keycloak and syncs to database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<?> createUser(@RequestBody UserCreateDTO userCreateDTO) {
        try {
            log.info("Creating user: {}", userCreateDTO.getUsername());

            User user = new User();
            user.setUsername(userCreateDTO.getUsername());
            user.setEmail(userCreateDTO.getEmail());
            user.setFirstName(userCreateDTO.getFirstName());
            user.setLastName(userCreateDTO.getLastName());
            user.setRole(userCreateDTO.getRole());
            user.setParentUser(userCreateDTO.getParentUserId());

            User createdUser = userService.createUser(user, userCreateDTO.getPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User created successfully");
            response.put("data", createdUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Bootstrap endpoint - Create initial SUPERADMIN
     * Only works if no users exist in the system
     * Call this FIRST to bootstrap your application
     * No authentication required for this endpoint
     * 
     * POST /api/users/bootstrap
     */
    @PostMapping("/bootstrap")
    @Operation(summary = "Bootstrap initial SUPERADMIN", 
        description = "Creates the first SUPERADMIN user. Only works when database is empty. No authentication required.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Bootstrap SUPERADMIN created successfully"),
            @ApiResponse(responseCode = "400", description = "Bootstrap failed or users already exist"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<?> bootstrapSuperAdmin(@RequestBody UserCreateDTO userCreateDTO) {
        try {
            log.warn("🔐 Bootstrap request received for: {}", userCreateDTO.getUsername());
            
            User user = new User();
            user.setUsername(userCreateDTO.getUsername());
            user.setEmail(userCreateDTO.getEmail());
            user.setFirstName(userCreateDTO.getFirstName());
            user.setLastName(userCreateDTO.getLastName());

            User createdUser = userService.bootstrapCreateSuperAdmin(user, userCreateDTO.getPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "✓ Bootstrap SUPERADMIN created successfully! Now you can login and create other users.");
            response.put("data", createdUser);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("❌ Bootstrap failed: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get user by ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieves a user by their database ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<?> getUserById(
            @Parameter(description = "User ID")
            @PathVariable Long id) {
        try {
            Optional<User> user = userService.getUserById(id);

            if (user.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", user.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Get user by username
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username", description = "Retrieves a user by their username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<?> getUserByUsername(
            @Parameter(description = "Username")
            @PathVariable String username) {
        try {
            Optional<User> user = userService.getUserByUsername(username);

            if (user.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", user.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

        }
    }

    /**
     * Get user by Keycloak ID
     * GET /api/users/keycloak/{keycloakId}
     */
    @GetMapping("/keycloak/{keycloakId}")
    @Operation(summary = "Get user by Keycloak ID", description = "Retrieves a user by their Keycloak user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<?> getUserByKeycloakId(
            @Parameter(description = "Keycloak user ID")
            @PathVariable String keycloakId) {
        try {
            Optional<User> user = userService.getUserByKeycloakId(keycloakId);

            if (user.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", user.get());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        } catch (Exception e) {
            log.error("Error fetching user: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

        }
    }

    /**
     * Get all users
     * GET /api/users
     */
    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieves all users from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching users: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);

        }
    }

    /**
     * Update user
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update a user", description = "Updates an existing user in both Keycloak and database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<?> updateUser(
            @Parameter(description = "User ID")
            @PathVariable Long id,
            @RequestBody UserUpdateDTO userUpdateDTO) {
        try {
            log.info("Updating user: {}", id);

            User userDetails = new User();
            userDetails.setEmail(userUpdateDTO.getEmail());
            userDetails.setFirstName(userUpdateDTO.getFirstName());
            userDetails.setLastName(userUpdateDTO.getLastName());
            userDetails.setRole(userUpdateDTO.getRole());

            User updatedUser = userService.updateUser(id, userDetails);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User updated successfully");
            response.put("data", updatedUser);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Delete user
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a user", description = "Deletes a user from both Keycloak and database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "User ID")
            @PathVariable Long id) {
        try {
            log.info("Deleting user: {}", id);

            userService.deleteUser(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Reset password
     * POST /api/users/{id}/reset-password
     */
    @PostMapping("/{id}/reset-password")
    @Operation(summary = "Reset password", description = "Resets the password for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<?> resetPassword(
            @Parameter(description = "User ID")
            @PathVariable Long id,
            @RequestBody PasswordResetDTO passwordResetDTO) {
        try {
            log.info("Resetting password for user: {}", id);

            userService.resetPassword(id, passwordResetDTO.getNewPassword());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Password reset successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    /**
     * Get Keycloak users (admin only)
     * GET /api/users/keycloak/list/all
     */
    @GetMapping("/keycloak/list/all")
    @Operation(summary = "Get all Keycloak users", description = "Retrieves all users from Keycloak (SUPERADMIN only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Keycloak users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden - SUPERADMIN only"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    public ResponseEntity<?> getKeycloakUsers() {
        try {
            var keycloakUsers = userService.getKeycloakUsers();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", keycloakUsers);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching Keycloak users: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

