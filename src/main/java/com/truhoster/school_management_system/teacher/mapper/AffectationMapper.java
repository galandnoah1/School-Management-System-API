package com.truhoster.school_management_system.teacher.mapper;

import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.classroom.repository.ClassroomRepository;
import com.truhoster.school_management_system.subject.entity.Subject;
import com.truhoster.school_management_system.subject.repository.SubjectRepository;
import com.truhoster.school_management_system.teacher.dto.AffectationRequest;
import com.truhoster.school_management_system.teacher.dto.AffectationResponse;
import com.truhoster.school_management_system.teacher.entity.Affectation;
import com.truhoster.school_management_system.teacher.entity.Teacher;
import com.truhoster.school_management_system.teacher.repository.TeacherRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AffectationMapper {

    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final SubjectRepository subjectRepository;

    /**
     * Convertit un AffectationRequest en entité Affectation.
     * Récupère le Teacher, la Classroom et le Subject correspondants à partir de leurs ids.
     *
     * @param request les données envoyées par le client
     * @return l'entité Affectation prête à être persistée
     * @throws EntityNotFoundException si le professeur, la classe ou la matière n'existe pas
     */
    public Affectation toEntity(AffectationRequest request) {
        if (request == null) {
            return null;
        }

        Teacher teacher = teacherRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Teacher not found with id: " + request.getTeacherId()));

        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Classroom not found with id: " + request.getClassroomId()));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subject not found with id: " + request.getSubjectId()));

        return Affectation.builder()
                .teacher(teacher)
                .classroom(classroom)
                .subject(subject)
                .build();
    }

    /**
     * Convertit une entité Affectation en AffectationResponse.
     * Les champs "classroom" et "subject" du DTO reprennent respectivement
     * le nom de la classe et le nom de la matière associées.
     *
     * @param affectation l'entité à convertir
     * @return le DTO exposé au client, ou null si l'entité est null
     */
    public AffectationResponse toDTO(Affectation affectation) {
        if (affectation == null) {
            return null;
        }

        return AffectationResponse.builder()
                .id(affectation.getId())
                .classroom(affectation.getClassroom() != null
                        ? affectation.getClassroom().getName()
                        : null)
                .subject(affectation.getSubject() != null
                        ? affectation.getSubject().getName()
                        : null)
                .build();
    }

    /**
     * Convertit une liste d'entités Affectation en liste de AffectationResponse.
     * Retourne une liste vide si la liste source est null (évite les NPE côté appelant).
     *
     * @param affectations la liste d'entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<AffectationResponse> toDTOList(List<Affectation> affectations) {
        if (affectations == null) {
            return Collections.emptyList();
        }
        return affectations.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}