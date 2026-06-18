package com.timesheet.service;

import com.timesheet.dto.request.TaskRequest;
import com.timesheet.dto.response.TaskResponse;
import com.timesheet.entity.Project;
import com.timesheet.entity.Task;
import com.timesheet.entity.TaskAssignment;
import com.timesheet.entity.User;
import com.timesheet.enums.TaskPriority;
import com.timesheet.enums.TaskStatus;
import com.timesheet.exception.BadRequestException;
import com.timesheet.exception.ResourceNotFoundException;
import com.timesheet.repository.ProjectRepository;
import com.timesheet.repository.TaskAssignmentRepository;
import com.timesheet.repository.TaskRepository;
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

    public List<TaskResponse> getAllTasks() {
        return taskRepository.findAll()
            .stream()
            .map(TaskResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Tâche", id));
        return TaskResponse.fromEntity(task);
    }

    public List<TaskResponse> getTasksByProject(Long projectId) {
        return taskRepository.findByProjectId(projectId)
            .stream()
            .map(TaskResponse::fromEntity)
            .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByUser(Long userId) {
        return taskRepository.findTasksByUserId(userId)
            .stream()
            .map(TaskResponse::fromEntity)
            .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse createTask(TaskRequest request) {
        // Vérifier si le code existe déjà
        if (taskRepository.existsByCode(request.getCode())) {
            throw new BadRequestException("Une tâche avec ce code existe déjà");
        }

        Project project = projectRepository.findById(request.getProjectId())
            .orElseThrow(() -> new ResourceNotFoundException("Projet", request.getProjectId()));

        Task task = new Task();
        task.setCode(request.getCode());
        task.setName(request.getName());
        task.setDescription(request.getDescription());
        task.setProject(project);

        if (request.getPriority() != null) {
            try {
                task.setPriority(TaskPriority.valueOf(request.getPriority().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Priorité invalide: " + request.getPriority());
            }
        }

        if (request.getStatus() != null) {
            try {
                task.setStatus(TaskStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Statut invalide: " + request.getStatus());
            }
        }

        Task savedTask = taskRepository.save(task);

        // Assigner la tâche à un utilisateur si spécifié
        if (request.getAssignedTo() != null) {
            assignTaskToUser(savedTask.getId(), request.getAssignedTo());
        }

        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(Long id, TaskRequest request) {
        Task task = taskRepository.findById(id)
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
                task.setPriority(TaskPriority.valueOf(request.getPriority().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Priorité invalide: " + request.getPriority());
            }
        }

        if (request.getStatus() != null) {
            try {
                task.setStatus(TaskStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Statut invalide: " + request.getStatus());
            }
        }

        if (request.getProjectId() != null && !request.getProjectId().equals(task.getProject().getId())) {
            Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Projet", request.getProjectId()));
            task.setProject(project);
        }

        Task updatedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
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
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Tâche", taskId));
        
        User user = userService.getUserEntityById(userId);

        // Vérifier si l'utilisateur est déjà assigné
        if (taskAssignmentRepository.existsByTaskIdAndUserId(taskId, userId)) {
            throw new BadRequestException("L'utilisateur est déjà assigné à cette tâche");
        }

        TaskAssignment assignment = new TaskAssignment();
        assignment.setTask(task);
        assignment.setUser(user);
        
        taskAssignmentRepository.save(assignment);
    }

    @Transactional
    public void unassignTaskFromUser(Long taskId, Long userId) {
        taskAssignmentRepository.deleteByTaskIdAndUserId(taskId, userId);
    }
}