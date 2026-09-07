package com.truhoster.school_management_system.timetable.dto;

import java.time.DayOfWeek;
import java.time.LocalTime;

public record LineDTO(
        Integer id,
        DayOfWeek day,
        LocalTime startHour,
        LocalTime endHour,
        Boolean itsBreak,
        LocalTime duration,
        Integer teacherId,
        String teacher,
        Integer subjectId,
        String subject,
        Integer timeTableId
) {
}
