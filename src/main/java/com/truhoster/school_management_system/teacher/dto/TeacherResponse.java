package com.truhoster.school_management_system.teacher.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TeacherResponse {
    private Integer id;
    private String name;
    private String phone;
}
