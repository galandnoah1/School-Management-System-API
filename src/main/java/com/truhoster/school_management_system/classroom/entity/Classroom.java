package com.truhoster.school_management_system.classroom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Enumerated(EnumType.STRING)
    private Section section;

    @Enumerated(EnumType.STRING)
    private Level level;

    @Enumerated(EnumType.STRING)
    private Speciality speciality;

    @Enumerated(EnumType.STRING)
    private Lv2 lv2;

    @Enumerated(EnumType.STRING)
    private Repartition repartition;

    private String name;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Student> studentList;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
