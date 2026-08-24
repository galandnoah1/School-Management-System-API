package com.truhoster.school_management_system.classroom.dto;

import com.truhoster.school_management_system.classroom.enums.Section;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassroomResponse {
    private Integer id;
    private String name;
    private String section;
    private int size;
}
