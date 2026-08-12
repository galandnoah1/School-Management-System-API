package com.truhoster.school_management_system.note.mapper;

import com.truhoster.school_management_system.note.dto.NoteLineRequest;
import com.truhoster.school_management_system.note.dto.NoteLineResponse;
import com.truhoster.school_management_system.note.entity.NoteLine;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class NoteLineMapper {

    /**
     * Convertit un NoteLineRequest en entité NoteLine.
     * Ne définit pas le lien vers ReportCard (résolu et assigné séparément dans NoteService,
     * car il dépend du student/classroom/trimester déduits de la Note associée).
     * Ne définit pas average/notecoefficie (calculés par le service).
     *
     * @param request les données envoyées par le client
     * @return l'entité NoteLine prête à être persistée
     */
    public NoteLine toEntity(NoteLineRequest request) {
        if (request == null) {
            return null;
        }

        return NoteLine.builder()
                .subject(request.getSubject())
                .note1(request.getNote1())
                .note2(request.getNote2() !=null ? request.getNote2() : null)
                .coefficient(request.getCoefficient() != null ? request.getCoefficient() : 0)
                .appreciation(request.getAppreciation())
                .teacher(request.getTeacher())
                .trimester(request.getTrimester())
                .build();
    }

    /**
     * Convertit une entité NoteLine en NoteLineResponse.
     *
     * @param noteLine l'entité à convertir
     * @return le DTO exposé au client, ou null si l'entité est null
     */
    public NoteLineResponse toDTO(NoteLine noteLine) {
        if (noteLine == null) {
            return null;
        }

        return NoteLineResponse.builder()
                .id(noteLine.getId())
                .subject(noteLine.getSubject())
                .note1(noteLine.getNote1())
                .note2(noteLine.getNote2())
                .average(noteLine.getAverage())
                .coefficient(noteLine.getCoefficient())
                .notecoefficie(noteLine.getNotecoefficie())
                .appreciation(noteLine.getAppreciation())
                .teacher(noteLine.getTeacher())
                .trimester(noteLine.getTrimester() != null ? noteLine.getTrimester().name() : null)
                .build();
    }

    /**
     * Convertit une liste d'entités NoteLine en liste de NoteLineResponse.
     * Retourne une liste vide si la liste source est null (évite les NPE côté appelant).
     *
     * @param noteLines la liste d'entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<NoteLineResponse> toDTOList(List<NoteLine> noteLines) {
        if (noteLines == null) {
            return Collections.emptyList();
        }
        return noteLines.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}