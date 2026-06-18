package com.timesheet.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String status;
    private String serviceName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer taskCount;
    private LocalDateTime createdAt;
}