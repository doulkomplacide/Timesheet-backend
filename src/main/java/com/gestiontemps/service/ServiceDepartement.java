package com.gestiontemps.service;

import com.gestiontemps.entite.Departement;
import com.gestiontemps.entite.ServiceEntite;
import com.gestiontemps.exception.BadRequestException;
import com.gestiontemps.exception.ResourceNotFoundException;
import com.gestiontemps.repository.DepartmentRepository;
import com.gestiontemps.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final ServiceRepository serviceRepository;

    public List<Departement> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Departement getDepartmentById(Long id) {
        return departmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Département", id));
    }

    @Transactional
    public Departement createDepartment(Departement department) {
        if (departmentRepository.existsByName(department.getName())) {
            throw new BadRequestException("Un département avec ce nom existe déjà");
        }
        return departmentRepository.save(department);
    }

    @Transactional
    public Departement updateDepartment(Long id, Departement departmentRequest) {
        Departement department = getDepartmentById(id);
        
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
        Departement department = getDepartmentById(id);
        
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

    public List<ServiceEntite> getServicesByDepartment(Long departmentId) {
        Departement department = getDepartmentById(departmentId);
        return department.getServices();
    }

    @Transactional
    public ServiceEntite addServiceToDepartment(Long departmentId, ServiceEntite service) {
        Departement department = getDepartmentById(departmentId);
        
        if (serviceRepository.existsByName(service.getName())) {
            throw new BadRequestException("Un service avec ce nom existe déjà");
        }

        service.setDepartment(department);
        return serviceRepository.save(service);
    }
}