# Authentication Service

This is a Spring Boot project that implements an authentication service using JWT (JSON Web Tokens) with role-based access control.

## Features

- User registration
- User login
- JWT token generation
- Role-based access control
- Secure endpoints

## Technologies Used

- Spring Boot
- Spring Security
- Spring Data JPA
- H2 Database (or any other database of your choice)
- Maven

## Project Structure

```
auth-service
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── example
│   │   │           └── authservice
│   │   │               ├── AuthServiceApplication.java
│   │   │               ├── config
│   │   │               │   └── SecurityConfig.java
│   │   │               ├── controller
│   │   │               │   └── AuthController.java
│   │   │               ├── model
│   │   │               │   └── User.java
│   │   │               ├── repository
│   │   │               │   └── UserRepository.java
│   │   │               ├── service
│   │   │               │   └── AuthService.java
│   │   │               └── util
│   │   │                   └── JwtUtil.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── README.md
│   └── test
│       └── java
│           └── com
│               └── example
│                   └── authservice
│                       └── AuthServiceApplicationTests.java
├── pom.xml
└── README.md
```

## Getting Started

1. Clone the repository:
   ```
   git clone <repository-url>
   ```

2. Navigate to the project directory:
   ```
   cd auth-service
   ```

3. Build the project using Maven:
   ```
   mvn clean install
   ```

4. Run the application:
   ```
   mvn spring-boot:run
   ```

## API Endpoints

- `POST /api/auth/register`: Register a new user
- `POST /api/auth/login`: Authenticate a user and return a JWT token

## Database Configuration

Configure your database settings in `src/main/resources/application.properties`.

## Testing

Run the tests using:
```
mvn test
```

## License

This project is licensed under the MIT License.