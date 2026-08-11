package com.truhoster.school_management_system.subject.repository;

import com.truhoster.school_management_system.classroom.enums.Section;
import com.truhoster.school_management_system.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    /**
     * Récupère un Subject avec sa liste subjectByClassrooms chargée en une seule requête
     * (évite le LazyInitializationException en dehors du contexte transactionnel).
     */
    @Query("SELECT s FROM Subject s LEFT JOIN FETCH s.subjectByClassrooms WHERE s.id = :id")
    Optional<Subject> findByIdWithClassrooms(@Param("id") Integer id);

    /**
     * Récupère tous les Subjects avec leurs subjectByClassrooms chargés en une seule requête.
     */
    @Query("SELECT DISTINCT s FROM Subject s LEFT JOIN FETCH s.subjectByClassrooms")
    List<Subject> findAllWithClassrooms();

    /**
     * Récupère tous les Subjects d'une section donnée, avec leurs subjectByClassrooms chargés.
     */
    @Query("SELECT DISTINCT s FROM Subject s LEFT JOIN FETCH s.subjectByClassrooms WHERE s.section = :section")
    List<Subject> findBySectionWithClassrooms(@Param("section") Section section);
}
