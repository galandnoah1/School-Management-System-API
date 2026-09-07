package com.truhoster.school_management_system.timetable.repository;

import com.truhoster.school_management_system.timetable.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TableRepo extends JpaRepository<TimeTable, Integer> {
    List<TimeTable> findByClassroomId(Integer classroomId);


}
