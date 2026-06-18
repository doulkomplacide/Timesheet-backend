package com.timesheet.entity;

import com.timesheet.enums.EntryStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "timesheet_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimesheetEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;
    
    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;
    
    @Column(name = "hours_worked", nullable = false)
    private Double hoursWorked;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntryStatus status = EntryStatus.DRAFT;
    
    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;
    
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
    
    @Column(name = "approved_by")
    private Long approvedBy;
    
    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void submit() {
        this.status = EntryStatus.SUBMITTED;
        this.submittedAt = LocalDateTime.now();
    }
    
    public void approve(Long approverId) {
        this.status = EntryStatus.APPROVED;
        this.approvedAt = LocalDateTime.now();
        this.approvedBy = approverId;
        this.rejectionReason = null;
    }
    
    public void reject(String reason, Long approverId) {
        this.status = EntryStatus.REJECTED;
        this.approvedAt = LocalDateTime.now();
        this.approvedBy = approverId;
        this.rejectionReason = reason;
    }
}