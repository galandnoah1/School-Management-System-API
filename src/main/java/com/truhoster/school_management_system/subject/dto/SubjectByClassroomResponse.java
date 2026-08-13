package com.truhoster.school_management_system.subject.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectByClassroomResponse {
    private String name;
    private int coefficient;
    private String classroom;
}
