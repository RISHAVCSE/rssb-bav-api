package com.RSSBAMB.API.Service;

import com.RSSBAMB.API.model.User;
import com.RSSBAMB.API.Repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for User Management
 * Handles CRUD operations with Keycloak and local database synchronization
 * Roles: SUPERADMIN, ADMIN, USER
 */
@Slf4j
@Service
@Transactional
public class UserService {

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private UserRepo userRepo;

    @Value("${keycloak.realm:springboot-test}")
    private String realm;

    private static final String ROLE_SUPERADMIN = "SUPERADMIN";
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    private String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        List<String> roles = jwt.getClaimAsStringList("roles");
        if (roles != null && !roles.isEmpty()) {
            return roles.get(0);
        }
        return ROLE_USER;
    }

    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        return jwt.getClaim("preferred_username");
    }

    public boolean isSuperAdmin() {
        return getCurrentUserRole().equals(ROLE_SUPERADMIN);
    }

    public boolean isAdmin() {
        String role = getCurrentUserRole();
        return role.equals(ROLE_ADMIN) || role.equals(ROLE_SUPERADMIN);
    }

    public void validateUserCreationPermission(String targetRole){
        String currentRole=getCurrentUserRole();
        if(currentRole.equals(ROLE_USER)){
            throw new AccessDeniedException("User Creation is not allowed");
        }
        if(currentRole.equals(ROLE_ADMIN)){
            if(targetRole.equals(ROLE_ADMIN) || targetRole.equals(ROLE_SUPERADMIN)){
                throw new AccessDeniedException("Admin cannot create Admin or SuperAdmin users");
            }
        }

        if(currentRole.equals(ROLE_SUPERADMIN)){
            return;
        }
    }
    public void validateUserUpdatePermission(User targetUser){
        String currentRole=getCurrentUserRole();

        if(currentRole.equals(ROLE_USER)){
            throw new AccessDeniedException("User Cannot update other users");
        }

        if(currentRole.equals(ROLE_ADMIN)){
            if(targetUser.getRoles().contains(ROLE_SUPERADMIN) ||
            targetUser.getRoles().contains(ROLE_ADMIN)){
                throw new AccessDeniedException("Admin cannot update Admin or SuperAdmin users");
            }
        }
    }

    // Validate delete permission
    private void validateUserDeletePermission(User targetUser) {
        String currentRole = getCurrentUserRole();

        if (currentRole.equals(ROLE_USER)) {
            throw new AccessDeniedException("Users cannot delete other users");
        }

        if (currentRole.equals(ROLE_ADMIN)) {
            if (targetUser.getRoles().contains(ROLE_SUPERADMIN) ||
                    targetUser.getRoles().contains(ROLE_ADMIN)) {
                throw new AccessDeniedException("Admins cannot delete SUPERADMIN or ADMIN users");
            }
        }
    }
    

    /**
     * Create a new user in Keycloak and sync to database
     * @param user User entity containing user details
     * @param password Plain text password for the new user
     * @return Created User with keycloakId
     */
    public User createUser(User user, String password) {
        validateUserCreationPermission(user.getRoles());
        try {
            // Create user in Keycloak
            UserRepresentation keycloakUser = new UserRepresentation();
            keycloakUser.setUsername(user.getUsername());
            keycloakUser.setEmail(user.getEmail());
            keycloakUser.setFirstName(user.getFirstName());
            keycloakUser.setLastName(user.getLastName());
            keycloakUser.setEnabled(true);

            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();

            // Create user and get the response to extract ID
            Response response = usersResource.create(keycloakUser);

            if (response.getStatus() != 201) {
                log.error("Failed to create user in Keycloak. Status: {}", response.getStatus());
                throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusInfo());
            }

            // Extract Keycloak ID from response location
            String keycloakId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("User created in Keycloak with ID: {}", keycloakId);

            // Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);

            UsersResource users = keycloak.realm(realm).users();
            users.get(keycloakId).resetPassword(credential);

            // Sync to database
            user.setKeycloakId(keycloakId);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwt = (Jwt) authentication.getPrincipal();

            String loggedInUsername = jwt.getClaim("preferred_username");

            user.setCreatedBy(loggedInUsername);

            User savedUser = userRepo.save(user);
            log.info("User synced to database with ID: {}", savedUser.getId());

            return savedUser;
        }catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw new RuntimeException("Error creating user: " + e.getMessage(), e);
        }
    }

    /**
     * Get user by ID from database
     * @param id User ID
     * @return User if found
     */
    public Optional<User> getUserById(Long id) {
        return userRepo.findById(id);
    }

    /**
     * Get user by username
     * @param username Username
     * @return User if found
     */
    public Optional<User> getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    /**
     * Get user by Keycloak ID
     * @param keycloakId Keycloak user ID
     * @return User if found
     */
    public Optional<User> getUserByKeycloakId(String keycloakId) {
        return userRepo.findByKeycloakId(keycloakId);
    }

    /**
     * Get all users from database
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     * Update existing user in both Keycloak and database
     * @param id User ID in database
     * @param userDetails Updated user details
     * @return Updated user
     */
    public User updateUser(Long id, User userDetails) {
        try {
            User existingUser = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            validateUserUpdatePermission(existingUser);

            String keycloakId = existingUser.getKeycloakId();

            // Update in Keycloak
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            UserRepresentation keycloakUser = usersResource.get(keycloakId).toRepresentation();

            if (userDetails.getEmail() != null) {
                keycloakUser.setEmail(userDetails.getEmail());
                existingUser.setEmail(userDetails.getEmail());
            }
            if (userDetails.getFirstName() != null) {
                keycloakUser.setFirstName(userDetails.getFirstName());
                existingUser.setFirstName(userDetails.getFirstName());
            }
            if (userDetails.getLastName() != null) {
                keycloakUser.setLastName(userDetails.getLastName());
                existingUser.setLastName(userDetails.getLastName());
            }
            if (userDetails.getRoles() != null) {
                existingUser.setRoles(userDetails.getRoles());
            }

            usersResource.get(keycloakId).update(keycloakUser);
            log.info("User updated in Keycloak with ID: {}", keycloakId);

            // Update in database
            User updatedUser = userRepo.save(existingUser);
            log.info("User updated in database with ID: {}", updatedUser.getId());

            return updatedUser;
        }catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.error("Error updating user: {}", e.getMessage(), e);
            throw new RuntimeException("Error updating user: " + e.getMessage(), e);
        }
    }

    /**
     * Delete user from both Keycloak and database
     * @param id User ID in database
     */
    public void deleteUser(Long id) {
        try {
            User user = userRepo.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
            validateUserDeletePermission(user);

            String keycloakId = user.getKeycloakId();

            // Delete from Keycloak
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            usersResource.delete(keycloakId);
            log.info("User deleted from Keycloak with ID: {}", keycloakId);

            // Delete from database
            userRepo.deleteById(id);
            log.info("User deleted from database with ID: {}", id);
        }catch (AccessDeniedException e) {
            log.error("Access denied: {}", e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.error("Error deleting user: {}", e.getMessage(), e);
            throw new RuntimeException("Error deleting user: " + e.getMessage(), e);
        }
    }

    /**
     * Reset password for existing user
     * @param id User ID
     * @param newPassword New password
     */
    public void resetPassword(Long id, String newPassword) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        String currentRole = getCurrentUserRole();
        String currentUsername = getCurrentUsername();

        if (!isSuperAdmin() && !user.getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Users can only reset their own password");
        }
        try {

            String keycloakId = user.getKeycloakId();

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);
            credential.setTemporary(false);

            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            usersResource.get(keycloakId).resetPassword(credential);

            log.info("Password reset for user: {}", keycloakId);
        } catch (Exception e) {
            log.error("Error resetting password: {}", e.getMessage(), e);
            throw new RuntimeException("Error resetting password: " + e.getMessage(), e);
        }
    }

    /**
     * Get all users from Keycloak (read-only)
     * @return List of users from Keycloak
     */
    public List<UserRepresentation> getKeycloakUsers() {
        if (!isSuperAdmin()) {
            throw new AccessDeniedException("Only SUPERADMIN can view Keycloak users");
        }

        try {
            RealmResource realmResource = keycloak.realm(realm);
            UsersResource usersResource = realmResource.users();
            return usersResource.list();
        } catch (Exception e) {
            log.error("Error fetching users from Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Error fetching users from Keycloak: " + e.getMessage(), e);
        }
    }
}

