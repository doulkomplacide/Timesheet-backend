package com.timesheet.repository;

import com.timesheet.entity.TimesheetEntry;
import com.timesheet.enums.EntryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimesheetEntryRepository extends JpaRepository<TimesheetEntry, Long> {

    // =============================================
    // 1. RECHERCHES DE BASE
    // =============================================

    /**
     * Trouve les entrées d'un utilisateur entre deux dates
     */
    List<TimesheetEntry> findByUserIdAndEntryDateBetween(
        Long userId, 
        LocalDate startDate, 
        LocalDate endDate
    );

    /**
     * Trouve les entrées d'un utilisateur pour une date spécifique
     */
    List<TimesheetEntry> findByUserIdAndEntryDate(
        Long userId, 
        LocalDate entryDate
    );

    /**
     * Trouve les entrées d'un utilisateur pour une tâche et une date spécifiques
     */
    List<TimesheetEntry> findByUserIdAndTaskIdAndEntryDate(
        Long userId, 
        Long taskId, 
        LocalDate entryDate
    );

    /**
     * Trouve les entrées d'un utilisateur pour une tâche spécifique
     */
    List<TimesheetEntry> findByUserIdAndTaskId(
        Long userId, 
        Long taskId
    );


    /**
     * Trouve les entrées d'un utilisateur par statut
     */
    List<TimesheetEntry> findByUserIdAndStatus(
        Long userId, 
        EntryStatus status
    );

    // =============================================
    // 2. RECHERCHES AVEC CRITÈRES MULTIPLES
    // =============================================

    /**
     * Trouve les entrées d'un utilisateur entre deux dates avec un statut spécifique
     */
    List<TimesheetEntry> findByUserIdAndEntryDateBetweenAndStatus(
        Long userId,
        LocalDate startDate,
        LocalDate endDate,
        EntryStatus status
    );

    /**
     * Trouve les entrées d'un utilisateur pour une tâche entre deux dates
     */
    List<TimesheetEntry> findByUserIdAndTaskIdAndEntryDateBetween(
        Long userId,
        Long taskId,
        LocalDate startDate,
        LocalDate endDate
    );

    /**
     * Trouve les entrées par tâche et statut
     */
    List<TimesheetEntry> findByTaskIdAndStatus(
        Long taskId,
        EntryStatus status
    );

    // =============================================
    // 3. RECHERCHES AVEC AGGREGATIONS
    // =============================================

    /**
     * Calcule le total des heures d'un utilisateur entre deux dates
     */
    @Query("SELECT COALESCE(SUM(t.hoursWorked), 0) FROM TimesheetEntry t " +
           "WHERE t.user.id = :userId AND t.entryDate BETWEEN :startDate AND :endDate")
    Double sumHoursByUserAndDateRange(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Calcule le total des heures d'un utilisateur pour une tâche entre deux dates
     */
    @Query("SELECT COALESCE(SUM(t.hoursWorked), 0) FROM TimesheetEntry t " +
           "WHERE t.user.id = :userId AND t.task.id = :taskId " +
           "AND t.entryDate BETWEEN :startDate AND :endDate")
    Double sumHoursByUserAndTaskAndDateRange(
        @Param("userId") Long userId,
        @Param("taskId") Long taskId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Calcule le total des heures pour une tâche entre deux dates
     */
    @Query("SELECT COALESCE(SUM(t.hoursWorked), 0) FROM TimesheetEntry t " +
           "WHERE t.task.id = :taskId AND t.entryDate BETWEEN :startDate AND :endDate")
    Double sumHoursByTaskAndDateRange(
        @Param("taskId") Long taskId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Calcule le total des heures pour un projet entre deux dates
     */
    @Query("SELECT COALESCE(SUM(t.hoursWorked), 0) FROM TimesheetEntry t " +
           "WHERE t.task.project.id = :projectId AND t.entryDate BETWEEN :startDate AND :endDate")
    Double sumHoursByProjectAndDateRange(
        @Param("projectId") Long projectId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // =============================================
    // 4. STATISTIQUES AVEC GROUP BY
    // =============================================

    /**
     * Compte le nombre d'entrées par statut pour un utilisateur
     */
    @Query("SELECT t.status, COUNT(t) FROM TimesheetEntry t " +
           "WHERE t.user.id = :userId AND t.entryDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.status")
    List<Object[]> countEntriesByStatusForUser(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Compte le nombre d'entrées par statut pour une tâche
     */
    @Query("SELECT t.status, COUNT(t) FROM TimesheetEntry t " +
           "WHERE t.task.id = :taskId GROUP BY t.status")
    List<Object[]> countEntriesByStatusForTask(@Param("taskId") Long taskId);

    /**
     * Heures par jour pour un utilisateur
     */
    @Query("SELECT t.entryDate, COALESCE(SUM(t.hoursWorked), 0) FROM TimesheetEntry t " +
           "WHERE t.user.id = :userId AND t.entryDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.entryDate ORDER BY t.entryDate")
    List<Object[]> getHoursByDayForUser(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Heures par projet pour un utilisateur
     */
    @Query("SELECT t.task.project.name, COALESCE(SUM(t.hoursWorked), 0) FROM TimesheetEntry t " +
           "WHERE t.user.id = :userId AND t.entryDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.task.project.name")
    List<Object[]> getHoursByProjectForUser(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Heures par tâche pour un utilisateur
     */
    @Query("SELECT t.task.name, COALESCE(SUM(t.hoursWorked), 0) FROM TimesheetEntry t " +
           "WHERE t.user.id = :userId AND t.entryDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.task.name")
    List<Object[]> getHoursByTaskForUser(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // =============================================
    // 5. RECHERCHES POUR APPROBATION
    // =============================================

    /**
     * Trouve les entrées soumises en attente d'approbation
     */
    List<TimesheetEntry> findByStatus(EntryStatus status);

    /**
     * Trouve les entrées soumises pour un utilisateur spécifique
     */
    List<TimesheetEntry> findByUserIdAndStatusOrderByEntryDateDesc(
        Long userId, 
        EntryStatus status
    );

    /**
     * Trouve les entrées soumises entre deux dates
     */
    List<TimesheetEntry> findByStatusAndEntryDateBetween(
        EntryStatus status,
        LocalDate startDate,
        LocalDate endDate
    );

    /**
     * Trouve les entrées soumises pour un département
     */
    @Query("SELECT t FROM TimesheetEntry t " +
           "WHERE t.status = :status AND t.user.department.id = :departmentId")
    List<TimesheetEntry> findByStatusAndDepartmentId(
        @Param("status") EntryStatus status,
        @Param("departmentId") Long departmentId
    );

    /**
     * Trouve les entrées soumises pour un service
     */
    @Query("SELECT t FROM TimesheetEntry t " +
           "WHERE t.status = :status AND t.user.service.id = :serviceId")
    List<TimesheetEntry> findByStatusAndServiceId(
        @Param("status") EntryStatus status,
        @Param("serviceId") Long serviceId
    );

    // =============================================
    // 6. VÉRIFICATIONS D'EXISTENCE
    // =============================================

    /**
     * Vérifie si une entrée existe pour un utilisateur, une tâche et une date
     */
    boolean existsByUserIdAndTaskIdAndEntryDate(
        Long userId,
        Long taskId,
        LocalDate entryDate
    );

    /**
     * Vérifie si un utilisateur a des entrées en brouillon pour une période
     */
    boolean existsByUserIdAndStatusAndEntryDateBetween(
        Long userId,
        EntryStatus status,
        LocalDate startDate,
        LocalDate endDate
    );

    /**
     * Vérifie si une tâche a des entrées associées
     */
    boolean existsByTaskId(Long taskId);

    /**
     * Vérifie si un utilisateur a des entrées associées
     */
    boolean existsByUserId(Long userId);

    // =============================================
    // 7. RECHERCHES AVEC PAGINATION ET TRI
    // =============================================

    /**
     * Trouve les entrées d'un utilisateur avec pagination
     */
    org.springframework.data.domain.Page<TimesheetEntry> findByUserId(
        Long userId,
        org.springframework.data.domain.Pageable pageable
    );

    /**
     * Trouve les entrées par statut avec pagination
     */
    org.springframework.data.domain.Page<TimesheetEntry> findByStatus(
        EntryStatus status,
        org.springframework.data.domain.Pageable pageable
    );

    /**
     * Trouve les entrées d'un utilisateur par statut avec pagination
     */
    org.springframework.data.domain.Page<TimesheetEntry> findByUserIdAndStatus(
        Long userId,
        EntryStatus status,
        org.springframework.data.domain.Pageable pageable
    );

    // =============================================
    // 8. RECHERCHES AVEC DATES SPÉCIFIQUES
    // =============================================

    /**
     * Trouve les entrées soumises aujourd'hui
     */
    @Query("SELECT t FROM TimesheetEntry t " +
           "WHERE t.submittedAt >= :startOfDay AND t.submittedAt <= :endOfDay")
    List<TimesheetEntry> findSubmittedToday(
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );

    /**
     * Trouve les entrées approuvées aujourd'hui
     */
    @Query("SELECT t FROM TimesheetEntry t " +
           "WHERE t.approvedAt >= :startOfDay AND t.approvedAt <= :endOfDay")
    List<TimesheetEntry> findApprovedToday(
        @Param("startOfDay") LocalDateTime startOfDay,
        @Param("endOfDay") LocalDateTime endOfDay
    );

    /**
     * Trouve les entrées d'une semaine spécifique
     */
    @Query("SELECT t FROM TimesheetEntry t " +
           "WHERE t.user.id = :userId AND t.entryDate >= :startDate AND t.entryDate <= :endDate")
    List<TimesheetEntry> findByUserIdAndWeek(
        @Param("userId") Long userId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Trouve les entrées d'un mois spécifique
     */
    @Query("SELECT t FROM TimesheetEntry t " +
           "WHERE t.user.id = :userId AND YEAR(t.entryDate) = :year AND MONTH(t.entryDate) = :month")
    List<TimesheetEntry> findByUserIdAndMonth(
        @Param("userId") Long userId,
        @Param("year") int year,
        @Param("month") int month
    );

    // =============================================
    // 9. RECHERCHES POUR RAPPORTS
    // =============================================

    /**
     * Obtient les heures totales par utilisateur pour une période
     */
    @Query("SELECT t.user.id, t.user.firstName, t.user.lastName, COALESCE(SUM(t.hoursWorked), 0) " +
           "FROM TimesheetEntry t " +
           "WHERE t.entryDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.user.id")
    List<Object[]> getHoursByUserForPeriod(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Obtient les heures totales par projet pour une période
     */
    @Query("SELECT t.task.project.id, t.task.project.name, COALESCE(SUM(t.hoursWorked), 0) " +
           "FROM TimesheetEntry t " +
           "WHERE t.entryDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.task.project.id")
    List<Object[]> getHoursByProjectForPeriod(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Obtient les heures totales par département pour une période
     */
    @Query("SELECT t.user.department.id, t.user.department.name, COALESCE(SUM(t.hoursWorked), 0) " +
           "FROM TimesheetEntry t " +
           "WHERE t.entryDate BETWEEN :startDate AND :endDate " +
           "GROUP BY t.user.department.id")
    List<Object[]> getHoursByDepartmentForPeriod(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    /**
     * Obtient les entrées non approuvées depuis plus de X jours
     */
    @Query("SELECT t FROM TimesheetEntry t " +
           "WHERE t.status = :status AND t.submittedAt <= :thresholdDate")
    List<TimesheetEntry> findPendingEntriesOlderThan(
        @Param("status") EntryStatus status,
        @Param("thresholdDate") LocalDateTime thresholdDate
    );

    // =============================================
    // 10. MÉTHODES DE SUPPRESSION EN MASSE
    // =============================================

    /**
     * Supprime toutes les entrées d'une tâche
     */
    void deleteByTaskId(Long taskId);

    /**
     * Supprime toutes les entrées d'un utilisateur
     */
    void deleteByUserId(Long userId);

    /**
     * Supprime les entrées en brouillon plus anciennes que X jours
     */
    @Query("DELETE FROM TimesheetEntry t WHERE t.status = 'DRAFT' AND t.createdAt <= :thresholdDate")
    void deleteDraftEntriesOlderThan(@Param("thresholdDate") LocalDateTime thresholdDate);
}