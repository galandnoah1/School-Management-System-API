package com.truhoster.school_management_system.teacher.mapper;

import com.truhoster.school_management_system.teacher.dto.TeacherRequest;
import com.truhoster.school_management_system.teacher.dto.TeacherResponse;
import com.truhoster.school_management_system.teacher.entity.Teacher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeacherMapper {

    private final AffectationMapper affectationMapper;

    /**
     * Convertit un TeacherRequest en entité Teacher.
     * Ne définit pas la liste affectations (l'affectation initiale liée à classroomId/subjectId
     * est créée séparément dans TeacherService, car elle nécessite l'id du Teacher déjà persisté).
     *
     * @param request les données envoyées par le client
     * @return l'entité Teacher prête à être persistée
     */
    public Teacher toEntity(TeacherRequest request) {
        if (request == null) {
            return null;
        }

        return Teacher.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .sex(request.getSex())
                .build();
    }

    /**
     * Convertit une entité Teacher en TeacherResponse.
     * Convertit également la liste des Affectations associées via AffectationMapper.
     *
     * @param teacher l'entité à convertir
     * @return le DTO exposé au client, ou null si l'entité est null
     */
    public TeacherResponse toDTO(Teacher teacher) {
        if (teacher == null) {
            return null;
        }

        return TeacherResponse.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .phone(teacher.getPhone())
                .affectations(affectationMapper.toDTOList(teacher.getAffectations()))
                .build();
    }

    /**
     * Convertit une liste d'entités Teacher en liste de TeacherResponse.
     * Retourne une liste vide si la liste source est null (évite les NPE côté appelant).
     *
     * @param teachers la liste d'entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<TeacherResponse> toDTOList(List<Teacher> teachers) {
        if (teachers == null) {
            return Collections.emptyList();
        }
        return teachers.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}