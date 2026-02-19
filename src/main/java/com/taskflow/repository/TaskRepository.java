package com.taskflow.repository;

import com.taskflow.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /**
     * Find all tasks filtered by completion status
     */
    Page<Task> findByIsCompleted(Boolean isCompleted, Pageable pageable);
    
    /**
     * Find all tasks with pagination and sorting
     */
    Page<Task> findAll(Pageable pageable);
    
    /**
     * Find tasks by title containing (case-insensitive)
     */
    @Query("SELECT t FROM Task t WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Task> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);
    
    /**
     * Find tasks by completion status and title containing
     */
    @Query("SELECT t FROM Task t WHERE t.isCompleted = :isCompleted AND LOWER(t.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Task> findByIsCompletedAndTitleContainingIgnoreCase(
            @Param("isCompleted") Boolean isCompleted,
            @Param("title") String title,
            Pageable pageable
    );
}
