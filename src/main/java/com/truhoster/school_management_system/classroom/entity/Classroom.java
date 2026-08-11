package com.truhoster.school_management_system.classroom.entity;

import com.truhoster.school_management_system.classroom.enums.*;
import com.truhoster.school_management_system.student.entity.Student;
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
@Table(name = "classrooms")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Classroom {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private Section section;
    private Level level;
    private Speciality speciality;
    private Lv2 lv2;
    private Repartition repartition;
    private String name;

    @OneToMany(mappedBy = "classroom")
    private List<Student> studentList;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
