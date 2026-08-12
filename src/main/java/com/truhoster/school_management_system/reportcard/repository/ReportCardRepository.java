package com.truhoster.school_management_system.reportcard.repository;

import com.truhoster.school_management_system.note.enums.Trimester;
import com.truhoster.school_management_system.reportcard.entity.ReportCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportCardRepository extends JpaRepository<ReportCard, Integer> {
    /**
     * Recherche le bulletin d'un élève pour une classe et un trimestre donnés.
     */
    Optional<ReportCard> findByStudentIdAndClassroomIdAndTrimester(
            Integer studentId, Integer classroomId, Trimester trimester);
}
