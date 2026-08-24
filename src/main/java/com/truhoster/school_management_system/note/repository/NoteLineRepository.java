package com.truhoster.school_management_system.note.repository;

import com.truhoster.school_management_system.note.entity.NoteLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteLineRepository extends JpaRepository<NoteLine,Integer> {
    /**
     * Recherche la ligne de bulletin d'une matière donnée dans un bulletin donné.
     */
    Optional<NoteLine> findByReportCardIdAndSubject(Integer reportCardId, String subject);

    /**
     * Récupère toutes les lignes de matière d'un bulletin donné.
     */
    List<NoteLine> findByReportCardId(Integer reportCardId);
}
