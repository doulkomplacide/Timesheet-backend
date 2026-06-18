package com.timesheet.service;

import com.timesheet.dto.request.ProjectRequest;
import com.timesheet.dto.response.ProjectResponse;
import com.timesheet.entity.Project;
import com.timesheet.entity.ServiceEntity;
import com.timesheet.enums.ProjectStatus;
import com.timesheet.exception.BadRequestException;
import com.timesheet.exception.ResourceNotFoundException;
import com.timesheet.repository.ProjectRepository;
import com.timesheet.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ServiceRepository serviceRepository;

    public Page<ProjectResponse> getAllProjects(Pageable pageable) {
        return projectRepository.findAll(pageable)
            .map(this::convertToResponse);
    }

    public ProjectResponse getProjectById(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Projet", id));
        return convertToResponse(project);
    }

    public List<ProjectResponse> getProjectsByService(Long serviceId) {
        return projectRepository.findByServiceId(serviceId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    public List<ProjectResponse> getProjectsByDepartment(Long departmentId) {
        return projectRepository.findByDepartmentId(departmentId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    public List<ProjectResponse> getProjectsByUser(Long userId) {
        return projectRepository.findProjectsByUserId(userId)
            .stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        // Vérifier si le code existe déjà
        if (projectRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Un projet avec ce code existe déjà");
        }

        ServiceEntity service = serviceRepository.findById(request.getServiceId())
            .orElseThrow(() -> new ResourceNotFoundException("Service", request.getServiceId()));

        Project project = new Project();
        project.setCode(request.getCode());
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setService(service);
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        if (request.getStatus() != null) {
            try {
                project.setStatus(ProjectStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Statut invalide: " + request.getStatus());
            }
        }

        Project savedProject = projectRepository.save(project);
        return convertToResponse(savedProject);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Projet", id));

        // Vérifier si le code est déjà utilisé par un autre projet
        if (!project.getCode().equals(request.getCode()) && 
            projectRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Un projet avec ce code existe déjà");
        }

        project.setCode(request.getCode());
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setEndDate(request.getEndDate());

        if (request.getStatus() != null) {
            try {
                project.setStatus(ProjectStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Statut invalide: " + request.getStatus());
            }
        }

        if (request.getServiceId() != null && !request.getServiceId().equals(project.getService().getId())) {
            ServiceEntity service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service", request.getServiceId()));
            project.setService(service);
        }

        Project updatedProject = projectRepository.save(project);
        return convertToResponse(updatedProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Projet", id));
        
        // Vérifier s'il y a des tâches associées
        if (!project.getTasks().isEmpty()) {
            throw new BadRequestException("Impossible de supprimer un projet avec des tâches associées");
        }
        
        projectRepository.delete(project);
    }

    private ProjectResponse convertToResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setCode(project.getCode());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setStatus(project.getStatus().name());
        response.setServiceName(project.getService() != null ? project.getService().getName() : null);
        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());
        response.setTaskCount(project.getTasks() != null ? project.getTasks().size() : 0);
        response.setCreatedAt(project.getCreatedAt());
        return response;
    }
}