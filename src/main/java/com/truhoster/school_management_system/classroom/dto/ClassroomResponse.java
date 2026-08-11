package com.truhoster.school_management_system.classroom.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassroomResponse {
    private String name;
    private int size;
}
