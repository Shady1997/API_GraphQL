package org.example.graphqlcrudapi.controller;

import org.example.graphqlcrudapi.dto.UserInput;
import org.example.graphqlcrudapi.entity.User;
import org.example.graphqlcrudapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

@Controller
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ========== QUERIES ==========

    /**
     * Get all users
     */
    @QueryMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    /**
     * Get user by ID
     */
    @QueryMapping
    public User getUserById(@Argument @NotNull Long id) {
        return userService.getUserById(id);
    }

    /**
     * Get user by email
     */
    @QueryMapping
    public Optional<User> getUserByEmail(@Argument String email) {
        return userService.getUserByEmail(email);
    }

    /**
     * Search users by name
     */
    @QueryMapping
    public List<User> searchUsersByName(@Argument String name) {
        return userService.searchUsersByName(name);
    }

    /**
     * Search users by multiple criteria
     */
    @QueryMapping
    public List<User> searchUsers(@Argument String name,
                                  @Argument String email,
                                  @Argument String phone) {
        return userService.searchUsers(name, email, phone);
    }

    /**
     * Get total user count
     */
    @QueryMapping
    public Long getUserCount() {
        return userService.getUserCount();
    }

    /**
     * Check if user exists by ID
     */
    @QueryMapping
    public Boolean userExists(@Argument @NotNull Long id) {
        return userService.userExists(id);
    }

    /**
     * Check if email exists
     */
    @QueryMapping
    public Boolean emailExists(@Argument String email) {
        return userService.emailExists(email);
    }

    // ========== MUTATIONS ==========

    /**
     * Create a new user
     */
    @MutationMapping
    public User createUser(@Argument @Valid UserInput input) {
        return userService.createUser(input);
    }

    /**
     * Update an existing user
     */
    @MutationMapping
    public User updateUser(@Argument @NotNull Long id, @Argument @Valid UserInput input) {
        return userService.updateUser(id, input);
    }

    /**
     * Delete user by ID
     */
    @MutationMapping
    public Boolean deleteUser(@Argument @NotNull Long id) {
        return userService.deleteUser(id);
    }
}