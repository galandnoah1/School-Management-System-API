package com.truhoster.school_management_system.timetable.repository;

import com.truhoster.school_management_system.timetable.entity.TimeTableLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LineRepo extends JpaRepository<TimeTableLine, Integer> {

    List<TimeTableLine> findByTeacherId(Integer teacherId);
}
