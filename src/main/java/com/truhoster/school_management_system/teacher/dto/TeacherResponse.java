package com.truhoster.school_management_system.teacher.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TeacherResponse {
    private Integer id;
    private String name;
    private String phone;
    private List<AffectationResponse> affectations;
}
