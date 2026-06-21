package com.gestiontemps.service;

import com.gestiontemps.dto.requete.RequeteSaisieTemps;
import com.gestiontemps.dto.reponse.ReponseSaisieTemps;
import com.gestiontemps.entite.Tache;
import com.gestiontemps.entite.SaisieTemps;
import com.gestiontemps.entite.Utilisateur;
import com.gestiontemps.enums.StatutSaisie;
import com.gestiontemps.exception.BadRequestException;
import com.gestiontemps.exception.ResourceNotFoundException;
import com.gestiontemps.exception.UnauthorizedException;
import com.gestiontemps.repository.TaskRepository;
import com.gestiontemps.repository.TimesheetEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimesheetService {
    private final TimesheetEntryRepository timesheetEntryRepository;
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final ServiceAuthentification authService;

    @Transactional
    public ReponseSaisieTemps createEntry(RequeteSaisieTemps request) {
        Utilisateur currentUser = authService.getCurrentUser();
        
        Tache task = taskRepository.findById(request.getTaskId())
            .orElseThrow(() -> new ResourceNotFoundException("Tâche", request.getTaskId()));

        // Vérifier si l'utilisateur est assigné à cette tâche
        boolean isAssigned = task.getAssignments().stream()
            .anyMatch(a -> a.getUser().getId().equals(currentUser.getId()));
        
        if (!isAssigned && !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("Vous n'êtes pas assigné à cette tâche");
        }

        // Vérifier s'il existe déjà une entrée pour cette date et cette tâche
        List<SaisieTemps> existingEntries = timesheetEntryRepository
            .findByUserIdAndTaskIdAndEntryDate(currentUser.getId(), task.getId(), request.getEntryDate());
        
        if (!existingEntries.isEmpty()) {
            throw new BadRequestException("Vous avez déjà une entrée pour cette tâche à cette date");
        }

        SaisieTemps entry = new SaisieTemps();
        entry.setUser(currentUser);
        entry.setTask(task);
        entry.setEntryDate(request.getEntryDate());
        entry.setHoursWorked(request.getHoursWorked());
        entry.setDescription(request.getDescription());
        entry.setStatus(StatutSaisie.DRAFT);

        SaisieTemps savedEntry = timesheetEntryRepository.save(entry);
        return convertToResponse(savedEntry);
    }

    @Transactional
    public ReponseSaisieTemps updateEntry(Long id, RequeteSaisieTemps request) {
        Utilisateur currentUser = authService.getCurrentUser();
        
        SaisieTemps entry = timesheetEntryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrée de timesheet", id));

        // Vérifier que l'utilisateur est le propriétaire ou admin
        if (!entry.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à modifier cette entrée");
        }

        // Vérifier que l'entrée est en état DRAFT
        if (entry.getStatus() != StatutSaisie.DRAFT) {
            throw new BadRequestException("Impossible de modifier une entrée déjà soumise ou approuvée");
        }

        entry.setHoursWorked(request.getHoursWorked());
        entry.setDescription(request.getDescription());
        
        if (request.getTaskId() != null && !request.getTaskId().equals(entry.getTask().getId())) {
            Tache task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Tâche", request.getTaskId()));
            entry.setTask(task);
        }

        SaisieTemps updatedEntry = timesheetEntryRepository.save(entry);
        return convertToResponse(updatedEntry);
    }

    @Transactional
    public void deleteEntry(Long id) {
        Utilisateur currentUser = authService.getCurrentUser();
        
        SaisieTemps entry = timesheetEntryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrée de timesheet", id));

        if (!entry.getUser().getId().equals(currentUser.getId()) && 
            !currentUser.getRole().name().equals("ADMIN")) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à supprimer cette entrée");
        }

        if (entry.getStatus() != StatutSaisie.DRAFT) {
            throw new BadRequestException("Impossible de supprimer une entrée déjà soumise ou approuvée");
        }

        timesheetEntryRepository.delete(entry);
    }

    @Transactional
    public ReponseSaisieTemps submitEntry(Long id) {
        Utilisateur currentUser = authService.getCurrentUser();
        
        SaisieTemps entry = timesheetEntryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrée de timesheet", id));

        if (!entry.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à soumettre cette entrée");
        }

        if (entry.getStatus() != StatutSaisie.DRAFT) {
            throw new BadRequestException("Cette entrée n'est pas en état de brouillon");
        }

        entry.submit();
        SaisieTemps updatedEntry = timesheetEntryRepository.save(entry);
        return convertToResponse(updatedEntry);
    }

    @Transactional
    public ReponseSaisieTemps approveEntry(Long id) {
        Utilisateur currentUser = authService.getCurrentUser();
        
        if (!currentUser.getRole().name().equals("ADMIN") && 
            !currentUser.getRole().name().equals("MANAGER")) {
            throw new UnauthorizedException("Seul un administrateur ou manager peut approuver");
        }

        SaisieTemps entry = timesheetEntryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrée de timesheet", id));

        if (entry.getStatus() != StatutSaisie.SUBMITTED) {
            throw new BadRequestException("Cette entrée n'est pas en état de soumission");
        }

        entry.approve(currentUser.getId());
        SaisieTemps updatedEntry = timesheetEntryRepository.save(entry);
        return convertToResponse(updatedEntry);
    }

    @Transactional
    public ReponseSaisieTemps rejectEntry(Long id, String reason) {
        Utilisateur currentUser = authService.getCurrentUser();
        
        if (!currentUser.getRole().name().equals("ADMIN") && 
            !currentUser.getRole().name().equals("MANAGER")) {
            throw new UnauthorizedException("Seul un administrateur ou manager peut rejeter");
        }

        SaisieTemps entry = timesheetEntryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Entrée de timesheet", id));

        if (entry.getStatus() != StatutSaisie.SUBMITTED) {
            throw new BadRequestException("Cette entrée n'est pas en état de soumission");
        }

        entry.reject(reason, currentUser.getId());
        SaisieTemps updatedEntry = timesheetEntryRepository.save(entry);
        return convertToResponse(updatedEntry);
    }

    public List<ReponseSaisieTemps> getEntriesForUser(Long userId, LocalDate startDate, LocalDate endDate) {
        Utilisateur currentUser = authService.getCurrentUser();
        
        // Si l'utilisateur demande ses propres entrées ou est admin/manager
        if (!currentUser.getId().equals(userId) && 
            !currentUser.getRole().name().equals("ADMIN") &&
            !currentUser.getRole().name().equals("MANAGER")) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à voir ces entrées");
        }

        List<SaisieTemps> entries = timesheetEntryRepository
            .findByUserIdAndEntryDateBetween(userId, startDate, endDate);
        
        return entries.stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    public List<ReponseSaisieTemps> getMyEntries(LocalDate startDate, LocalDate endDate) {
        Utilisateur currentUser = authService.getCurrentUser();
        return getEntriesForUser(currentUser.getId(), startDate, endDate);
    }

    public Map<String, Object> getStatistics(Long userId, LocalDate startDate, LocalDate endDate) {
        Utilisateur currentUser = authService.getCurrentUser();
        
        if (userId == null) {
            userId = currentUser.getId();
        }

        // Vérifier les autorisations
        if (!currentUser.getId().equals(userId) && 
            !currentUser.getRole().name().equals("ADMIN") &&
            !currentUser.getRole().name().equals("MANAGER")) {
            throw new UnauthorizedException("Vous n'êtes pas autorisé à voir ces statistiques");
        }

        List<SaisieTemps> entries = timesheetEntryRepository
            .findByUserIdAndEntryDateBetween(userId, startDate, endDate);

        Map<String, Object> stats = new HashMap<>();
        
        // Total des heures
        double totalHours = entries.stream()
            .mapToDouble(SaisieTemps::getHoursWorked)
            .sum();
        stats.put("totalHours", totalHours);
        
        // Nombre d'entrées par statut
        Map<String, Long> statusCount = entries.stream()
            .collect(Collectors.groupingBy(
                e -> e.getStatus().name(),
                Collectors.counting()
            ));
        stats.put("statusCount", statusCount);
        
        // Heures par jour
        Map<LocalDate, Double> hoursByDay = entries.stream()
            .collect(Collectors.groupingBy(
                SaisieTemps::getEntryDate,
                Collectors.summingDouble(SaisieTemps::getHoursWorked)
            ));
        stats.put("hoursByDay", hoursByDay);
        
        // Heures par projet
        Map<String, Double> hoursByProject = entries.stream()
            .collect(Collectors.groupingBy(
                e -> e.getTask().getProject().getName(),
                Collectors.summingDouble(SaisieTemps::getHoursWorked)
            ));
        stats.put("hoursByProject", hoursByProject);

        return stats;
    }

    private ReponseSaisieTemps convertToResponse(SaisieTemps entry) {
        ReponseSaisieTemps response = new ReponseSaisieTemps();
        response.setId(entry.getId());
        response.setUserId(entry.getUser().getId());
        response.setUserFullName(entry.getUser().getFullName());
        response.setTaskId(entry.getTask().getId());
        response.setTaskName(entry.getTask().getName());
        response.setProjectName(entry.getTask().getProject().getName());
        response.setEntryDate(entry.getEntryDate());
        response.setHoursWorked(entry.getHoursWorked());
        response.setDescription(entry.getDescription());
        response.setStatus(entry.getStatus().name());
        response.setSubmittedAt(entry.getSubmittedAt());
        response.setApprovedAt(entry.getApprovedAt());
        response.setRejectionReason(entry.getRejectionReason());
        response.setCreatedAt(entry.getCreatedAt());
        response.setUpdatedAt(entry.getUpdatedAt());
        return response;
    }
}