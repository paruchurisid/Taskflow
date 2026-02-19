# TaskFlow

TaskFlow is a task management REST API built with Java and Spring Boot, demonstrating modern server-side application development and RESTful API design.

## Features

- **CRUD Operations**: Create, read, update, and delete tasks
- **Task Management**: Mark tasks as completed/incomplete
- **Filtering**: Filter tasks by completion status and title
- **Pagination & Sorting**: Built-in pagination and sorting support
- **Validation**: Input validation with meaningful error messages
- **Error Handling**: Comprehensive error handling with custom exceptions
- **Layered Architecture**: Clean separation of concerns (Controller, Service, Repository)
- **DTO Pattern**: Request/Response DTOs for API stability
- **Audit Fields**: Automatic creation and modification timestamps

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Hibernate**
- **H2 Database** (development)
- **PostgreSQL** (production ready)
- **Maven**

## Project Structure

```
src/main/java/com/taskflow/
├── TaskFlowApplication.java      # Main Spring Boot application
├── controller/
│   └── TaskController.java      # REST API endpoints
├── service/
│   └── TaskService.java          # Business logic layer
├── repository/
│   └── TaskRepository.java       # Data access layer
├── entity/
│   └── Task.java                 # JPA entity
├── dto/
│   ├── TaskRequestDto.java       # Request DTO
│   ├── TaskResponseDto.java      # Response DTO
│   └── ErrorResponseDto.java     # Error response DTO
└── exception/
    ├── ResourceNotFoundException.java
    └── GlobalExceptionHandler.java
```

## API Endpoints

### Create Task
```http
POST /api/tasks
Content-Type: application/json

{
  "title": "Complete project",
  "description": "Finish the TaskFlow project",
  "dueDate": "2024-12-31T23:59:59"
}
```

### Get All Tasks
```http
GET /api/tasks?isCompleted=false&title=project&page=0&size=10&sort=createdAt,desc
```

Query Parameters:
- `isCompleted` (optional): Filter by completion status (true/false)
- `title` (optional): Filter by title (case-insensitive partial match)
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 10)
- `sort` (optional): Sort field and direction (e.g., "createdAt,desc")

### Get Task by ID
```http
GET /api/tasks/{id}
```

### Update Task
```http
PUT /api/tasks/{id}
Content-Type: application/json

{
  "title": "Updated title",
  "description": "Updated description",
  "dueDate": "2024-12-31T23:59:59"
}
```

### Mark Task as Completed
```http
PATCH /api/tasks/{id}/complete
```

### Mark Task as Incomplete
```http
PATCH /api/tasks/{id}/incomplete
```

### Delete Task
```http
DELETE /api/tasks/{id}
```

## Running the Application

### Prerequisites
- Java 17 or higher
- Maven 3.6+

### Development Mode (H2 Database)
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Production Mode (PostgreSQL)
1. Update `application-prod.properties` with your PostgreSQL credentials
2. Run with production profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## H2 Console (Development)

Access the H2 console at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:taskflowdb`
- Username: `sa`
- Password: (empty)

## Example API Calls

### Create a Task
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Spring Boot",
    "description": "Complete Spring Boot tutorial",
    "dueDate": "2024-12-31T23:59:59"
  }'
```

### Get All Tasks
```bash
curl http://localhost:8080/api/tasks
```

### Get Incomplete Tasks
```bash
curl http://localhost:8080/api/tasks?isCompleted=false
```

### Update Task
```bash
curl -X PUT http://localhost:8080/api/tasks/1 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Updated Task",
    "description": "New description"
  }'
```

### Mark Task as Completed
```bash
curl -X PATCH http://localhost:8080/api/tasks/1/complete
```

### Delete Task
```bash
curl -X DELETE http://localhost:8080/api/tasks/1
```

## Error Responses

The API returns standardized error responses:

```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 404,
  "error": "Resource Not Found",
  "message": "Task with id 1 not found",
  "path": "/api/tasks/1"
}
```

For validation errors:
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "Invalid input provided",
  "path": "/api/tasks",
  "validationErrors": [
    "title: Title is required"
  ]
}
```

## Testing

Run tests with:
```bash
mvn test
```

## License

This project is open source and available for educational purposes.

