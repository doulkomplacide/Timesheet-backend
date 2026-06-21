package com.gestiontemps.repository;

import com.gestiontemps.entite.Projet;
import com.gestiontemps.enums.StatutProjet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Projet, Long> {
    Optional<Projet> findByCode(String code);
    boolean existsByCode(String code);
    List<Projet> findByServiceId(Long serviceId);
    List<Projet> findByStatus(StatutProjet status);
    
    @Query("SELECT p FROM Projet p WHERE p.service.department.id = :departmentId")
    List<Projet> findByDepartmentId(@Param("departmentId") Long departmentId);
    
    @Query("SELECT p FROM Projet p JOIN p.tasks t JOIN t.assignments a WHERE a.user.id = :userId")
    List<Projet> findProjectsByUserId(@Param("userId") Long userId);
}