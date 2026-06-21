package com.gestiontemps.dto.reponse;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class TimesheetEntryResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private Long taskId;
    private String taskName;
    private String projectName;
    private LocalDate entryDate;
    private Double hoursWorked;
    private String description;
    private String status;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}