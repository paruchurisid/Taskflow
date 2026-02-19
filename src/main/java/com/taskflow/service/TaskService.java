package com.taskflow.service;

import com.taskflow.dto.TaskRequestDto;
import com.taskflow.dto.TaskResponseDto;
import com.taskflow.entity.Task;
import com.taskflow.exception.ResourceNotFoundException;
import com.taskflow.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {
    
    private final TaskRepository taskRepository;
    
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    /**
     * Create a new task
     */
    public TaskResponseDto createTask(TaskRequestDto requestDto) {
        Task task = new Task();
        task.setTitle(requestDto.getTitle());
        task.setDescription(requestDto.getDescription());
        task.setDueDate(requestDto.getDueDate());
        task.setIsCompleted(false);
        
        Task savedTask = taskRepository.save(task);
        return convertToDto(savedTask);
    }
    
    /**
     * Get all tasks with pagination and optional filtering
     */
    @Transactional(readOnly = true)
    public Page<TaskResponseDto> getAllTasks(Boolean isCompleted, String title, Pageable pageable) {
        Page<Task> tasks;
        
        if (isCompleted != null && title != null && !title.trim().isEmpty()) {
            tasks = taskRepository.findByIsCompletedAndTitleContainingIgnoreCase(isCompleted, title, pageable);
        } else if (isCompleted != null) {
            tasks = taskRepository.findByIsCompleted(isCompleted, pageable);
        } else if (title != null && !title.trim().isEmpty()) {
            tasks = taskRepository.findByTitleContainingIgnoreCase(title, pageable);
        } else {
            tasks = taskRepository.findAll(pageable);
        }
        
        return tasks.map(this::convertToDto);
    }
    
    /**
     * Get a task by ID
     */
    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        return convertToDto(task);
    }
    
    /**
     * Update a task
     */
    public TaskResponseDto updateTask(Long id, TaskRequestDto requestDto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        
        task.setTitle(requestDto.getTitle());
        task.setDescription(requestDto.getDescription());
        task.setDueDate(requestDto.getDueDate());
        
        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }
    
    /**
     * Mark a task as completed or incomplete
     */
    public TaskResponseDto updateTaskStatus(Long id, Boolean isCompleted) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", id));
        
        task.setIsCompleted(isCompleted);
        Task updatedTask = taskRepository.save(task);
        return convertToDto(updatedTask);
    }
    
    /**
     * Delete a task
     */
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task", id);
        }
        taskRepository.deleteById(id);
    }
    
    /**
     * Convert Task entity to TaskResponseDto
     */
    private TaskResponseDto convertToDto(Task task) {
        return new TaskResponseDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getIsCompleted(),
                task.getDueDate(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}
