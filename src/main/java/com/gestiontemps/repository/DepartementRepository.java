package com.gestiontemps.repository;

import com.gestiontemps.entite.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Departement, Long> {
    Optional<Departement> findByName(String name);
    boolean existsByName(String name);
}