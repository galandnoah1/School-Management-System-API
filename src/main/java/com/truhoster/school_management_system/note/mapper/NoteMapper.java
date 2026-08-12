package com.truhoster.school_management_system.note.mapper;

import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.classroom.repository.ClassroomRepository;
import com.truhoster.school_management_system.note.dto.NoteRequest;
import com.truhoster.school_management_system.note.dto.NoteResponse;
import com.truhoster.school_management_system.note.entity.Note;
import com.truhoster.school_management_system.student.entity.Student;
import com.truhoster.school_management_system.student.repository.StudentRepository;
import com.truhoster.school_management_system.subject.entity.Subject;
import com.truhoster.school_management_system.subject.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class NoteMapper {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final ClassroomRepository classroomRepository;

    /**
     * Convertit un NoteRequest en entité Note.
     * Récupère le Student, le Subject et la Classroom correspondants à partir de leurs ids.
     *
     * @param request les données envoyées par le client
     * @return l'entité Note prête à être persistée
     * @throws EntityNotFoundException si l'élève, la matière ou la classe n'existe pas
     */
    public Note toEntity(NoteRequest request) {
        if (request == null) {
            return null;
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Student not found with id: " + request.getStudentId()));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subject not found with id: " + request.getSubjectId()));

        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Classroom not found with id: " + request.getClassroomId()));

        return Note.builder()
                .note(request.getNote())
                .evaluation(request.getEvaluation())
                .student(student)
                .subject(subject)
                .classroom(classroom)
                .build();
    }

    /**
     * Convertit une entité Note en NoteResponse.
     * Les champs "subject", "classroom" et "student" du DTO reprennent respectivement
     * le nom de la matière, le nom de la classe et l'identité de l'élève.
     *
     * @param note l'entité à convertir
     * @return le DTO exposé au client, ou null si l'entité est null
     */
    public NoteResponse toDTO(Note note) {
        if (note == null) {
            return null;
        }

        return NoteResponse.builder()
                .id(note.getId())
                .note(note.getNote())
                .subject(note.getSubject() != null ? note.getSubject().getName() : null)
                .classroom(note.getClassroom() != null ? note.getClassroom().getName() : null)
                .student(note.getStudent() != null
                        ? note.getStudent().getFirstname() + " " + note.getStudent().getLastname()
                        : null)
                .evaluation(note.getEvaluation() != null ? note.getEvaluation().name() : null)
                .build();
    }

    /**
     * Convertit une liste d'entités Note en liste de NoteResponse.
     * Retourne une liste vide si la liste source est null (évite les NPE côté appelant).
     *
     * @param notes la liste d'entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<NoteResponse> toDTOList(List<Note> notes) {
        if (notes == null) {
            return Collections.emptyList();
        }
        return notes.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}