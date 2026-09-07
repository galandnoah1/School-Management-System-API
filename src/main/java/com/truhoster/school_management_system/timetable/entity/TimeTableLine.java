package com.truhoster.school_management_system.timetable.entity;


import com.truhoster.school_management_system.subject.entity.Subject;
import com.truhoster.school_management_system.teacher.entity.Teacher;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "timetable_lines")
@Getter
@Setter
public class TimeTableLine {
    @Id @GeneratedValue(strategy =GenerationType.IDENTITY)
    private Integer id;

    private DayOfWeek day;

    @Column(name = "start_hour", nullable = false)
    private LocalTime startHour;


    @Column(name = "end_hour", nullable = false)
    private LocalTime endHour;

    @Column(name = "its_break")
    private Boolean itsBreak;

    private LocalTime duration;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timetable_id")
    private TimeTable timetable;


    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
