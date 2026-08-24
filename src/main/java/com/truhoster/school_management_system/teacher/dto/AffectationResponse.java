package com.truhoster.school_management_system.teacher.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AffectationResponse {
    private Integer id;
    private String classroom;
    private String subject;
}
