package com.truhoster.school_management_system.subject.mapper;

import com.truhoster.school_management_system.subject.dto.SubjectRequest;
import com.truhoster.school_management_system.subject.dto.SubjectResponse;
import com.truhoster.school_management_system.subject.entity.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SubjectMapper {

    private final SubjectByClassroomMapper subjectByClassroomMapper;

    /**
     * Convertit un SubjectRequest en entité Subject.
     * Ne définit pas la liste subjectByClassrooms (gérée séparément via son propre endpoint).
     *
     * @param request les données envoyées par le client
     * @return l'entité Subject prête à être persistée
     */
    public Subject toEntity(SubjectRequest request) {
        if (request == null) {
            return null;
        }

        return Subject.builder()
                .name(request.getName())
                .section(request.getSection())
                .subjectGroup(request.getSubjectGroup())
                .build();
    }

    /**
     * Convertit une entité Subject en SubjectResponse.
     * Convertit également la liste des SubjectByClassroom associées via SubjectByClassroomMapper.
     *
     * @param subject l'entité à convertir
     * @return le DTO exposé au client, ou null si l'entité est null
     */
    public SubjectResponse toDTO(Subject subject) {
        if (subject == null) {
            return null;
        }

        return SubjectResponse.builder()
                .id(subject.getId())
                .name(subject.getName())
                .section(subject.getSection())
                .subjectGroup(subject.getSubjectGroup())
                .classrooms(subjectByClassroomMapper.toDTOList(subject.getSubjectByClassrooms()))
                .build();
    }

    /**
     * Convertit une liste d'entités Subject en liste de SubjectResponse.
     * Retourne une liste vide si la liste source est null (évite les NPE côté appelant).
     *
     * @param subjects la liste d'entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<SubjectResponse> toDTOList(List<Subject> subjects) {
        if (subjects == null) {
            return Collections.emptyList();
        }
        return subjects.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}