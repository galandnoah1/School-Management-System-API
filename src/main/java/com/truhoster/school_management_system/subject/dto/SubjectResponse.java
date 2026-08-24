package com.truhoster.school_management_system.subject.dto;

import com.truhoster.school_management_system.classroom.enums.Section;
import com.truhoster.school_management_system.subject.enums.SubjectGroup;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SubjectResponse {
    private Integer id;
    private String name;
    private Section section;
    private SubjectGroup subjectGroup;
    private List<SubjectByClassroomResponse> classrooms;
}
