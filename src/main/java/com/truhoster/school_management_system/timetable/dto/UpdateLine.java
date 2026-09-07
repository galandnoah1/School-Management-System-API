package com.truhoster.school_management_system.timetable.dto;

import java.time.LocalTime;

public record UpdateLine(
        LocalTime startHour,
        LocalTime endHour,
        Integer subjectId,
        Integer teacherId,
        Boolean isBreak
) {
}
