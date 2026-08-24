package com.truhoster.school_management_system.teacher.repository;

import com.truhoster.school_management_system.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Integer> {

    /**
     * Récupère un Teacher avec ses affectations chargées en une seule requête
     * (évite le LazyInitializationException en dehors du contexte transactionnel).
     */
    @Query("SELECT t FROM Teacher t LEFT JOIN FETCH t.affectations WHERE t.id = :id")
    Optional<Teacher> findByIdWithAffectations(@Param("id") Integer id);

    /**
     * Récupère tous les Teachers avec leurs affectations chargées.
     */
    @Query("SELECT DISTINCT t FROM Teacher t LEFT JOIN FETCH t.affectations")
    List<Teacher> findAllWithAffectations();
}
