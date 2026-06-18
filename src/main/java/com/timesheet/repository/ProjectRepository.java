package com.timesheet.repository;

import com.timesheet.entity.Project;
import com.timesheet.enums.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByCode(String code);
    boolean existsByCode(String code);
    List<Project> findByServiceId(Long serviceId);
    List<Project> findByStatus(ProjectStatus status);
    
    @Query("SELECT p FROM Project p WHERE p.service.department.id = :departmentId")
    List<Project> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    @Query("SELECT p FROM Project p JOIN p.tasks t JOIN t.assignments a WHERE a.user.id = :userId")
    List<Project> findProjectsByUserId(@Param("userId") Long userId);
}