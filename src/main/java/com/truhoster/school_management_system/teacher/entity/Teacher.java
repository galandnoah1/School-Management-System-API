package com.truhoster.school_management_system.teacher.entity;

import com.truhoster.school_management_system.teacher.enums.Sex;
import com.truhoster.school_management_system.timetable.entity.TimeTableLine;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.List;

@Builder
@Entity
@Table(name = "teachers")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Teacher {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Sex sex;

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Affectation> affectations;

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    private List<TimeTableLine> timeTableLines;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
