package com.gestiontemps.repository;

import com.gestiontemps.enums.StatutTache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Tache, Long> {
    Optional<Tache> findByCode(String code);
    boolean existsByCode(String code);
    List<Tache> findByProjectId(Long projectId);
    List<Tache> findByStatus(StatutTache status);
    
    @Query("SELECT t FROM Tache t JOIN t.assignments a WHERE a.user.id = :userId")
    List<Tache> findTasksByUserId(@Param("userId") Long userId);
    
    @Query("SELECT t FROM Tache t WHERE t.project.id = :projectId AND t.status = :status")
    List<Tache> findByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") StatutTache status);
}