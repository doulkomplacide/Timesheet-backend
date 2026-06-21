package com.gestiontemps.repository;

import com.gestiontemps.entite.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Utilisateur> findByDepartmentId(Long departmentId);

    List<Utilisateur> findByServiceId(Long serviceId);

    List<Utilisateur> findByActive(boolean active);

    @Query("SELECT u FROM Utilisateur u WHERE u.role = 'ADMIN'")
    List<Utilisateur> findAllAdmins();

    @Query("SELECT u FROM Utilisateur u WHERE u.role = 'MANAGER'")
    List<Utilisateur> findAllManagers();

    @Query("SELECT u FROM Utilisateur u WHERE u.department.id = :departmentId AND u.active = true")
    List<Utilisateur> findActiveByDepartmentId(@Param("departmentId") Long departmentId);

    @Query("SELECT u FROM Utilisateur u WHERE u.service.id = :serviceId AND u.active = true")
    List<Utilisateur> findActiveByServiceId(@Param("serviceId") Long serviceId);
}