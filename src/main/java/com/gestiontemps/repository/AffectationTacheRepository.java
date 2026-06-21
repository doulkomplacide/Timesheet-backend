package com.gestiontemps.repository;

import com.gestiontemps.entite.AffectationTache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<AffectationTache, Long> {
    List<AffectationTache> findByUserId(Long userId);
    List<AffectationTache> findByTaskId(Long taskId);
    Optional<AffectationTache> findByTaskIdAndUserId(Long taskId, Long userId);
    void deleteByTaskIdAndUserId(Long taskId, Long userId);
    boolean existsByTaskIdAndUserId(Long taskId, Long userId);
}