package com.truhoster.school_management_system.schoolfee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomSummaryResponse {
    private Integer id;
    private String name;
}