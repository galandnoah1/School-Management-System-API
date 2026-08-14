package com.truhoster.school_management_system.printhistory.mapper;

import com.truhoster.school_management_system.printhistory.dto.PrintHistoryResponse;
import com.truhoster.school_management_system.printhistory.entity.PrintHistory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PrintHistoryMapper {

    /**
     * Convertit une entité PrintHistory en PrintHistoryResponse.
     * "student" n'est renseigné que si reportCard est présent (PRINTED/DOWNLOADED) ;
     * reste null pour une ligne GENERATED.
     *
     * @param printHistory l'entité à convertir
     * @return le DTO exposé au client, ou null si l'entité est null
     */
    public PrintHistoryResponse toDTO(PrintHistory printHistory) {
        if (printHistory == null) {
            return null;
        }

        boolean hasReportCard = printHistory.getReportCard() != null;

        return PrintHistoryResponse.builder()
                .id(printHistory.getId())
                .action(printHistory.getAction().name())
                .reportCardId(hasReportCard ? printHistory.getReportCard().getId() : null)
                .student(hasReportCard
                        ? printHistory.getReportCard().getStudent().getFirstname() + " "
                          + printHistory.getReportCard().getStudent().getLastname()
                        : null)
                .classroom(printHistory.getClassroom() != null ? printHistory.getClassroom().getName() : null)
                .trimester(printHistory.getTrimester() != null ? printHistory.getTrimester().name() : null)
                .count(printHistory.getCount())
                .performedBy(printHistory.getPerformedBy())
                .performedAt(printHistory.getPerformedAt())
                .updatedAt(printHistory.getUpdatedAt())
                .build();
    }

    /**
     * Convertit une liste d'entités PrintHistory en liste de PrintHistoryResponse.
     *
     * @param printHistories la liste d'entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<PrintHistoryResponse> toDTOList(List<PrintHistory> printHistories) {
        if (printHistories == null) {
            return Collections.emptyList();
        }
        return printHistories.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}