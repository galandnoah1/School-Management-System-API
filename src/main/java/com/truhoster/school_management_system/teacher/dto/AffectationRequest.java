package com.truhoster.school_management_system.teacher.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AffectationRequest {
    @NotNull(message = "The teacher id is required")
    private Integer teacherId;

    @NotNull(message = "The classroom id is required")
    private Integer classroomId;

    @NotNull(message = "The subject id is required")
    private Integer subjectId;
}
