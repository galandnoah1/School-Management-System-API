package com.truhoster.school_management_system.reportcard.repository;

import com.truhoster.school_management_system.note.enums.Trimester;
import com.truhoster.school_management_system.reportcard.entity.ReportCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReportCardRepository extends JpaRepository<ReportCard, Integer> {
    /**
     * Recherche le bulletin d'un élève pour une classe et un trimestre donnés.
     */
    Optional<ReportCard> findByStudentIdAndClassroomIdAndTrimester(
            Integer studentId, Integer classroomId, Trimester trimester);

    /**
     * Récupère tous les bulletins d'une classe pour un trimestre donné.
     * Nécessaire pour générer le classement et les statistiques de classe.
     */
    List<ReportCard> findByClassroomIdAndTrimester(Integer classroomId, Trimester trimester);

    /**
     * Recherche le bulletin d'un élève pour un trimestre donné (indépendamment de la classe,
     * puisqu'un élève n'appartient qu'à une seule classe à un instant T).
     */
    Optional<ReportCard> findByStudentIdAndTrimester(Integer studentId, Trimester trimester);
}
