package com.truhoster.school_management_system.timetable.repository;

import com.truhoster.school_management_system.timetable.entity.TimeTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepo extends JpaRepository<TimeTable, Integer> {
}
