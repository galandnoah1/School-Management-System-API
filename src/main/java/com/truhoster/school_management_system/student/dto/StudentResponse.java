package com.truhoster.school_management_system.student.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class StudentResponse {
    private Integer id;
    private String firstname;
    private String lastname;
    private String  matricule;
    private String classroom;
    private String photoUrlLink;
}
