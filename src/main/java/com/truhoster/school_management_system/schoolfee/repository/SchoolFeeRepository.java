package com.truhoster.school_management_system.schoolfee.repository;

import com.truhoster.school_management_system.schoolfee.entity.SchoolFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SchoolFeeRepository extends JpaRepository<SchoolFee, Integer> {

    /**
     * Recherche la grille de frais contenant une classe donnée.
     * Utilisée pour la vérification d'unicité et pour retrouver la grille applicable à un élève.
     */
    @Query("SELECT sf FROM SchoolFee sf JOIN sf.classrooms c WHERE c.id = :classroomId")
    Optional<SchoolFee> findByClassroomId(@Param("classroomId") Integer classroomId);
}