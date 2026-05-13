# Policy Lifecycle Manager

## Project Overview

Policy Lifecycle Manager is a comprehensive Spring Boot-based backend application designed to manage the complete lifecycle of insurance policies. It provides a robust REST API for creating, reading, updating, and deleting policies, with advanced features like caching, authentication, file uploads, and automated notifications.

### Key Features
- **JWT Authentication**: Secure API endpoints with JSON Web Tokens
- **Redis Caching**: High-performance caching with configurable TTL for improved response times
- **Docker Containerization**: Fully containerized architecture using Docker Compose
- **Swagger/OpenAPI Documentation**: Interactive API documentation and testing interface
- **File Upload System**: Secure file upload and management capabilities
- **Email Notifications**: Automated email notifications for policy events
- **Comprehensive Testing**: Unit and integration tests with JUnit 5 and Mockito

### Technologies Used
- **Spring Boot**: Framework for building the REST API
- **MySQL**: Primary database for policy data persistence
- **Redis**: In-memory data store for caching
- **JWT**: Token-based authentication
- **Swagger/OpenAPI**: API documentation and testing
- **Docker**: Containerization and orchestration
- **File Uploads**: Multipart file handling
- **Email Notifications**: SMTP-based email service
- **JUnit Testing**: Comprehensive test coverage

## Features

- **JWT-Secured APIs**: All endpoints protected with JWT authentication
- **Redis Caching with TTL**: Automatic caching of policy data with 10-minute expiration
- **Dockerized Architecture**: Complete containerization with MySQL, Redis, and backend services
- **Swagger API Documentation**: Interactive API docs accessible via web interface
- **File Upload System**: Support for PDF file uploads with validation and storage
- **Email Notifications**: Automated emails for policy creation and expiry reminders
- **Pagination**: Efficient data retrieval with pageable results
- **Global Exception Handling**: Consistent error responses across all endpoints
- **Data Seeding**: Automatic database initialization with sample data
- **Unit and Integration Testing**: Comprehensive test suite with high coverage

## Architecture

```
Client Applications
       |
       v
Spring Boot REST API (Port 8080)
       |
       +-------------------+
       |                   |
       v                   v
   MySQL Database     Redis Cache
   (Port 3306)        (Port 6379)
       |
       v
  Mail Service (SMTP)
       |
       v
 File Storage System
```

## Tech Stack

- **Java 17**: Programming language
- **Spring Boot 3.2.5**: Framework for building the application
- **Spring Security**: Authentication and authorization
- **Spring Data Redis**: Redis integration for caching
- **Spring Data JPA**: Database access layer
- **MySQL 8**: Relational database
- **Redis 7**: In-memory data store
- **Docker & Docker Compose**: Containerization
- **Maven**: Build and dependency management
- **JUnit 5**: Unit testing framework
- **Mockito**: Mocking framework for tests
- **Swagger/OpenAPI 3**: API documentation

## Prerequisites

Before running this application, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.6+** for building the project
- **Docker Desktop** for containerization
- **Git** for version control

## Environment Variables

The application uses the following environment variables. Create a `.env` file in the backend directory or set them in your environment:

| Variable | Description | Example Value |
|----------|-------------|---------------|
| `DB_URL` | MySQL database connection URL | `jdbc:mysql://localhost:3306/policydb` |
| `DB_USERNAME` | MySQL database username | `root` |
| `DB_PASSWORD` | MySQL database password | `root` |
| `REDIS_HOST` | Redis server hostname | `localhost` |
| `REDIS_PORT` | Redis server port | `6379` |
| `MAIL_USERNAME` | SMTP email username | `yourmail@gmail.com` |
| `MAIL_PASSWORD` | SMTP email password | `yourpassword` |
| `JWT_SECRET` | JWT signing secret key (min 32 chars) | `yourjwtsecretkey0123456789012345` |

## Setup Instructions

### Local Development Setup

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd policy-lifecycle-manager
   ```

2. **Configure environment variables**:
   Create a `.env` file in the `backend/` directory with the required environment variables.

3. **Build the application**:
   ```bash
   cd backend
   mvn clean install
   ```

4. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`.

### Docker Setup

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd policy-lifecycle-manager
   ```

2. **Start the services**:
   ```bash
   docker compose up --build
   ```

This will start all services: MySQL, Redis, and the Spring Boot application.

## Docker Commands

- **Start all services**: `docker compose up --build`
- **Stop all services**: `docker compose down`
- **Stop and remove volumes**: `docker compose down -v`
- **Check running containers**: `docker ps`

## Swagger Documentation

Access the interactive API documentation at:
```
http://localhost:8080/swagger-ui/index.html
```

This provides a complete interface for exploring and testing all API endpoints.

## Redis Verification

To verify Redis caching is working:

1. **Access Redis CLI**:
   ```bash
   docker exec -it policy-redis redis-cli
   ```

2. **Check cache keys**:
   ```bash
   KEYS *
   ```

Expected cache keys after accessing policies:
- `policies::1` (cached policy data)
- Other policy IDs as accessed

## Testing

### Unit and Integration Tests
Run the test suite:
```bash
mvn test
```

### Docker Validation
1. Start containers: `docker compose up --build`
2. Verify services are running: `docker ps`
3. Test API endpoints using Swagger or curl commands

### Redis Validation
1. Access a protected endpoint (e.g., GET /policies/1)
2. Check Redis keys as described above
3. Verify cache hit on subsequent requests

### File Upload Testing
Use the Swagger UI or curl to test file upload endpoints:
```bash
curl -X POST "http://localhost:8080/files/upload" \
  -H "Authorization: Bearer <token>" \
  -F "file=@test.pdf"
```

## Troubleshooting

### Windows Docker Issues
If `localhost:8080` behaves inconsistently on Windows Docker:
- Use `http://127.0.0.1:8080` instead
- This ensures reliable connection to the containerized application

### Common Issues
- **Port conflicts**: Ensure ports 8080, 3306, 6379 are available
- **Database connection**: Verify MySQL container is healthy before starting the backend
- **Redis connection**: Check Redis container status and network connectivity
- **File uploads**: Ensure upload directory has proper permissions

## Future Improvements

- **CI/CD Pipeline**: Automated build, test, and deployment pipeline
- **Role-Based Access Control**: Enhanced authorization with user roles and permissions
- **Cloud Deployment**: Container orchestration with Kubernetes
- **Monitoring Dashboards**: Application metrics and health monitoring
- **API Rate Limiting**: Request throttling and abuse prevention
- **Audit Logging**: Comprehensive logging of policy changes
- **Multi-tenant Support**: Database isolation for multiple organizations
- **Advanced Caching**: Cache invalidation strategies and distributed caching