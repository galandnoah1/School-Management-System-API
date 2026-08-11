package com.truhoster.school_management_system.teacher.dto;


import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AffectationRequest {
    @NotNull(message = "The teacher id is required")
    private Integer teacherId;

    @NotNull(message = "The classroom id is required")
    private Integer classroomId;

    @NotNull(message = "The subject id is required")
    private Integer subjectId;
}
