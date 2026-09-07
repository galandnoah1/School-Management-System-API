package com.truhoster.school_management_system.timetable.dto;

import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record CreateLine(
        @NotNull(message = "Veuillez choisir un jour de la semaine")
        DayOfWeek day,

        @NotNull(message = "Veuillez sélectionner l'emploi du temps")
        Integer timeTableId,


        Integer subjectId,


        Integer teacherId,

        @NotNull(message = "Veuillez ajouter une heure de debut")
        LocalTime startHour,

        @NotNull(message = "Veuillez ajouter une heure de fin")
        LocalTime endHour,

        Boolean itsBreak
) {
}
