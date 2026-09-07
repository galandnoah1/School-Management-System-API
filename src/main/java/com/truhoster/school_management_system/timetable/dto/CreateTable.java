package com.truhoster.school_management_system.timetable.dto;

import jakarta.validation.constraints.NotNull;


import java.time.LocalDate;

public record CreateTable(
        @NotNull(message = "Veuillez choisir la salle de classe")
        Integer classroomId,

        String title,

        LocalDate startDate,

        LocalDate endDate
) {
}
