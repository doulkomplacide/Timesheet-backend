package com.gestiontemps.service;

import com.gestiontemps.dto.requete.RequeteTache;
import com.gestiontemps.dto.reponse.ReponseTache;
import com.gestiontemps.entite.Projet;
import com.gestiontemps.entite.Tache;
import com.gestiontemps.entite.AffectationTache;
import com.gestiontemps.entite.Utilisateur;
import com.gestiontemps.enums.PrioriteTache;
import com.gestiontemps.enums.StatutTache;
import com.gestiontemps.exception.BadRequestException;
import com.gestiontemps.exception.ResourceNotFoundException;
import com.gestiontemps.repository.ProjectRepository;
import com.gestiontemps.repository.TaskAssignmentRepository;
import com.gestiontemps.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final UserService userService;

    public List<ReponseTache> getAllTasks() {
        return taskRepository.findAll()
            .stream()
            .map(ReponseTache::fromEntity)
            .collect(Collectors.toList());
    }

    public ReponseTache getTaskById(Long id) {
        Tache task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tâche", id));
        return ReponseTache.fromEntity(task);
    }

    public List<ReponseTache> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId)
            .stream()
            .map(ReponseTache::fromEntity)
            .collect(Collectors.toList());
    }

    public List<ReponseTache> getTasksByUser(Long userId) {
        return taskRepository.findTasksByUserId(userId)
            .stream()
            .map(ReponseTache::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public ReponseTache createTask(RequeteTache request) {
        // Vérifier si le code existe déjà
        if (taskRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Une tâche avec ce code existe déjà");
        }

        Projet project = projectRepository.findById(request.getProjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Projet", request.getProjectId()));

        Tache task = new Tache();
        task.setCode(request.getCode());
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setProject(project);

        if (request.getPriority() != null) {
            try {
                task.setPriority(PrioriteTache.valueOf(request.getPriority().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Priorité invalide: " + request.getPriority());
            }
        }

        if (request.getStatus() != null) {
            try {
                task.setStatus(StatutTache.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Statut invalide: " + request.getStatus());
            }
        }

        Tache savedTask = taskRepository.save(task);

        // Assigner la tâche à un utilisateur si spécifié
        if (request.getAssignedTo() != null) {
            assignTaskToUser(savedTask.getId(), request.getAssignedTo());
        }

        return ReponseTache.fromEntity(savedTask);
    }

    @Transactional
    public ReponseTache updateTask(Long id, RequeteTache request) {
        Tache task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tâche", id));

        // Vérifier si le code est déjà utilisé par une autre tâche
        if (!task.getCode().equals(request.getCode()) && 
            taskRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Une tâche avec ce code existe déjà");
        }

        task.setCode(request.getCode());
        task.setName(request.getName());
        task.setDescription(request.getDescription());

        if (request.getPriority() != null) {
            try {
                task.setPriority(PrioriteTache.valueOf(request.getPriority().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Priorité invalide: " + request.getPriority());
            }
        }

        if (request.getStatus() != null) {
            try {
                task.setStatus(StatutTache.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Statut invalide: " + request.getStatus());
            }
        }

        if (request.getProjectId() != null && !request.getProjectId().equals(task.getProject().getId())) {
            Projet project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Projet", request.getProjectId()));
            task.setProject(project);
        }

        Tache updatedTask = taskRepository.save(task);
        return ReponseTache.fromEntity(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        Tache task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tâche", id));
        
        // Vérifier s'il y a des timesheets associés
        if (!task.getTimesheetEntries().isEmpty()) {
            throw new BadRequestException("Impossible de supprimer une tâche avec des entrées de timesheet associées");
        }
        
        // Supprimer les assignments
        taskAssignmentRepository.deleteAll(task.getAssignments());
        taskRepository.delete(task);
    }

    @Transactional
    public void assignTaskToUser(Long taskId, Long userId) {
        Tache task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Tâche", taskId));
        
        Utilisateur user = userService.getUserEntityById(userId);

        // Vérifier si l'utilisateur est déjà assigné
        if (taskAssignmentRepository.existsByTaskIdAndUserId(taskId, userId)) {
            throw new BadRequestException("L'utilisateur est déjà assigné à cette tâche");
        }

        AffectationTache assignment = new AffectationTache();
        assignment.setTask(task);
        assignment.setUser(user);
        
        taskAssignmentRepository.save(assignment);
    }

    @Transactional
    public void unassignTaskFromUser(Long taskId, Long userId) {
        taskAssignmentRepository.deleteByTaskIdAndUserId(taskId, userId);
    }
}