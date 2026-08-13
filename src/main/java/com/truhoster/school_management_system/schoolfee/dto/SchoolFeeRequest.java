package com.truhoster.school_management_system.schoolfee.dto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolFeeRequest {

    private String label;

    @NotNull(message = "Le montant de l'inscription est obligatoire")
    private Double inscriptionAmount;

    @NotNull(message = "Le montant de la tranche 1 est obligatoire")
    private Double tranche1Amount;

    @NotNull(message = "Le delai de la tranche 1 est obligatoire")
    private LocalDate tranche1Deadline;

    @NotNull(message = "Le montant de la tranche 2 est obligatoire")
    private Double tranche2Amount;

    @NotNull(message = "Le delai de la tranche 2 est obligatoire")
    private LocalDate tranche2Deadline;

    @NotEmpty(message = "Il faut selectionner au moins une classe")
    private List<Integer> classroomIds;
}