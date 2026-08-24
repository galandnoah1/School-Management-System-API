package com.truhoster.school_management_system.reportcard.mapper;

import com.truhoster.school_management_system.note.mapper.NoteLineMapper;
import com.truhoster.school_management_system.reportcard.dto.ReportCardResponse;
import com.truhoster.school_management_system.reportcard.entity.ReportCard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ReportCardMapper {

    private final NoteLineMapper noteLineMapper;

    /**
     * Convertit une entité ReportCard en ReportCardResponse.
     * Les champs "student" et "classroom" reprennent respectivement l'identité de l'élève
     * et le nom de la classe. La liste noteLines est convertie via NoteLineMapper.
     *
     * @param reportCard l'entité à convertir
     * @return le DTO exposé au client, ou null si l'entité est null
     */
    public ReportCardResponse toDTO(ReportCard reportCard) {
        if (reportCard == null) {
            return null;
        }

        return ReportCardResponse.builder()
                .id(reportCard.getId())
                .ranking(reportCard.getRanking())
                .average(reportCard.getAverage())
                .average1(reportCard.getAverage1())
                .average2(reportCard.getAverage2())
                .firstaverage(reportCard.getFirstaverage())
                .lastaverage(reportCard.getLastaverage())
                .overallaverage(reportCard.getOverallaverage())
                .appreciation(reportCard.getAppreciation())
                .trimester(reportCard.getTrimester() != null ? reportCard.getTrimester().name() : null)
                .student(reportCard.getStudent() != null
                        ? reportCard.getStudent().getFirstname() + " " + reportCard.getStudent().getLastname()
                        : null)
                .classroom(reportCard.getClassroom() != null ? reportCard.getClassroom().getName() : null)
                .noteLines(noteLineMapper.toDTOList(reportCard.getNoteLines()))
                .build();
    }

    /**
     * Convertit une liste d'entités ReportCard en liste de ReportCardResponse.
     *
     * @param reportCards la liste d'entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<ReportCardResponse> toDTOList(List<ReportCard> reportCards) {
        if (reportCards == null) {
            return Collections.emptyList();
        }
        return reportCards.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}