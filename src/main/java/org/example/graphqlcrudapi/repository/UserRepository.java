package org.example.graphqlcrudapi.repository;

import org.example.graphqlcrudapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find users by name containing (case insensitive)
     */
    List<User> findByNameContainingIgnoreCase(String name);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Find users by phone number
     */
    List<User> findByPhone(String phone);

    /**
     * Custom query to search users by multiple criteria
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:name IS NULL OR LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:phone IS NULL OR u.phone LIKE CONCAT('%', :phone, '%'))")
    List<User> searchUsers(@Param("name") String name,
                           @Param("email") String email,
                           @Param("phone") String phone);

    /**
     * Count users by name pattern
     */
    @Query("SELECT COUNT(u) FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :namePattern, '%'))")
    long countByNamePattern(@Param("namePattern") String namePattern);
}