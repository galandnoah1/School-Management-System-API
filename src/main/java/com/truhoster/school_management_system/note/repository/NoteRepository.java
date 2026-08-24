package com.truhoster.school_management_system.note.repository;

import com.truhoster.school_management_system.note.entity.Note;
import com.truhoster.school_management_system.note.enums.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Integer> {
    /**
     * Recherche les notes d'une classe pour une matière et une évaluation données.
     */
    List<Note> findByClassroomIdAndSubjectIdAndEvaluation(
            Integer classroomId, Integer subjectId, Evaluation evaluation);

    Boolean existsByStudentIdAndSubjectIdAndEvaluation(Integer studentId, Integer subjectId, Evaluation evaluation);
}
