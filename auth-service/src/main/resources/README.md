# Authentication Service

This project is a Spring Boot application that implements JWT authentication with role-based access control. It provides a secure way to manage user authentication and authorization.

## Features

- User registration
- User login
- JWT token generation
- Role-based access control
- Secure REST API

## Technologies Used

- Spring Boot
- Spring Security
- JWT (JSON Web Tokens)
- Spring Data JPA
- H2 Database (or any other database of your choice)

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven

### Installation

1. Clone the repository:
   ```
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```
   cd auth-service
   ```

3. Build the project:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   mvn spring-boot:run
   ```

### API Endpoints

- **POST /api/auth/register**: Register a new user
- **POST /api/auth/login**: Authenticate a user and return a JWT token

### Configuration

Update the `src/main/resources/application.properties` file with your database connection settings and JWT secret.

## Testing

Unit tests are included in the `src/test/java/com/example/authservice` directory. You can run the tests using:
```
mvn test
```

## License

This project is licensed under the MIT License. See the LICENSE file for details.