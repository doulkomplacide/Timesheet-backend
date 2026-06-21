package com.gestiontemps.dto.reponse;

import com.gestiontemps.entite.Tache;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TaskResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String priority;
    private String status;
    private Long projectId;
    private String projectName;
    private List<Long> assignedTo;
    private List<String> assignedToNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;


    public static TaskResponse fromEntity(Tache task) {
        if (task == null) {
            return null;
        }

        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setCode(task.getCode());
        response.setName(task.getName());
        response.setDescription(task.getDescription());

        if (task.getPriority() != null) {
            response.setPriority(task.getPriority().name());
        }

        if (task.getStatus() != null) {
            response.setStatus(task.getStatus().name());
        }

        if (task.getProject() != null) {
            response.setProjectId(task.getProject().getId());
            response.setProjectName(task.getProject().getName());
        }

        // Extraction des informations des utilisateurs assignés si la liste existe
        if (task.getAssignments() != null) {
            response.setAssignedTo(task.getAssignments().stream()
                    .map(assignment -> assignment.getUser().getId())
                    .collect(Collectors.toList()));

            response.setAssignedToNames(task.getAssignments().stream()
                    .map(assignment -> assignment.getUser().getFirstName() + " " + assignment.getUser().getLastName())
                    .collect(Collectors.toList()));
        }

        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());

        return response;
    }
}