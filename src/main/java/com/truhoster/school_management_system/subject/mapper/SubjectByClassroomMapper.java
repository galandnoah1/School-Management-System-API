package com.truhoster.school_management_system.subject.mapper;

import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.classroom.repository.ClassroomRepository;
import com.truhoster.school_management_system.subject.dto.SubjectByClassroomRequest;
import com.truhoster.school_management_system.subject.dto.SubjectByClassroomResponse;
import com.truhoster.school_management_system.subject.entity.Subject;
import com.truhoster.school_management_system.subject.entity.SubjectByClassroom;
import com.truhoster.school_management_system.subject.repository.SubjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubjectByClassroomMapper {

    private final ClassroomRepository classroomRepository;
    private final SubjectRepository subjectRepository;

    /**
     * Convertit un SubjectByClassroomRequest en entité SubjectByClassroom.
     * Récupère la Classroom et le Subject correspondants à partir de leurs ids.
     *
     * @param request les données envoyées par le client
     * @return l'entité SubjectByClassroom prête à être persistée
     * @throws EntityNotFoundException si la classe ou la matière n'existe pas
     */
    public SubjectByClassroom toEntity(SubjectByClassroomRequest request) {
        if (request == null) {
            return null;
        }

        Classroom classroom = classroomRepository.findById(request.getClassroomId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Classroom not found with id: " + request.getClassroomId()));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Subject not found with id: " + request.getSubjectId()));

        return SubjectByClassroom.builder()
                .coefficient(request.getCoefficient())
                .classroom(classroom)
                .subject(subject)
                .build();
    }

    /**
     * Convertit une entité SubjectByClassroom en SubjectByClassroomResponse.
     * Le champ "classroom" du DTO reprend le nom de la classe associée.
     *
     * @param subjectByClassroom l'entité à convertir
     * @return le DTO exposé au client, ou null si l'entité est null
     */
    public SubjectByClassroomResponse toDTO(SubjectByClassroom subjectByClassroom) {
        if (subjectByClassroom == null) {
            return null;
        }

        return SubjectByClassroomResponse.builder()
                .coefficient(subjectByClassroom.getCoefficient())
                .classroom(subjectByClassroom.getClassroom() != null
                        ? subjectByClassroom.getClassroom().getName()
                        : null)
                .build();
    }

    /**
     * Convertit une liste d'entités SubjectByClassroom en liste de SubjectByClassroomResponse.
     * Retourne une liste vide si la liste source est null (évite les NPE côté appelant).
     *
     * @param subjectByClassrooms la liste d'entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<SubjectByClassroomResponse> toDTOList(List<SubjectByClassroom> subjectByClassrooms) {
        if (subjectByClassrooms == null) {
            return Collections.emptyList();
        }
        return subjectByClassrooms.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}