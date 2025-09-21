package org.example.graphqlcrudapi.service;

import org.example.graphqlcrudapi.dto.UserInput;
import org.example.graphqlcrudapi.entity.User;
import org.example.graphqlcrudapi.exception.UserNotFoundException;
import org.example.graphqlcrudapi.exception.DuplicateEmailException;
import org.example.graphqlcrudapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Search users by name
     */
    @Transactional(readOnly = true)
    public List<User> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Search users by multiple criteria
     */
    @Transactional(readOnly = true)
    public List<User> searchUsers(String name, String email, String phone) {
        return userRepository.searchUsers(name, email, phone);
    }

    /**
     * Create a new user
     */
    public User createUser(UserInput userInput) {
        // Check if email already exists
        if (userRepository.existsByEmail(userInput.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + userInput.getEmail());
        }

        User user = new User();
        user.setName(userInput.getName());
        user.setEmail(userInput.getEmail());
        user.setPhone(userInput.getPhone());
        user.setAddress(userInput.getAddress());

        return userRepository.save(user);
    }

    /**
     * Update an existing user
     */
    public User updateUser(Long id, UserInput userInput) {
        User existingUser = getUserById(id);

        // Check if email is being changed and if new email already exists
        if (!existingUser.getEmail().equals(userInput.getEmail()) &&
                userRepository.existsByEmail(userInput.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + userInput.getEmail());
        }

        existingUser.setName(userInput.getName());
        existingUser.setEmail(userInput.getEmail());
        existingUser.setPhone(userInput.getPhone());
        existingUser.setAddress(userInput.getAddress());

        return userRepository.save(existingUser);
    }

    /**
     * Delete user by ID
     */
    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
        return true;
    }

    /**
     * Get user count
     */
    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count();
    }

    /**
     * Check if user exists by ID
     */
    @Transactional(readOnly = true)
    public boolean userExists(Long id) {
        return userRepository.existsById(id);
    }

    /**
     * Check if email exists
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}