package com.timesheet.service;

import com.timesheet.entity.Department;
import com.timesheet.entity.ServiceEntity;
import com.timesheet.exception.BadRequestException;
import com.timesheet.exception.ResourceNotFoundException;
import com.timesheet.repository.DepartmentRepository;
import com.timesheet.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final ServiceRepository serviceRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Département", id));
    }

    @Transactional
    public Department createDepartment(Department department) {
        if (departmentRepository.existsByName(department.getName())) {
            throw new BadRequestException("Un département avec ce nom existe déjà");
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public Department updateDepartment(Long id, Department departmentRequest) {
        Department department = getDepartmentById(id);
        
        if (!department.getName().equals(departmentRequest.getName()) && 
            departmentRepository.existsByName(departmentRequest.getName())) {
            throw new BadRequestException("Un département avec ce nom existe déjà");
        }

        department.setName(departmentRequest.getName());
        department.setDescription(departmentRequest.getDescription());
        return departmentRepository.save(department);
    }

    @Transactional
    public void deleteDepartment(Long id) {
        Department department = getDepartmentById(id);
        
        // Vérifier si le département a des services
        if (!department.getServices().isEmpty()) {
            throw new BadRequestException("Impossible de supprimer un département avec des services associés");
        }
        
        // Vérifier si le département a des utilisateurs
        if (!department.getUsers().isEmpty()) {
            throw new BadRequestException("Impossible de supprimer un département avec des utilisateurs associés");
        }
        
        departmentRepository.delete(department);
    }

    public List<ServiceEntity> getServicesByDepartment(Long departmentId) {
        Department department = getDepartmentById(departmentId);
        return department.getServices();
    }

    @Transactional
    public ServiceEntity addServiceToDepartment(Long departmentId, ServiceEntity service) {
        Department department = getDepartmentById(departmentId);
        
        if (serviceRepository.existsByName(service.getName())) {
            throw new BadRequestException("Un service avec ce nom existe déjà");
        }

        service.setDepartment(department);
        return serviceRepository.save(service);
    }
}