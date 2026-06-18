package com.timesheet.controller;

import com.timesheet.entity.Department;
import com.timesheet.entity.ServiceEntity;
import com.timesheet.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Department> createDepartment(@RequestBody Department department) {
        return ResponseEntity.ok(departmentService.createDepartment(department));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable Long id,
            @RequestBody Department department) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, department));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{departmentId}/services")
    public ResponseEntity<List<ServiceEntity>> getServicesByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(departmentService.getServicesByDepartment(departmentId));
    }

    @PostMapping("/{departmentId}/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceEntity> addServiceToDepartment(
            @PathVariable Long departmentId,
            @RequestBody ServiceEntity service) {
        return ResponseEntity.ok(departmentService.addServiceToDepartment(departmentId, service));
    }
}