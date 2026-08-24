package com.truhoster.school_management_system.subject.repository;

import com.truhoster.school_management_system.subject.entity.SubjectByClassroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectByClassroomRepository extends JpaRepository<SubjectByClassroom, Integer> {
    /**
     * Vérifie si une association existe déjà entre une classe et une matière données.
     */
    boolean existsByClassroomIdAndSubjectId(Integer classroomId, Integer subjectId);

    /**
     * Recherche l'association classe/matière pour en récupérer le coefficient.
     */
    Optional<SubjectByClassroom> findByClassroomIdAndSubjectId(Integer classroomId, Integer subjectId);

    /**
     * Récupère toutes les associations matière/classe pour une classe donnée.
     * Permet d'en extraire les matières enseignées dans cette classe.
     */
    List<SubjectByClassroom> findByClassroomId(Integer classroomId);
}
