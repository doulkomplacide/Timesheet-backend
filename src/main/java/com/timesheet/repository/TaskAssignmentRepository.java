package com.timesheet.repository;

import com.timesheet.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    List<TaskAssignment> findByUserId(Long userId);
    List<TaskAssignment> findByTaskId(Long taskId);
    Optional<TaskAssignment> findByTaskIdAndUserId(Long taskId, Long userId);
    void deleteByTaskIdAndUserId(Long taskId, Long userId);
    boolean existsByTaskIdAndUserId(Long taskId, Long userId);
}