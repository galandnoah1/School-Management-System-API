package com.truhoster.school_management_system.printhistory.repository;

import com.truhoster.school_management_system.note.enums.Trimester;
import com.truhoster.school_management_system.printhistory.entity.PrintHistory;
import com.truhoster.school_management_system.printhistory.enums.PrintAction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface PrintHistoryRepository extends JpaRepository<PrintHistory, Integer> {

    /**
     * Recherche la ligne GENERATED existante pour une classe/trimestre (pas de bulletin associé).
     */
    Optional<PrintHistory> findByClassroomIdAndTrimesterAndActionAndReportCardIsNull(
            Integer classroomId, Trimester trimester, PrintAction action);

    /**
     * Recherche la ligne PRINTED ou DOWNLOADED existante pour un bulletin donné.
     */
    Optional<PrintHistory> findByReportCardIdAndAction(Integer reportCardId, PrintAction action);

    /**
     * Historique complet d'une classe pour un trimestre donné (toutes actions confondues).
     */
    List<PrintHistory> findByClassroomIdAndTrimester(Integer classroomId, Trimester trimester);

    /**
     * Lignes d'une classe/trimestre pour une action donnée (utilisé pour sommer les count,
     * ex: total des impressions de toute la classe).
     */
    List<PrintHistory> findByClassroomIdAndTrimesterAndAction(
            Integer classroomId, Trimester trimester, PrintAction action);

    List<PrintHistory> findByPerformedAtBetween(Timestamp start, Timestamp end);
}