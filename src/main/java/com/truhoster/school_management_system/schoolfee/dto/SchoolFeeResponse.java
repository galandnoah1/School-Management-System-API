package com.truhoster.school_management_system.schoolfee.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchoolFeeResponse {
    private Integer id;
    private String label;
    private Double inscriptionAmount;
    private Double tranche1Amount;
    private LocalDate tranche1Deadline;
    private Double tranche2Amount;
    private LocalDate tranche2Deadline;
    private List<ClassroomSummaryResponse> classrooms;
}
