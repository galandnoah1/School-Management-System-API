package com.truhoster.school_management_system.teacher.repository;

import com.truhoster.school_management_system.teacher.entity.Affectation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AffectationRepository extends JpaRepository<Affectation, Integer> {
    /**
     * Vérifie si une affectation existe déjà pour ce trio prof/classe/matière.
     */
    boolean existsByTeacherIdAndClassroomIdAndSubjectId(
            Integer teacherId, Integer classroomId, Integer subjectId);

    /**
     * Récupère toutes les affectations liées à une classe donnée.
     * Utile pour retrouver les enseignants d'une classe.
     */
    List<Affectation> findByClassroomId(Integer classroomId);

    /**
     * Recherche l'affectation (donc le professeur) pour une classe et une matière données.
     */
    Optional<Affectation> findByClassroomIdAndSubjectId(Integer classroomId, Integer subjectId);
}
