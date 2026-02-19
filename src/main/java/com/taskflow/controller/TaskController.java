package com.taskflow.controller;

import com.taskflow.dto.TaskRequestDto;
import com.taskflow.dto.TaskResponseDto;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private final TaskService taskService;
    
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    /**
     * POST /api/tasks
     * Create a new task
     */
    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto requestDto) {
        TaskResponseDto responseDto = taskService.createTask(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    
    /**
     * GET /api/tasks
     * Get all tasks with pagination, sorting, and optional filtering
     * Query parameters:
     *   - isCompleted: filter by completion status (true/false)
     *   - title: filter by title (case-insensitive partial match)
     *   - page: page number (default: 0)
     *   - size: page size (default: 10)
     *   - sort: sort field and direction (e.g., "createdAt,desc" or "title,asc")
     */
    @GetMapping
    public ResponseEntity<Page<TaskResponseDto>> getAllTasks(
            @RequestParam(required = false) Boolean isCompleted,
            @RequestParam(required = false) String title,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<TaskResponseDto> tasks = taskService.getAllTasks(isCompleted, title, pageable);
        return ResponseEntity.ok(tasks);
    }
    
    /**
     * GET /api/tasks/{id}
     * Get a task by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(@PathVariable Long id) {
        TaskResponseDto responseDto = taskService.getTaskById(id);
        return ResponseEntity.ok(responseDto);
    }
    
    /**
     * PUT /api/tasks/{id}
     * Update a task
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDto requestDto) {
        TaskResponseDto responseDto = taskService.updateTask(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }
    
    /**
     * PATCH /api/tasks/{id}/complete
     * Mark a task as completed
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponseDto> markTaskAsCompleted(@PathVariable Long id) {
        TaskResponseDto responseDto = taskService.updateTaskStatus(id, true);
        return ResponseEntity.ok(responseDto);
    }
    
    /**
     * PATCH /api/tasks/{id}/incomplete
     * Mark a task as incomplete
     */
    @PatchMapping("/{id}/incomplete")
    public ResponseEntity<TaskResponseDto> markTaskAsIncomplete(@PathVariable Long id) {
        TaskResponseDto responseDto = taskService.updateTaskStatus(id, false);
        return ResponseEntity.ok(responseDto);
    }
    
    /**
     * DELETE /api/tasks/{id}
     * Delete a task
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
