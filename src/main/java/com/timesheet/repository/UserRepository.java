package com.timesheet.repository;

import com.timesheet.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByDepartmentId(Long departmentId);

    List<User> findByServiceId(Long serviceId);

    List<User> findByActive(boolean active);

    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    List<User> findAllAdmins();

    @Query("SELECT u FROM User u WHERE u.role = 'MANAGER'")
    List<User> findAllManagers();

    @Query("SELECT u FROM User u WHERE u.department.id = :departmentId AND u.active = true")
    List<User> findActiveByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT u FROM User u WHERE u.service.id = :serviceId AND u.active = true")
    List<User> findActiveByServiceId(@Param("serviceId") Long serviceId);
}