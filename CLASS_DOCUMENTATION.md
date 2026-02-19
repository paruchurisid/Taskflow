# Class Documentation - TaskFlow

This document provides a comprehensive explanation of each Java class and file in the TaskFlow project, organized by package and architectural layer.

---

## Table of Contents

1. [Application Entry Point](#application-entry-point)
2. [Entity Layer](#entity-layer)
3. [DTO Layer](#dto-layer)
4. [Repository Layer](#repository-layer)
5. [Service Layer](#service-layer)
6. [Controller Layer](#controller-layer)
7. [Exception Handling Layer](#exception-handling-layer)

---

## Application Entry Point

### `TaskFlowApplication.java`
**Location:** `src/main/java/com/taskflow/TaskFlowApplication.java`

**Purpose:** This is the main entry point of the Spring Boot application. It contains the `main` method that bootstraps the entire application.

**Key Components:**
- **`@SpringBootApplication`**: This annotation is a convenience annotation that combines:
  - `@Configuration`: Marks the class as a source of bean definitions
  - `@EnableAutoConfiguration`: Enables Spring Boot's auto-configuration mechanism
  - `@ComponentScan`: Enables component scanning to find and register Spring components

**Functionality:**
- When the application starts, Spring Boot scans the package and sub-packages for components (controllers, services, repositories, etc.)
- It automatically configures the application context, sets up the embedded server (Tomcat), and initializes the database connection
- The `main` method calls `SpringApplication.run()`, which starts the application server on port 8080 (default)

**Usage:** Run this class to start the entire REST API server.

---

## Entity Layer

### `Task.java`
**Location:** `src/main/java/com/taskflow/entity/Task.java`

**Purpose:** This is the JPA entity class that represents a Task in the database. It maps to the `tasks` table and defines the structure of a task object.

**Key Annotations:**
- **`@Entity`**: Marks this class as a JPA entity, making it eligible for persistence
- **`@Table(name = "tasks")`**: Specifies the database table name
- **`@Id`**: Marks the `id` field as the primary key
- **`@GeneratedValue(strategy = GenerationType.IDENTITY)`**: Auto-generates the ID using database identity column
- **`@Column`**: Maps fields to specific database columns with constraints
- **`@CreationTimestamp`**: Automatically sets the timestamp when the entity is first persisted
- **`@UpdateTimestamp`**: Automatically updates the timestamp whenever the entity is modified
- **`@NotBlank`** and **`@Size`**: Validation constraints that ensure data integrity

**Fields:**
- **`id`** (Long): Primary key, auto-generated
- **`title`** (String): Task title, required, max 200 characters
- **`description`** (String): Task description, optional, max 1000 characters
- **`isCompleted`** (Boolean): Completion status, defaults to false
- **`dueDate`** (LocalDateTime): Optional due date for the task
- **`createdAt`** (LocalDateTime): Timestamp when task was created (auto-managed)
- **`updatedAt`** (LocalDateTime): Timestamp when task was last updated (auto-managed)

**Purpose in Architecture:** This is the domain model that directly represents the database schema. It's used internally by the repository layer but is never exposed directly to API clients (DTOs are used instead for API communication).

---

## DTO Layer

DTOs (Data Transfer Objects) are used to transfer data between the API layer and clients. They prevent exposing internal entity structure and provide a stable API contract.

### `TaskRequestDto.java`
**Location:** `src/main/java/com/taskflow/dto/TaskRequestDto.java`

**Purpose:** Represents the data structure for creating or updating a task when received from API clients.

**Key Features:**
- Contains only the fields that clients can send (title, description, dueDate)
- Does NOT include `id`, `isCompleted`, `createdAt`, or `updatedAt` (these are managed by the system)
- Uses validation annotations (`@NotBlank`, `@Size`) to ensure incoming data is valid
- Uses `@JsonFormat` to specify the expected date format for JSON parsing

**Fields:**
- **`title`** (String): Required, 1-200 characters
- **`description`** (String): Optional, max 1000 characters
- **`dueDate`** (LocalDateTime): Optional, formatted as "yyyy-MM-dd'T'HH:mm:ss"

**Usage:** This DTO is used in POST and PUT endpoints to receive task data from clients.

---

### `TaskResponseDto.java`
**Location:** `src/main/java/com/taskflow/dto/TaskResponseDto.java`

**Purpose:** Represents the data structure returned to API clients when they request task information.

**Key Features:**
- Contains all task information including system-managed fields (id, isCompleted, timestamps)
- Uses `@JsonFormat` to ensure consistent date formatting in JSON responses
- Provides a clean, stable API contract that doesn't expose internal entity structure

**Fields:**
- **`id`** (Long): Task identifier
- **`title`** (String): Task title
- **`description`** (String): Task description
- **`isCompleted`** (Boolean): Completion status
- **`dueDate`** (LocalDateTime): Due date
- **`createdAt`** (LocalDateTime): Creation timestamp
- **`updatedAt`** (LocalDateTime): Last update timestamp

**Usage:** This DTO is returned in all GET endpoints and after successful POST/PUT/PATCH operations.

---

### `ErrorResponseDto.java`
**Location:** `src/main/java/com/taskflow/dto/ErrorResponseDto.java`

**Purpose:** Provides a standardized structure for error responses returned to API clients.

**Key Features:**
- Consistent error format across all error scenarios
- Includes timestamp, HTTP status, error type, message, and request path
- Can include validation errors list for input validation failures
- Automatically sets timestamp to current time when created

**Fields:**
- **`timestamp`** (LocalDateTime): When the error occurred
- **`status`** (int): HTTP status code (e.g., 404, 400, 500)
- **`error`** (String): Error type/category (e.g., "Resource Not Found", "Validation Failed")
- **`message`** (String): Human-readable error message
- **`path`** (String): The API endpoint path where the error occurred
- **`validationErrors`** (List<String>): Optional list of field-specific validation errors

**Usage:** Used by `GlobalExceptionHandler` to format all error responses consistently.

---

## Repository Layer

### `TaskRepository.java`
**Location:** `src/main/java/com/taskflow/repository/TaskRepository.java`

**Purpose:** This is a Spring Data JPA repository interface that provides data access methods for the Task entity. It extends `JpaRepository`, which provides basic CRUD operations automatically.

**Key Features:**
- **`@Repository`**: Marks this as a Spring repository component
- **Extends `JpaRepository<Task, Long>`**: Provides built-in methods like `save()`, `findById()`, `deleteById()`, `existsById()`, etc.
- **Custom Query Methods**: Defines custom methods using Spring Data JPA's query derivation and `@Query` annotations

**Methods:**
1. **`findByIsCompleted(Boolean isCompleted, Pageable pageable)`**
   - Finds all tasks filtered by completion status
   - Supports pagination and sorting via `Pageable` parameter
   - Returns a `Page<Task>` object

2. **`findAll(Pageable pageable)`**
   - Overrides the default `findAll()` to add pagination support
   - Returns paginated results

3. **`findByTitleContainingIgnoreCase(String title, Pageable pageable)`**
   - Custom JPQL query that searches for tasks by title (case-insensitive partial match)
   - Uses `LIKE` with `CONCAT` for pattern matching
   - Supports pagination

4. **`findByIsCompletedAndTitleContainingIgnoreCase(Boolean isCompleted, String title, Pageable pageable)`**
   - Combines both filters: completion status AND title search
   - Most specific query method for complex filtering scenarios

**Purpose in Architecture:** This layer abstracts database operations. The service layer uses these methods without needing to write SQL queries manually. Spring Data JPA automatically implements these interface methods at runtime.

---

## Service Layer

### `TaskService.java`
**Location:** `src/main/java/com/taskflow/service/TaskService.java`

**Purpose:** Contains the business logic for task operations. It acts as an intermediary between the controller and repository layers, handling data transformation and business rules.

**Key Annotations:**
- **`@Service`**: Marks this as a Spring service component (business logic layer)
- **`@Transactional`**: Ensures all methods run within a database transaction
- **`@Transactional(readOnly = true)`**: Optimizes read-only operations

**Key Responsibilities:**
1. **Data Transformation**: Converts between Entity (`Task`) and DTO (`TaskResponseDto`) objects
2. **Business Logic**: Implements filtering logic, validation checks, and error handling
3. **Transaction Management**: Ensures data consistency through database transactions
4. **Exception Handling**: Throws custom exceptions when resources are not found

**Methods:**

1. **`createTask(TaskRequestDto requestDto)`**
   - Creates a new task from request DTO
   - Sets default values (isCompleted = false)
   - Saves to database and returns response DTO

2. **`getAllTasks(Boolean isCompleted, String title, Pageable pageable)`**
   - Retrieves tasks with optional filtering
   - Implements filtering logic based on provided parameters
   - Supports pagination and sorting
   - Returns paginated response DTOs

3. **`getTaskById(Long id)`**
   - Retrieves a single task by ID
   - Throws `ResourceNotFoundException` if task doesn't exist
   - Returns response DTO

4. **`updateTask(Long id, TaskRequestDto requestDto)`**
   - Updates an existing task's title, description, and dueDate
   - Throws exception if task not found
   - Preserves completion status and timestamps (managed by database)

5. **`updateTaskStatus(Long id, Boolean isCompleted)`**
   - Updates only the completion status of a task
   - Used by PATCH endpoints for marking tasks complete/incomplete

6. **`deleteTask(Long id)`**
   - Deletes a task by ID
   - Checks existence before deletion to provide meaningful error

7. **`convertToDto(Task task)`** (private)
   - Helper method to convert Task entity to TaskResponseDto
   - Encapsulates transformation logic

**Purpose in Architecture:** This layer implements the core business logic and ensures proper separation of concerns. Controllers delegate to services, and services use repositories for data access.

---

## Controller Layer

### `TaskController.java`
**Location:** `src/main/java/com/taskflow/controller/TaskController.java`

**Purpose:** This is the REST API controller that handles HTTP requests and responses. It defines all the API endpoints and maps them to service methods.

**Key Annotations:**
- **`@RestController`**: Combines `@Controller` and `@ResponseBody`, making all methods return JSON responses
- **`@RequestMapping("/api/tasks")`**: Base path for all endpoints in this controller
- **`@PostMapping`**, **`@GetMapping`**, **`@PutMapping`**, **`@PatchMapping`**, **`@DeleteMapping`**: HTTP method mappings
- **`@Valid`**: Triggers validation on request DTOs
- **`@PathVariable`**: Extracts path variables from URL
- **`@RequestParam`**: Extracts query parameters from URL
- **`@RequestBody`**: Binds JSON request body to DTO object
- **`@PageableDefault`**: Sets default pagination parameters

**Endpoints:**

1. **POST `/api/tasks`**
   - Creates a new task
   - Accepts `TaskRequestDto` in request body
   - Returns `TaskResponseDto` with HTTP 201 (Created)
   - Validates input using `@Valid`

2. **GET `/api/tasks`**
   - Retrieves all tasks with pagination
   - Query parameters:
     - `isCompleted` (optional): Filter by completion status
     - `title` (optional): Filter by title (partial match)
     - `page` (optional): Page number (default: 0)
     - `size` (optional): Page size (default: 10)
     - `sort` (optional): Sort field and direction (default: createdAt,desc)
   - Returns paginated `Page<TaskResponseDto>`

3. **GET `/api/tasks/{id}`**
   - Retrieves a single task by ID
   - Returns `TaskResponseDto` or 404 if not found

4. **PUT `/api/tasks/{id}`**
   - Updates an existing task (full update)
   - Accepts `TaskRequestDto` in request body
   - Returns updated `TaskResponseDto` or 404 if not found

5. **PATCH `/api/tasks/{id}/complete`**
   - Marks a task as completed
   - Returns updated `TaskResponseDto` or 404 if not found

6. **PATCH `/api/tasks/{id}/incomplete`**
   - Marks a task as incomplete
   - Returns updated `TaskResponseDto` or 404 if not found

7. **DELETE `/api/tasks/{id}`**
   - Deletes a task by ID
   - Returns HTTP 204 (No Content) or 404 if not found

**Purpose in Architecture:** This is the entry point for all HTTP requests. It handles request/response mapping, validation, and delegates business logic to the service layer.

---

## Exception Handling Layer

### `ResourceNotFoundException.java`
**Location:** `src/main/java/com/todo/exception/ResourceNotFoundException.java`

**Purpose:** Custom exception class that represents the scenario when a requested resource (like a task) is not found in the database.

**Key Features:**
- Extends `RuntimeException` (unchecked exception)
- Provides two constructors:
  - Generic message constructor
  - Resource name and ID constructor (formats message automatically)

**Usage:** Thrown by service methods when a task with a given ID doesn't exist. The `GlobalExceptionHandler` catches this and converts it to an HTTP 404 response.

**Example:** When `getTaskById(999)` is called but task 999 doesn't exist, this exception is thrown with message "Task with id 999 not found".

---

### `GlobalExceptionHandler.java`
**Location:** `src/main/java/com/taskflow/exception/GlobalExceptionHandler.java`

**Purpose:** Centralized exception handling for the entire application. It catches all exceptions and converts them to standardized HTTP error responses.

**Key Annotations:**
- **`@RestControllerAdvice`**: Makes this class handle exceptions across all controllers
- **`@ExceptionHandler`**: Methods annotated with this handle specific exception types

**Exception Handlers:**

1. **`handleResourceNotFoundException`**
   - Handles `ResourceNotFoundException`
   - Returns HTTP 404 (Not Found)
   - Includes error details in `ErrorResponseDto`

2. **`handleValidationExceptions`**
   - Handles `MethodArgumentNotValidException` (from `@Valid` annotations)
   - Returns HTTP 400 (Bad Request)
   - Includes list of validation errors for each invalid field
   - Extracts field names and error messages from validation failures

3. **`handleConstraintViolationException`**
   - Handles `ConstraintViolationException` (from path/query parameter validation)
   - Returns HTTP 400 (Bad Request)
   - Includes constraint violation details

4. **`handleGlobalException`**
   - Catches all other exceptions (catch-all handler)
   - Returns HTTP 500 (Internal Server Error)
   - Logs unexpected errors for debugging

**Purpose in Architecture:** Provides consistent error responses across the entire API. Instead of each controller handling exceptions individually, this centralized handler ensures all errors follow the same format and include appropriate HTTP status codes.

---

## Architecture Flow Summary

**Request Flow:**
1. Client sends HTTP request → **TaskController** receives it
2. Controller validates input using `@Valid` → **TaskRequestDto** is validated
3. Controller calls → **TaskService** method
4. Service converts DTO to Entity → Uses **Task** entity
5. Service calls → **TaskRepository** method
6. Repository queries database → Returns **Task** entity
7. Service converts Entity to DTO → Returns **TaskResponseDto**
8. Controller returns → HTTP response with DTO

**Error Flow:**
1. Exception occurs anywhere in the flow
2. **GlobalExceptionHandler** catches it
3. Handler creates → **ErrorResponseDto**
4. Handler returns → HTTP error response

**Key Design Principles:**
- **Separation of Concerns**: Each layer has a specific responsibility
- **DTO Pattern**: Entities are never exposed to clients
- **Dependency Injection**: Spring manages object creation and dependencies
- **Transaction Management**: Service layer ensures data consistency
- **Centralized Error Handling**: Consistent error responses across the API

---

## Summary

TaskFlow follows a clean, layered architecture:

- **Entity Layer**: Database representation (`Task`)
- **DTO Layer**: API contracts (`TaskRequestDto`, `TaskResponseDto`, `ErrorResponseDto`)
- **Repository Layer**: Data access (`TaskRepository`)
- **Service Layer**: Business logic (`TaskService`)
- **Controller Layer**: HTTP handling (`TaskController`)
- **Exception Layer**: Error handling (`ResourceNotFoundException`, `GlobalExceptionHandler`)

Each layer has a clear responsibility, making the codebase maintainable, testable, and scalable.

