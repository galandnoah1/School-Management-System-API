package com.truhoster.school_management_system.teacher.dto;

import com.truhoster.school_management_system.teacher.enums.Sex;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TeacherRequest {
    @NotNull(message = "Teacher's name is required")
    private String name;

    @NotNull(message = "Teacher's phone is required")
    private String phone;

    private Sex sex;

    @NotNull(message = "The classroom id is required")
    private Integer classroomId;

    @NotNull(message = "The subject id is required")
    private Integer subjectId;
}
