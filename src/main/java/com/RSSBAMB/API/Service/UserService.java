package com.RSSBAMB.API.Service;

import com.RSSBAMB.API.model.Role;
import com.RSSBAMB.API.model.User;
import com.RSSBAMB.API.Repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
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
import java.util.Map;
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

    private static final Role ROLE_SUPERADMIN = Role.SUPERADMIN;
    private static final Role ROLE_ADMIN = Role.ADMIN;
    private static final Role ROLE_USER = Role.USER;
    private static final Role ROLE_SUBUSER = Role.SUBUSER;

    /**
     * FIX #1: Extract roles from realm_access.roles and filter out Keycloak system roles
     * Keycloak stores realm roles under: jwt.realm_access.roles
     * System roles to ignore: offline_access, default-roles-*, uma_authorization
     * This method iterates through all roles, filters system roles, and returns the first valid application role
     */
    private Role getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        
        // Extract roles from realm_access map (Keycloak's actual location)
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        List<String> roles = null;
        
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            roles = (List<String>) realmAccess.get("roles");
        }
        
        log.debug("🔍 JWT realm_access.roles found: {}", roles);
        
        if (roles != null && !roles.isEmpty()) {
            // Keycloak system roles to filter out
            List<String> systemRoles = List.of("offline_access", "uma_authorization");
            
            // Iterate through all roles and find the first valid application role
            for (String role : roles) {
                // Skip Keycloak system roles
                if (systemRoles.contains(role)) {
                    log.debug("⏭️  Skipping system role: {}", role);
                    continue;
                }
                
                // Skip default realm roles pattern (e.g., default-roles-springboot-test)
                if (role.startsWith("default-roles-")) {
                    log.debug("⏭️  Skipping default realm role: {}", role);
                    continue;
                }
                
                // Try to match this role against the Role enum
                try {
                    Role appRole = Role.valueOf(role.toUpperCase());
                    log.info("✅ Extracted application role from JWT: {} (from raw value: {})", appRole, role);
                    return appRole;
                } catch (IllegalArgumentException e) {
                    log.warn("⚠️  Invalid application role in JWT: {} (not in Role enum), skipping", role);
                    continue;
                }
            }
            
            // No valid application role found
            log.warn("⚠️  No valid application role found in JWT. All roles were either system roles or invalid. Roles in token: {}", roles);
        } else {
            log.warn("⚠️  No roles found in JWT realm_access claim");
        }
        
        log.debug("🔐 Defaulting to USER role");
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
        Role role = getCurrentUserRole();
        return role.equals(ROLE_ADMIN) || role.equals(ROLE_SUPERADMIN);
    }

    public void validateUserCreationPermission(Role targetRole){
        Role currentRole=getCurrentUserRole();
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
        Role currentRole=getCurrentUserRole();

        if(currentRole.equals(ROLE_USER)){
            throw new AccessDeniedException("User Cannot update other users");
        }

        if(currentRole.equals(ROLE_ADMIN)){
            Role targetRole = targetUser.getRole();
            if(targetRole.equals(ROLE_SUPERADMIN) || targetRole.equals(ROLE_ADMIN)){
                throw new AccessDeniedException("Admin cannot update Admin or SuperAdmin users");
            }
        }
    }

    // Validate delete permission
    private void validateUserDeletePermission(User targetUser) {
        Role currentRole = getCurrentUserRole();

        if (currentRole.equals(ROLE_USER)) {
            throw new AccessDeniedException("Users cannot delete other users");
        }

        if (currentRole.equals(ROLE_ADMIN)) {
            Role targetRole = targetUser.getRole();
            if (targetRole.equals(ROLE_SUPERADMIN) || targetRole.equals(ROLE_ADMIN)) {
                throw new AccessDeniedException("Admins cannot delete SUPERADMIN or ADMIN users");
            }
        }
    }
    

    /**
     * BOOTSTRAP METHOD: Create initial SUPERADMIN without authentication
     * This bypasses normal permission checks - only callable when no users exist
     * Use this endpoint first to bootstrap your system
     * 
     * @param user User entity containing user details
     * @param password Plain text password for the new user
     * @return Created User with keycloakId
     */
    public User bootstrapCreateSuperAdmin(User user, String password) {
        // Check if any users exist - bootstrap only allowed on empty database
        List<User> existingUsers = userRepo.findAll();
        if (!existingUsers.isEmpty()) {
            throw new RuntimeException("Bootstrap only allowed when no users exist. Use normal createUser() instead.");
        }
        
        log.warn("🔐 BOOTSTRAP: Creating initial SUPERADMIN user - {}", user.getUsername());
        String keycloakId = null;
        
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

            Response response = usersResource.create(keycloakUser);

            if (response.getStatus() != 201) {
                String errorBody = response.readEntity(String.class);
                log.error("Failed to create bootstrap user in Keycloak. Status: {}, Body: {}", response.getStatus(), errorBody);
                throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusInfo());
            }

            keycloakId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("✓ Bootstrap user created in Keycloak with ID: {}", keycloakId);

            // Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);

            usersResource.get(keycloakId).resetPassword(credential);
            log.info("✓ Bootstrap user password set");

            // FIX #2: Assign SUPERADMIN role to Keycloak user
            String roleName = Role.SUPERADMIN.name();
            try {
                assignRoleToKeycloakUser(roleName, keycloakId, realmResource, usersResource);
            } catch (RuntimeException roleException) {
                log.error("❌ CRITICAL: Role assignment failed for SUPERADMIN. Bootstrap cannot continue. Rolling back Keycloak user...");
                try {
                    usersResource.get(keycloakId).remove();
                    log.info("✓ Keycloak user '{}' deleted due to role assignment failure", keycloakId);
                } catch (Exception deleteException) {
                    log.error("❌ Failed to delete Keycloak user '{}' during rollback: {}", keycloakId, deleteException.getMessage());
                }
                throw new RuntimeException("Bootstrap failed: SUPERADMIN role could not be assigned in Keycloak", roleException);
            }

            // Save to local database
            user.setKeycloakId(keycloakId);
            user.setRole(Role.SUPERADMIN);
            user.setCreatedBy("BOOTSTRAP");

            // FIX #3: Handle distributed transactions - wrap database save in try-catch
            try {
                User savedUser = userRepo.save(user);
                log.warn("✓ BOOTSTRAP COMPLETE: SUPERADMIN created successfully - ID: {}", savedUser.getId());
                return savedUser;
            } catch (Exception dbException) {
                log.error("❌ Failed to save bootstrap user to database. Rolling back Keycloak user...");
                try {
                    usersResource.get(keycloakId).remove();
                    log.info("✓ Keycloak user {} deleted due to database save failure", keycloakId);
                } catch (Exception deleteException) {
                    log.error("❌ Failed to delete Keycloak user {} during rollback: {}", keycloakId, deleteException.getMessage());
                }
                throw new RuntimeException("Bootstrap failed: Database save failed after Keycloak user creation", dbException);
            }
        } catch (Exception e) {
            log.error("❌ Bootstrap failed: {}", e.getMessage(), e);
            throw new RuntimeException("Bootstrap failed: " + e.getMessage(), e);
        }
    }

    /**
     * Create a new user in Keycloak and sync to database
     * @param user User entity containing user details
     * @param password Plain text password for the new user
     * @return Created User with keycloakId
     */
    public User createUser(User user, String password) {
        validateUserCreationPermission(user.getRole());
        String keycloakId = null;
        
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
                String errorBody = response.readEntity(String.class);
                log.error("Failed to create user in Keycloak. Status: {}, Body: {}", response.getStatus(), errorBody);
                throw new RuntimeException("Failed to create user in Keycloak: " + response.getStatusInfo());
            }

            // Extract Keycloak ID from response location
            keycloakId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            log.info("User created in Keycloak with ID: {}", keycloakId);

            // Set password
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(password);
            credential.setTemporary(false);

            UsersResource users = keycloak.realm(realm).users();
            users.get(keycloakId).resetPassword(credential);
            log.info("✓ Password set for user in Keycloak");

            // FIX #2: Sync roles to Keycloak - Assign user's role to Keycloak realm roles
            String roleName = user.getRole().name();
            try {
                assignRoleToKeycloakUser(roleName, keycloakId, realmResource, usersResource);
            } catch (RuntimeException roleException) {
                log.error("❌ CRITICAL: Role assignment failed for role '{}'. User creation cannot continue. Rolling back Keycloak user...", roleName);
                try {
                    usersResource.get(keycloakId).remove();
                    log.info("✓ Keycloak user '{}' deleted due to role assignment failure", keycloakId);
                } catch (Exception deleteException) {
                    log.error("❌ Failed to delete Keycloak user '{}' during rollback: {}", keycloakId, deleteException.getMessage());
                }
                throw new RuntimeException("Failed to create user: Role '" + roleName + "' could not be assigned in Keycloak", roleException);
            }

            // Sync to database
            user.setKeycloakId(keycloakId);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwt = (Jwt) authentication.getPrincipal();

            String loggedInUsername = jwt.getClaim("preferred_username");
            user.setCreatedBy(loggedInUsername);

            // FIX #3: Handle distributed transactions - wrap database save in try-catch
            try {
                User savedUser = userRepo.save(user);
                log.info("User synced to database with ID: {}", savedUser.getId());
                return savedUser;
            } catch (Exception dbException) {
                log.error("Failed to save user to database. Deleting Keycloak user {} to prevent orphaned accounts", keycloakId);
                try {
                    usersResource.get(keycloakId).remove();
                    log.info("Keycloak user {} deleted due to database save failure", keycloakId);
                } catch (Exception deleteException) {
                    log.error("Failed to delete Keycloak user {} during rollback: {}", keycloakId, deleteException.getMessage());
                }
                throw new RuntimeException("Error creating user: Database save failed after Keycloak user creation", dbException);
            }
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
            if (userDetails.getRole() != null) {
                syncRoleToKeycloakUser(userDetails.getRole(), keycloakId, realmResource, usersResource);
                existingUser.setRole(userDetails.getRole());
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

        Role currentRole = getCurrentUserRole();
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

    /**
     * FIX #4: Helper method to validate role exists and assign it to user in Keycloak
     * This method ensures the role exists in Keycloak before assignment and verifies success
     * @param roleName The role name to assign (e.g., "ADMIN", "SUPERADMIN")
     * @param keycloakId The Keycloak user ID
     * @param realmResource The Keycloak realm resource
     * @param usersResource The Keycloak users resource
     * @throws RuntimeException if role doesn't exist or assignment fails
     */
    private void assignRoleToKeycloakUser(String roleName, String keycloakId, RealmResource realmResource, UsersResource usersResource) {
        log.info("🔄 Attempting to assign role '{}' to Keycloak user '{}'", roleName, keycloakId);
        
        try {
            // Step 1: Get all available roles in the realm
            List<RoleRepresentation> allRealmRoles = realmResource.roles().list();
            log.debug("Available roles in realm: {}", 
                allRealmRoles.stream().map(RoleRepresentation::getName).toList());
            
            // Step 2: Find the specific role from the list
            RoleRepresentation roleRepresentation = allRealmRoles.stream()
                .filter(role -> role.getName().equalsIgnoreCase(roleName))
                .findFirst()
                .orElse(null);
            
            if (roleRepresentation == null) {
                String availableRoles = allRealmRoles.stream()
                    .map(RoleRepresentation::getName)
                    .sorted()
                    .toList()
                    .toString();
                log.error("❌ Role '{}' not found in Keycloak realm. Available roles: {}", roleName, availableRoles);
                throw new RuntimeException("Role '" + roleName + "' does not exist in Keycloak realm. Available roles: " + availableRoles);
            }
            
            log.debug("✓ Role '{}' found in Keycloak with ID: {}", roleName, roleRepresentation.getId());
            
            // Step 3: Add role to user
            List<RoleRepresentation> rolesToAdd = new ArrayList<>();
            rolesToAdd.add(roleRepresentation);
            
            try {
                usersResource.get(keycloakId).roles().realmLevel().add(rolesToAdd);
            } catch (Exception assignError) {
                log.error("❌ Failed to add role to user roles mapping: {}", assignError.getMessage(), assignError);
                throw assignError;
            }
            
            log.info("✅ Successfully assigned role '{}' to Keycloak user '{}'", roleName, keycloakId);
            
            // Step 4: Verify the assignment (optional but recommended)
            try {
                List<RoleRepresentation> userRoles = usersResource.get(keycloakId).roles().realmLevel().listAll();
                List<String> userRoleNames = userRoles.stream().map(RoleRepresentation::getName).toList();
                log.debug("User '{}' now has roles: {}", keycloakId, userRoleNames);
                
                if (!userRoleNames.contains(roleName)) {
                    log.warn("⚠️  Role '{}' was assigned but verification failed. User roles: {}", roleName, userRoleNames);
                } else {
                    log.debug("✓ Role assignment verified for user '{}'", keycloakId);
                }
            } catch (Exception verifyException) {
                log.warn("⚠️  Could not verify role assignment: {}", verifyException.getMessage());
            }
            
        } catch (RuntimeException runtimeEx) {
            log.error("❌ Failed to assign role '{}' to user '{}': {}", roleName, keycloakId, runtimeEx.getMessage());
            throw runtimeEx;
        } catch (Exception e) {
            String errorMsg = "Failed to assign role '" + roleName + "' to Keycloak user '" + keycloakId + "': " + e.getMessage();
            log.error("❌ {}", errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

    /**
     * Replace the user application role in Keycloak with the selected role.
     * Keycloak default/system roles are left untouched.
     */
    private void syncRoleToKeycloakUser(Role newRole, String keycloakId, RealmResource realmResource, UsersResource usersResource) {
        String newRoleName = newRole.name();
        log.info("Syncing Keycloak role {} for user {}", newRoleName, keycloakId);

        try {
            List<String> applicationRoleNames = List.of(
                    Role.SUPERADMIN.name(),
                    Role.ADMIN.name(),
                    Role.USER.name(),
                    Role.SUBUSER.name()
            );

            List<RoleRepresentation> assignedApplicationRoles = usersResource.get(keycloakId)
                    .roles()
                    .realmLevel()
                    .listAll()
                    .stream()
                    .filter(role -> applicationRoleNames.stream()
                            .anyMatch(appRole -> appRole.equalsIgnoreCase(role.getName())))
                    .toList();

            if (!assignedApplicationRoles.isEmpty()) {
                usersResource.get(keycloakId).roles().realmLevel().remove(assignedApplicationRoles);
                log.info("Removed existing application roles from Keycloak user {}: {}",
                        keycloakId,
                        assignedApplicationRoles.stream().map(RoleRepresentation::getName).toList());
            }

            assignRoleToKeycloakUser(newRoleName, keycloakId, realmResource, usersResource);
        } catch (RuntimeException e) {
            log.error("Failed to sync Keycloak role {} for user {}: {}", newRoleName, keycloakId, e.getMessage());
            throw e;
        } catch (Exception e) {
            String errorMsg = "Failed to sync Keycloak role '" + newRoleName + "' for user '" + keycloakId + "': " + e.getMessage();
            log.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }

}

