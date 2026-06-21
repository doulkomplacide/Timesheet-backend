package com.gestiontemps.entite;

import com.gestiontemps.enums.AssignmentRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "task_id")
    private Tache task;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Utilisateur user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentRole role = AssignmentRole.CONTRIBUTOR;
    
    @Column(name = "assigned_at")
    private LocalDateTime assignedAt;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        assignedAt = LocalDateTime.now();
        createdAt = LocalDateTime.now();
    }
}