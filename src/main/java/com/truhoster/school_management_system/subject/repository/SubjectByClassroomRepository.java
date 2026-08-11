package com.truhoster.school_management_system.subject.repository;

import com.truhoster.school_management_system.subject.entity.SubjectByClassroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectByClassroomRepository extends JpaRepository<SubjectByClassroom, Integer> {
    /**
     * Vérifie si une association existe déjà entre une classe et une matière données.
     */
    boolean existsByClassroomIdAndSubjectId(Integer classroomId, Integer subjectId);
}
