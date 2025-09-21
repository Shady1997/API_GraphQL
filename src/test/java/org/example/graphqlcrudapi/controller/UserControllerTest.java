package org.example.graphqlcrudapi.controller;

import org.example.graphqlcrudapi.entity.User;
import org.example.graphqlcrudapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        testUser = new User("Test User", "test@example.com", "+1234567890", "123 Test St");
        testUser = userRepository.save(testUser);
    }

    @Test
    void shouldGetAllUsers() {
        String query = """
                query {
                    getAllUsers {
                        id
                        name
                        email
                        phone
                        address
                    }
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("getAllUsers")
                .entityList(User.class)
                .hasSize(1)
                .contains(testUser);
    }

    @Test
    void shouldGetUserById() {
        String query = """
                query GetUser($id: ID!) {
                    getUserById(id: $id) {
                        id
                        name
                        email
                        phone
                        address
                    }
                }
                """;

        graphQlTester.document(query)
                .variable("id", testUser.getId())
                .execute()
                .path("getUserById")
                .entity(User.class)
                .satisfies(user -> {
                    assertThat(user.getId()).isEqualTo(testUser.getId());
                    assertThat(user.getName()).isEqualTo(testUser.getName());
                    assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
                });
    }

    @Test
    void shouldCreateUser() {
        String mutation = """
                mutation CreateUser($input: UserInput!) {
                    createUser(input: $input) {
                        id
                        name
                        email
                        phone
                        address
                    }
                }
                """;

        graphQlTester.document(mutation)
                .variable("input", java.util.Map.of(
                        "name", "New User",
                        "email", "new@example.com",
                        "phone", "+9876543210",
                        "address", "456 New St"
                ))
                .execute()
                .path("createUser")
                .entity(User.class)
                .satisfies(user -> {
                    assertThat(user.getName()).isEqualTo("New User");
                    assertThat(user.getEmail()).isEqualTo("new@example.com");
                    assertThat(user.getPhone()).isEqualTo("+9876543210");
                    assertThat(user.getAddress()).isEqualTo("456 New St");
                });
    }

    @Test
    void shouldUpdateUser() {
        String mutation = """
                mutation UpdateUser($id: ID!, $input: UserInput!) {
                    updateUser(id: $id, input: $input) {
                        id
                        name
                        email
                        phone
                        address
                    }
                }
                """;

        graphQlTester.document(mutation)
                .variable("id", testUser.getId())
                .variable("input", java.util.Map.of(
                        "name", "Updated User",
                        "email", "updated@example.com",
                        "phone", "+1111111111",
                        "address", "789 Updated St"
                ))
                .execute()
                .path("updateUser")
                .entity(User.class)
                .satisfies(user -> {
                    assertThat(user.getId()).isEqualTo(testUser.getId());
                    assertThat(user.getName()).isEqualTo("Updated User");
                    assertThat(user.getEmail()).isEqualTo("updated@example.com");
                });
    }

    @Test
    void shouldDeleteUser() {
        String mutation = """
                mutation DeleteUser($id: ID!) {
                    deleteUser(id: $id)
                }
                """;

        graphQlTester.document(mutation)
                .variable("id", testUser.getId())
                .execute()
                .path("deleteUser")
                .entity(Boolean.class)
                .isEqualTo(true);

        // Verify user is deleted
        assertThat(userRepository.existsById(testUser.getId())).isFalse();
    }

    @Test
    void shouldSearchUsersByName() {
        String query = """
                query SearchUsers($name: String!) {
                    searchUsersByName(name: $name) {
                        id
                        name
                        email
                    }
                }
                """;

        graphQlTester.document(query)
                .variable("name", "Test")
                .execute()
                .path("searchUsersByName")
                .entityList(User.class)
                .hasSize(1);
    }

    @Test
    void shouldGetUserCount() {
        String query = """
                query {
                    getUserCount
                }
                """;

        graphQlTester.document(query)
                .execute()
                .path("getUserCount")
                .entity(Long.class)
                .isEqualTo(1L);
    }

    @Test
    void shouldReturnErrorForNonExistentUser() {
        String query = """
                query GetUser($id: ID!) {
                    getUserById(id: $id) {
                        id
                        name
                    }
                }
                """;

        graphQlTester.document(query)
                .variable("id", 999L)
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.get(0).getMessage()).contains("User not found");
                });
    }

    @Test
    void shouldReturnErrorForDuplicateEmail() {
        String mutation = """
                mutation CreateUser($input: UserInput!) {
                    createUser(input: $input) {
                        id
                        name
                        email
                    }
                }
                """;

        graphQlTester.document(mutation)
                .variable("input", java.util.Map.of(
                        "name", "Duplicate User",
                        "email", testUser.getEmail(), // Using existing email
                        "phone", "+9999999999"
                ))
                .execute()
                .errors()
                .satisfy(errors -> {
                    assertThat(errors).hasSize(1);
                    assertThat(errors.get(0).getMessage()).contains("Email already exists");
                });
    }
}