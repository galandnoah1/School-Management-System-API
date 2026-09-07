package com.truhoster.school_management_system.timetable.dto;

import java.time.LocalDate;
import java.util.List;

public record TableDTO(
        Integer id,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        Integer classroomId,
        String classroom,
        List<LineDTO> lines
) {
}
