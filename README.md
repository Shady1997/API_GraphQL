# GraphQL CRUD API with Java Spring Boot

A complete GraphQL-based CRUD (Create, Read, Update, Delete) API built with Java, Spring Boot, and H2 database with comprehensive testing support using Postman and RestAssured.

## Features

- ✅ Full CRUD operations for User entity
- ✅ GraphQL API with comprehensive schema
- ✅ Input validation and error handling
- ✅ Custom exception handling
- ✅ H2 in-memory database for development
- ✅ GraphiQL interface for testing
- ✅ Unit tests with Spring Boot Test
- ✅ Initial data loading
- ✅ Search and filtering capabilities
- ✅ Proper layered architecture
- ✅ Postman collection for API testing
- ✅ RestAssured integration tests

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring GraphQL**
- **H2 Database**
- **Maven**
- **JUnit 5**
- **RestAssured** (for API testing)
- **GraphQL Java Extended Scalars**

## Project Structure

```
src/
├── main/
│   ├── java/com/example/graphqlcrudapi/
│   │   ├── GraphqlCrudApiApplication.java      # Main application class
│   │   ├── config/
│   │   │   ├── DataLoader.java                 # Initial data setup
│   │   │   ├── GlobalExceptionHandler.java     # Error handling
│   │   │   └── GraphQLConfig.java              # GraphQL configuration
│   │   ├── controller/
│   │   │   └── UserController.java             # GraphQL resolvers
│   │   ├── dto/
│   │   │   └── UserInput.java                  # Input DTOs
│   │   ├── entity/
│   │   │   └── User.java                       # JPA entity
│   │   ├── exception/
│   │   │   ├── UserNotFoundException.java      # Custom exceptions
│   │   │   └── DuplicateEmailException.java
│   │   ├── repository/
│   │   │   └── UserRepository.java             # Data access layer
│   │   └── service/
│   │       └── UserService.java                # Business logic
│   └── resources/
│       ├── application.yml                     # Configuration
│       └── graphql/
│           └── schema.graphqls                 # GraphQL schema
└── test/
    └── java/com/example/graphqlcrudapi/
        └── controller/
            └── UserControllerTest.java         # Unit tests
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd graphql-crud-api
   ```

2. **Build the project**
   ```bash
   mvn clean compile
   ```

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**
   - GraphiQL Interface: http://localhost:8089/graphiql
   - GraphQL Endpoint: http://localhost:8089/graphql
   - H2 Console: http://localhost:8089/h2-console

### H2 Database Configuration

- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: (empty)

## API Testing Guide

### Prerequisites for API Testing

1. **Install Postman**: Download from [postman.com](https://www.postman.com/)
2. **RestAssured Dependencies**: Already included in the project for automated testing

### Using Postman for GraphQL Testing

#### 1. Setting Up Postman

1. **Create a new request** in Postman
2. **Set method to POST**
3. **Set URL to**: `http://localhost:8089/graphql`
4. **Set Headers**:
   - `Content-Type: application/json`
   - `Accept: application/json`

#### 2. GraphQL Request Structure

All GraphQL requests use the same POST endpoint with a JSON body:
```json
{
  "query": "YOUR_QUERY_OR_MUTATION_HERE",
  "variables": {
    "variable1": "value1",
    "variable2": "value2"
  }
}
```

---

## Complete CRUD Operations Guide

### 1. CREATE USER

#### Postman Request:
```json
{
  "query": "mutation CreateUser($input: UserInput!) { createUser(input: $input) { id name email phone address createdAt updatedAt } }",
  "variables": {
    "input": {
      "name": "John Doe",
      "email": "john.doe@example.com",
      "phone": "+1234567890",
      "address": "123 Main St, New York, NY"
    }
  }
}
```

#### Expected Response:
```json
{
  "data": {
    "createUser": {
      "id": "11",
      "name": "John Doe",
      "email": "john.doe@example.com",
      "phone": "+1234567890",
      "address": "123 Main St, New York, NY",
      "createdAt": "2025-09-21T10:30:00.123456",
      "updatedAt": "2025-09-21T10:30:00.123456"
    }
  }
}
```

#### RestAssured Test:
```java
@Test
void createUserTest() {
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
    
    Map<String, Object> variables = Map.of(
        "input", Map.of(
            "name", "John Doe",
            "email", "john.doe@example.com",
            "phone", "+1234567890",
            "address", "123 Main St, New York, NY"
        )
    );
    
    given()
        .contentType(ContentType.JSON)
        .body(Map.of("query", mutation, "variables", variables))
    .when()
        .post("/graphql")
    .then()
        .statusCode(200)
        .body("data.createUser.name", equalTo("John Doe"))
        .body("data.createUser.email", equalTo("john.doe@example.com"));
}
```

### 2. READ OPERATIONS

#### A. Get All Users

##### Postman Request:
```json
{
  "query": "query { getAllUsers { id name email phone address createdAt updatedAt } }"
}
```

##### Expected Response:
```json
{
  "data": {
    "getAllUsers": [
      {
        "id": "1",
        "name": "John Doe",
        "email": "john.doe@example.com",
        "phone": "+1234567890",
        "address": "123 Main St, New York, NY",
        "createdAt": "2025-09-21T10:00:00.123456",
        "updatedAt": "2025-09-21T10:00:00.123456"
      }
    ]
  }
}
```

##### RestAssured Test:
```java
@Test
void getAllUsersTest() {
    String query = "query { getAllUsers { id name email phone address } }";
    
    given()
        .contentType(ContentType.JSON)
        .body(Map.of("query", query))
    .when()
        .post("/graphql")
    .then()
        .statusCode(200)
        .body("data.getAllUsers", hasSize(greaterThan(0)))
        .body("data.getAllUsers[0].id", notNullValue());
}
```

#### B. Get User by ID

##### Postman Request:
```json
{
  "query": "query GetUser($id: ID!) { getUserById(id: $id) { id name email phone address createdAt updatedAt } }",
  "variables": {
    "id": "1"
  }
}
```

##### Expected Response:
```json
{
  "data": {
    "getUserById": {
      "id": "1",
      "name": "John Doe",
      "email": "john.doe@example.com",
      "phone": "+1234567890",
      "address": "123 Main St, New York, NY",
      "createdAt": "2025-09-21T10:00:00.123456",
      "updatedAt": "2025-09-21T10:00:00.123456"
    }
  }
}
```

##### RestAssured Test:
```java
@Test
void getUserByIdTest() {
    String query = """
        query GetUser($id: ID!) {
            getUserById(id: $id) {
                id
                name
                email
            }
        }
        """;
    
    given()
        .contentType(ContentType.JSON)
        .body(Map.of("query", query, "variables", Map.of("id", "1")))
    .when()
        .post("/graphql")
    .then()
        .statusCode(200)
        .body("data.getUserById.id", equalTo("1"))
        .body("data.getUserById.name", notNullValue());
}
```

#### C. Search Users by Name

##### Postman Request:
```json
{
  "query": "query SearchUsers($name: String!) { searchUsersByName(name: $name) { id name email phone address } }",
  "variables": {
    "name": "John"
  }
}
```

##### Expected Response:
```json
{
  "data": {
    "searchUsersByName": [
      {
        "id": "1",
        "name": "John Doe",
        "email": "john.doe@example.com",
        "phone": "+1234567890",
        "address": "123 Main St, New York, NY"
      }
    ]
  }
}
```

#### D. Advanced Search

##### Postman Request:
```json
{
  "query": "query SearchUsers($name: String, $email: String, $phone: String) { searchUsers(name: $name, email: $email, phone: $phone) { id name email phone address } }",
  "variables": {
    "name": "John",
    "email": "example.com",
    "phone": null
  }
}
```

#### E. Get User Count

##### Postman Request:
```json
{
  "query": "query { getUserCount }"
}
```

##### Expected Response:
```json
{
  "data": {
    "getUserCount": 10
  }
}
```

### 3. UPDATE USER

#### Postman Request:
```json
{
  "query": "mutation UpdateUser($id: ID!, $input: UserInput!) { updateUser(id: $id, input: $input) { id name email phone address updatedAt } }",
  "variables": {
    "id": "1",
    "input": {
      "name": "John Smith Updated",
      "email": "john.smith.updated@example.com",
      "phone": "+9876543210",
      "address": "456 Updated Street, Los Angeles, CA"
    }
  }
}
```

#### Expected Response:
```json
{
  "data": {
    "updateUser": {
      "id": "1",
      "name": "John Smith Updated",
      "email": "john.smith.updated@example.com",
      "phone": "+9876543210",
      "address": "456 Updated Street, Los Angeles, CA",
      "updatedAt": "2025-09-21T11:00:00.123456"
    }
  }
}
```

#### RestAssured Test:
```java
@Test
void updateUserTest() {
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
    
    Map<String, Object> variables = Map.of(
        "id", "1",
        "input", Map.of(
            "name", "John Smith Updated",
            "email", "john.updated@example.com",
            "phone", "+9876543210",
            "address", "456 Updated Street"
        )
    );
    
    given()
        .contentType(ContentType.JSON)
        .body(Map.of("query", mutation, "variables", variables))
    .when()
        .post("/graphql")
    .then()
        .statusCode(200)
        .body("data.updateUser.name", equalTo("John Smith Updated"))
        .body("data.updateUser.email", equalTo("john.updated@example.com"));
}
```

### 4. DELETE USER

#### Postman Request:
```json
{
  "query": "mutation DeleteUser($id: ID!) { deleteUser(id: $id) }",
  "variables": {
    "id": "1"
  }
}
```

#### Expected Response:
```json
{
  "data": {
    "deleteUser": true
  }
}
```

#### RestAssured Test:
```java
@Test
void deleteUserTest() {
    String mutation = """
        mutation DeleteUser($id: ID!) {
            deleteUser(id: $id)
        }
        """;
    
    given()
        .contentType(ContentType.JSON)
        .body(Map.of("query", mutation, "variables", Map.of("id", "1")))
    .when()
        .post("/graphql")
    .then()
        .statusCode(200)
        .body("data.deleteUser", equalTo(true));
}
```

### 5. UTILITY QUERIES

#### A. Check if User Exists

##### Postman Request:
```json
{
  "query": "query CheckUser($id: ID!) { userExists(id: $id) }",
  "variables": {
    "id": "1"
  }
}
```

#### B. Check if Email Exists

##### Postman Request:
```json
{
  "query": "query CheckEmail($email: String!) { emailExists(email: $email) }",
  "variables": {
    "email": "john.doe@example.com"
  }
}
```

---

## Error Handling Examples

### 1. User Not Found Error

#### Postman Request:
```json
{
  "query": "query GetUser($id: ID!) { getUserById(id: $id) { id name } }",
  "variables": {
    "id": "999"
  }
}
```

#### Expected Error Response:
```json
{
  "errors": [
    {
      "message": "User not found with id: 999",
      "locations": [
        {
          "line": 1,
          "column": 25
        }
      ],
      "path": ["getUserById"],
      "extensions": {
        "classification": "NOT_FOUND"
      }
    }
  ],
  "data": {
    "getUserById": null
  }
}
```

### 2. Duplicate Email Error

#### Postman Request:
```json
{
  "query": "mutation CreateUser($input: UserInput!) { createUser(input: $input) { id } }",
  "variables": {
    "input": {
      "name": "Duplicate User",
      "email": "john.doe@example.com",
      "phone": "+1111111111"
    }
  }
}
```

#### Expected Error Response:
```json
{
  "errors": [
    {
      "message": "Email already exists: john.doe@example.com",
      "locations": [
        {
          "line": 1,
          "column": 43
        }
      ],
      "path": ["createUser"],
      "extensions": {
        "classification": "BAD_REQUEST"
      }
    }
  ],
  "data": {
    "createUser": null
  }
}
```

### 3. Validation Error

#### Postman Request:
```json
{
  "query": "mutation CreateUser($input: UserInput!) { createUser(input: $input) { id } }",
  "variables": {
    "input": {
      "name": "A",
      "email": "invalid-email",
      "phone": "+1234567890123456789"
    }
  }
}
```

#### Expected Error Response:
```json
{
  "errors": [
    {
      "message": "Validation failed: Name must be between 2 and 100 characters; Email must be valid; Phone number cannot exceed 15 characters; ",
      "locations": [
        {
          "line": 1,
          "column": 43
        }
      ],
      "path": ["createUser"],
      "extensions": {
        "classification": "BAD_REQUEST"
      }
    }
  ],
  "data": {
    "createUser": null
  }
}
```

---

## Complete RestAssured Test Suite

Create a new test class `GraphQLApiIntegrationTest.java`:

```java
package com.example.graphqlcrudapi.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class GraphQLApiIntegrationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "";
    }

    @Test
    @Order(1)
    void shouldGetAllUsers() {
        String query = "query { getAllUsers { id name email phone address } }";
        
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", query))
        .when()
            .post("/graphql")
        .then()
            .statusCode(200)
            .body("data.getAllUsers", hasSize(10))
            .body("data.getAllUsers[0].id", notNullValue())
            .body("data.getAllUsers[0].name", notNullValue());
    }

    @Test
    @Order(2)
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
        
        Map<String, Object> variables = Map.of(
            "input", Map.of(
                "name", "RestAssured User",
                "email", "restassured@example.com",
                "phone", "+1111111111",
                "address", "123 RestAssured St"
            )
        );
        
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", mutation, "variables", variables))
        .when()
            .post("/graphql")
        .then()
            .statusCode(200)
            .body("data.createUser.name", equalTo("RestAssured User"))
            .body("data.createUser.email", equalTo("restassured@example.com"))
            .body("data.createUser.phone", equalTo("+1111111111"));
    }

    @Test
    @Order(3)
    void shouldGetUserById() {
        String query = """
            query GetUser($id: ID!) {
                getUserById(id: $id) {
                    id
                    name
                    email
                }
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", query, "variables", Map.of("id", "1")))
        .when()
            .post("/graphql")
        .then()
            .statusCode(200)
            .body("data.getUserById.id", equalTo("1"))
            .body("data.getUserById.name", notNullValue())
            .body("data.getUserById.email", notNullValue());
    }

    @Test
    @Order(4)
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
        
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", query, "variables", Map.of("name", "John")))
        .when()
            .post("/graphql")
        .then()
            .statusCode(200)
            .body("data.searchUsersByName", hasSize(greaterThan(0)))
            .body("data.searchUsersByName[0].name", containsString("John"));
    }

    @Test
    @Order(5)
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
        
        Map<String, Object> variables = Map.of(
            "id", "1",
            "input", Map.of(
                "name", "Updated User",
                "email", "updated@example.com",
                "phone", "+9999999999",
                "address", "999 Updated Ave"
            )
        );
        
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", mutation, "variables", variables))
        .when()
            .post("/graphql")
        .then()
            .statusCode(200)
            .body("data.updateUser.name", equalTo("Updated User"))
            .body("data.updateUser.email", equalTo("updated@example.com"));
    }

    @Test
    @Order(6)
    void shouldGetUserCount() {
        String query = "query { getUserCount }";
        
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", query))
        .when()
            .post("/graphql")
        .then()
            .statusCode(200)
            .body("data.getUserCount", greaterThan(10));
    }

    @Test
    @Order(7)
    void shouldDeleteUser() {
        String mutation = """
            mutation DeleteUser($id: ID!) {
                deleteUser(id: $id)
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", mutation, "variables", Map.of("id", "1")))
        .when()
            .post("/graphql")
        .then()
            .statusCode(200)
            .body("data.deleteUser", equalTo(true));
    }

    @Test
    @Order(8)
    void shouldReturnErrorForNonExistentUser() {
        String query = """
            query GetUser($id: ID!) {
                getUserById(id: $id) {
                    id
                    name
                }
            }
            """;
        
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", query, "variables", Map.of("id", "999")))
        .when()
            .post("/graphql")
        .then()
            .statusCode(200)
            .body("errors", hasSize(1))
            .body("errors[0].message", containsString("User not found"))
            .body("data.getUserById", nullValue());
    }

    @Test
    @Order(9)
    void shouldReturnValidationError() {
        String mutation = """
            mutation CreateUser($input: UserInput!) {
                createUser(input: $input) {
                    id
                }
            }
            """;
        
        Map<String, Object> variables = Map.of(
            "input", Map.of(
                "name", "A", // Too short
                "email", "invalid-email", // Invalid format
                "phone", "+12345678901234567890" // Too long
            )
        );
        
        given()
            .contentType(ContentType.JSON)
            .body(Map.of("query", mutation, "variables", variables))
        .when()
            .post("/graphql")
        .then()
            .statusCode(200)
            .body("errors", hasSize(1))
            .body("errors[0].message", containsString("Validation failed"));
    }
}
```

---

## Postman Collection Setup

### 1. Import Environment Variables

Create a Postman environment with these variables:
- `base_url`: `http://localhost:8089`
- `user_id`: `1` (for testing)
- `test_email`: `test@example.com`

### 2. Pre-request Scripts

For dynamic testing, add this pre-request script:
```javascript
// Generate random user data
pm.globals.set("random_name", "User_" + Math.floor(Math.random() * 1000));
pm.globals.set("random_email", "user" + Math.floor(Math.random() * 1000) + "@example.com");
pm.globals.set("random_phone", "+1" + Math.floor(Math.random() * 1000000000));
```

### 3. Test Scripts

Add this test script to verify responses:
```javascript
pm.test("Status code is 200", function () {
    pm.response.to.have.status(200);
});

pm.test("Response has data", function () {
    const jsonData = pm.response.json();
    pm.expect(jsonData).to.have.property('data');
});

pm.test("No GraphQL errors", function () {
    const jsonData = pm.response.json();
    if (jsonData.errors) {
        pm.expect(jsonData.errors).to.have.length(0);
    }
});
```

---

## Testing Best Practices

### 1. Test Order
- Always test CREATE before UPDATE/DELETE
- Test READ operations first to understand data structure
- Test error scenarios after successful operations

### 2. Data Management
- Use unique identifiers for test data
- Clean up test data after tests
- Use database transactions in tests when possible

### 3. Assertions
- Verify both success and error responses
- Check response structure and data types
- Validate business logic constraints

### 4. Environment Setup
- Use different databases for testing
- Set up proper test data fixtures
- Configure appropriate logging levels

#### Get All Users
```graphql
query {
  getAllUsers {
    id
    name
    email
    phone
    address
    createdAt
    updatedAt
  }
}
```

#### Get User by ID
```graphql
query {
  getUserById(id: "1") {
    id
    name
    email
    phone
    address
  }
}
```

#### Search Users by Name
```graphql
query {
  searchUsersByName(name: "John") {
    id
    name
    email
  }
}
```

#### Advanced Search
```graphql
query {
  searchUsers(name: "John", email: "example.com") {
    id
    name
    email
    phone
  }
}
```

#### Get User Count
```graphql
query {
  getUserCount
}
```

#### Check if User Exists
```graphql
query {
  userExists(id: "1")
  emailExists(email: "john.doe@example.com")
}
```

### Mutations

#### Create User
```graphql
mutation {
  createUser(input: {
    name: "New User"
    email: "newuser@example.com"
    phone: "+1234567890"
    address: "123 New Street"
  }) {
    id
    name
    email
    phone
    address
    createdAt
  }
}
```

#### Update User
```graphql
mutation {
  updateUser(id: "1", input: {
    name: "Updated Name"
    email: "updated@example.com"
    phone: "+9876543210"
    address: "456 Updated Street"
  }) {
    id
    name
    email
    phone
    address
    updatedAt
  }
}
```

#### Delete User
```graphql
mutation {
  deleteUser(id: "1")
}
```

## Data Model

### User Entity
```java
{
        "id": "Long (Primary Key)",
        "name": "String (Required, 2-100 chars)",
        "email": "String (Required, Valid email, Unique)",
        "phone": "String (Optional, Max 15 chars)",
        "address": "String (Optional, Max 500 chars)",
        "createdAt": "DateTime (Auto-generated)",
        "updatedAt": "DateTime (Auto-updated)"
        }
```

## Validation Rules

- **Name**: Required, 2-100 characters
- **Email**: Required, valid email format, unique
- **Phone**: Optional, maximum 15 characters
- **Address**: Optional, maximum 500 characters

## Error Handling

The API provides comprehensive error handling for:

- **Validation Errors**: Invalid input data
- **Not Found Errors**: User does not exist
- **Duplicate Email Errors**: Email already exists
- **Bad Request Errors**: Invalid arguments
- **Internal Server Errors**: Unexpected errors

## Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Test Coverage
The project includes comprehensive unit tests covering:
- All CRUD operations
- Search functionality
- Error scenarios
- Validation rules

## API Examples

### Creating Multiple Users
```graphql
mutation {
  user1: createUser(input: {
    name: "Alice Johnson"
    email: "alice@example.com"
    phone: "+1111111111"
    address: "123 Alice St"
  }) { id name }
  
  user2: createUser(input: {
    name: "Bob Smith"
    email: "bob@example.com"
    phone: "+2222222222"
    address: "456 Bob Ave"
  }) { id name }
}
```

### Batch Queries
```graphql
query {
  allUsers: getAllUsers { id name email }
  userCount: getUserCount
  johnUsers: searchUsersByName(name: "John") { id name }
}
```

## Development Features

- **Hot Reload**: Enabled with Spring Boot DevTools
- **SQL Logging**: Enabled for development debugging
- **GraphiQL**: Interactive GraphQL IDE
- **H2 Console**: Database inspection tool
- **Comprehensive Logging**: Debug information for troubleshooting

## Production Considerations

For production deployment, consider:

1. **Database**: Replace H2 with PostgreSQL/MySQL
2. **Security**: Add authentication and authorization
3. **Caching**: Implement Redis for better performance
4. **Monitoring**: Add metrics and health checks
5. **Rate Limiting**: Implement API rate limiting
6. **Validation**: Enhanced input validation
7. **Documentation**: API documentation generation

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make changes and add tests
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For questions or issues, please create an issue in the repository.