package com.timesheet.repository;

import com.timesheet.entity.Task;
import com.timesheet.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByCode(String code);
    boolean existsByCode(String code);
    List<Task> findByProjectId(Long projectId);
    List<Task> findByStatus(TaskStatus status);
    
    @Query("SELECT t FROM Task t JOIN t.assignments a WHERE a.user.id = :userId")
    List<Task> findTasksByUserId(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    List<Task> findByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatus status);
}