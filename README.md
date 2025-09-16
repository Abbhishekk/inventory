# Inventory Management System

A Spring Boot microservice for managing inventory with secure, role-based access.

## Features

- **CRUD Operations**: Manage products, categories, and suppliers.
- **Stock Alerts**: Get notified when stock falls below reorder levels.
- **Role-Based Security**: JWT authentication with user roles.
- **Database**: Uses PostgreSQL for persistent storage.

## Tech Stack

- Java, Spring Boot
- PostgreSQL
- Spring Security (JWT)
- Maven

## Endpoints

- `/products` - CRUD for products
- `/categories` - CRUD for categories
- `/suppliers` - CRUD for suppliers
- `/auth` - Authentication endpoints

## Security

- All endpoints (except `/auth`) require a valid JWT token.
- Role-based access control for sensitive operations.

## Getting Started

1. **Clone the repository**
2. **Configure `application.properties`** with your PostgreSQL and JWT settings.
3. **Build and run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
4. **API Usage:**  
   Use tools like Postman to interact with the API. Authenticate via `/auth/login` to receive a JWT.

## Demo

- Show CRUD operations for products, categories, and suppliers.
- Demonstrate stock alert and reorder level notifications.
- Highlight secure access with JWT and roles.

---
