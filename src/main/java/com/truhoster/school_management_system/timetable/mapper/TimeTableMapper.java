package com.truhoster.school_management_system.timetable.mapper;

import com.truhoster.school_management_system.timetable.dto.*;
import com.truhoster.school_management_system.timetable.entity.TimeTable;
import com.truhoster.school_management_system.timetable.entity.TimeTableLine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TimeTableMapper {
    /**
     * Map CreateLine to TimeTableLine
     * */
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "timetable", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "duration", ignore = true)
    TimeTableLine toLine(CreateLine line);

    /**
     * Map TimeTableLine to LineDTO
     * */
    @Mapping(source = "teacher.id", target = "teacherId")
    @Mapping(source = "subject.id", target = "subjectId")
    @Mapping(source = "timetable.id", target = "timeTableId")
    @Mapping(source = "teacher.name", target = "teacher")
    @Mapping(source = "subject.name", target = "subject")
    LineDTO toLineDTO(TimeTableLine timeTableLine);


    /**
     * Map CreateTable to TimeTable
     * */
    @Mapping(target = "classroom", ignore = true)
    @Mapping(target = "lines", ignore = true)
    TimeTable toTable(CreateTable createTable);

    /**
     * Map TimeTable to TableDTO
     * */
    @Mapping(source = "classroom.id", target = "classroomId")
    @Mapping(source = "classroom.name", target = "classroom")
    TableDTO toTableDTO(TimeTable timeTable);
}
