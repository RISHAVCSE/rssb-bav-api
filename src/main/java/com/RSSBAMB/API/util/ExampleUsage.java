//package com.RSSBAMB.API.util;
//
///**
// * Example Usage Guide for Keycloak User Management
// *
// * This file demonstrates how to use the UserService and UserController
// * for managing users with Keycloak integration.
// */
//
///*
//
//EXAMPLE 1: CREATE A NEW USER
//================================
//
//Request:
//POST /api/users
//Content-Type: application/json
//
//{
//  "username": "alice.johnson",
//  "email": "alice.johnson@example.com",
//  "firstName": "Alice",
//  "lastName": "Johnson",
//  "password": "SecurePassword123!@#",
//  "roles": "user,customer",
//  "createdBy": "admin"
//}
//
//Response (201 Created):
//{
//  "success": true,
//  "message": "User created successfully",
//  "data": {
//    "id": 1,
//    "keycloakId": "a1b2c3d4-e5f6-4a7b-8c9d-e0f1a2b3c4d5",
//    "username": "alice.johnson",
//    "email": "alice.johnson@example.com",
//    "firstName": "Alice",
//    "lastName": "Johnson",
//    "roles": "user,customer",
//    "createdBy": "admin"
//  }
//}
//
//
//EXAMPLE 2: GET USER BY ID
//================================
//
//Request:
//GET /api/users/1
//Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5c...
//
//Response (200 OK):
//{
//  "success": true,
//  "data": {
//    "id": 1,
//    "keycloakId": "a1b2c3d4-e5f6-4a7b-8c9d-e0f1a2b3c4d5",
//    "username": "alice.johnson",
//    "email": "alice.johnson@example.com",
//    "firstName": "Alice",
//    "lastName": "Johnson",
//    "roles": "user,customer",
//    "createdBy": "admin"
//  }
//}
//
//
//EXAMPLE 3: GET USER BY USERNAME
//================================
//
//Request:
//GET /api/users/username/alice.johnson
//Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5c...
//
//Response (200 OK):
//{
//  "success": true,
//  "data": {
//    "id": 1,
//    "keycloakId": "a1b2c3d4-e5f6-4a7b-8c9d-e0f1a2b3c4d5",
//    "username": "alice.johnson",
//    "email": "alice.johnson@example.com",
//    "firstName": "Alice",
//    "lastName": "Johnson",
//    "roles": "user,customer",
//    "createdBy": "admin"
//  }
//}
//
//
//EXAMPLE 4: GET ALL USERS
//================================
//
//Request:
//GET /api/users
//Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5c...
//
//Response (200 OK):
//{
//  "success": true,
//  "count": 2,
//  "data": [
//    {
//      "id": 1,
//      "keycloakId": "a1b2c3d4-e5f6-4a7b-8c9d-e0f1a2b3c4d5",
//      "username": "alice.johnson",
//      "email": "alice.johnson@example.com",
//      "firstName": "Alice",
//      "lastName": "Johnson",
//      "roles": "user,customer",
//      "createdBy": "admin"
//    },
//    {
//      "id": 2,
//      "keycloakId": "b2c3d4e5-f6a7-4b8c-9d0e-f1a2b3c4d5e6",
//      "username": "bob.smith",
//      "email": "bob.smith@example.com",
//      "firstName": "Bob",
//      "lastName": "Smith",
//      "roles": "user",
//      "createdBy": "admin"
//    }
//  ]
//}
//
//
//EXAMPLE 5: UPDATE USER
//================================
//
//Request:
//PUT /api/users/1
//Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5c...
//Content-Type: application/json
//
//{
//  "email": "alice.newemail@example.com",
//  "firstName": "Alicia",
//  "lastName": "Johnson",
//  "roles": "admin,user"
//}
//
//*/
//
//}
//    System.out.println("User: " + user.getUsername() + " - " + user.getEmail());
//for (User user : allUsers) {
//List<User> allUsers = userService.getAllUsers();
//
//Example 5: Get all users
//
//
//userService.resetPassword(1L, "NewPassword123!@#");
//
//Example 4: Reset password
//
//
//User updatedUser = userService.updateUser(1L, userDetails);
//
//userDetails.setRoles("admin");
//userDetails.setFirstName("Updated");
//userDetails.setEmail("newemail@example.com");
//User userDetails = new User();
//
//Example 3: Update user
//
//
//}
//    System.out.println("User: " + user.get().getUsername());
//if (user.isPresent()) {
//Optional<User> user = userService.getUserByKeycloakId("a1b2c3d4-e5f6-4a7b-8c9d-e0f1a2b3c4d5");
//
//Example 2: Get user by Keycloak ID
//
//
//User createdUser = userService.createUser(newUser, "SecurePassword123!");
//
//newUser.setCreatedBy("admin");
//newUser.setRoles("user");
//newUser.setLastName("Doe");
//newUser.setFirstName("John");
//newUser.setEmail("john@example.com");
//newUser.setUsername("john.doe");
//User newUser = new User();
//// Create user
//
//private UserService userService;
//@Autowired
//
//Example 1: Using UserService in another service/controller
//
//================================
//JAVA CODE EXAMPLES
//
//
//}
//  "message": "Error updating user: Unable to connect to Keycloak server"
//  "success": false,
//{
//3. Server Error (500):
//
//}
//  "message": "Failed to create user in Keycloak: User already exists"
//  "success": false,
//{
//2. Invalid Input (400):
//
//}
//  "message": "User not found with id: 999"
//  "success": false,
//{
//1. User Not Found (404):
//
//================================
//ERROR RESPONSES
//
//
//}
//  "message": "Password reset successfully"
//  "success": true,
//{
//Response (200 OK):
//
//}
//  "newPassword": "NewSecurePassword456!@#"
//{
//
//Content-Type: application/json
//Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5c...
//POST /api/users/1/reset-password
//Request:
//
//================================
//EXAMPLE 7: RESET PASSWORD
//
//
//Note: User is deleted from both Keycloak and the database
//
//}
//  "message": "User deleted successfully"
//  "success": true,
//{
//Response (200 OK):
//
//Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5c...
//DELETE /api/users/1
//Request:
//
//================================
//EXAMPLE 6: DELETE USER
//
//
//}
//  }
//    "createdBy": "admin"
//    "roles": "admin,user",
//    "lastName": "Johnson",
//    "firstName": "Alicia",
//    "email": "alice.newemail@example.com",
//    "username": "alice.johnson",
//    "keycloakId": "a1b2c3d4-e5f6-4a7b-8c9d-e0f1a2b3c4d5",
//    "id": 1,
//  "data": {
//  "message": "User updated successfully",
//  "success": true,
//{
//Response (200 OK):
//
