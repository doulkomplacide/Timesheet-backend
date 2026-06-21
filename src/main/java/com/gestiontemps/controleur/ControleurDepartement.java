package com.gestiontemps.controleur;

import com.gestiontemps.entite.Departement;
import com.gestiontemps.entite.ServiceEntite;
import com.gestiontemps.service.ServiceDepartement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final ServiceDepartement departmentService;

    @GetMapping
    public ResponseEntity<List<Departement>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.getAllDepartments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Departement> getDepartmentById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getDepartmentById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Departement> createDepartment(@RequestBody Departement department) {
        return ResponseEntity.ok(departmentService.createDepartment(department));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Departement> updateDepartment(
            @PathVariable Long id,
            @RequestBody Departement department) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, department));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{departmentId}/services")
    public ResponseEntity<List<ServiceEntite>> getServicesByDepartment(@PathVariable Long departmentId) {
        return ResponseEntity.ok(departmentService.getServicesByDepartment(departmentId));
    }

    @PostMapping("/{departmentId}/services")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ServiceEntite> addServiceToDepartment(
            @PathVariable Long departmentId,
            @RequestBody ServiceEntite service) {
        return ResponseEntity.ok(departmentService.addServiceToDepartment(departmentId, service));
    }
}