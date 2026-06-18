package com.timesheet.repository;

import com.timesheet.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    Optional<ServiceEntity> findByName(String name);
    boolean existsByName(String name);
    List<ServiceEntity> findByDepartmentId(Long departmentId);
}