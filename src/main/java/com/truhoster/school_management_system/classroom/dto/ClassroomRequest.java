package com.truhoster.school_management_system.classroom.dto;

import com.truhoster.school_management_system.classroom.enums.*;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClassroomRequest {

    @NotNull(message = "La section est obligatoire")
    private Section section;

    @NotNull(message = "Le niveau est obligatoire")
    private Level level;

    private Speciality speciality;

    private Lv2 lv2;

    private Repartition repartition;
}