package com.truhoster.school_management_system.subject.entity;


import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.classroom.enums.Section;
import com.truhoster.school_management_system.subject.enums.SubjectGroup;
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
@Table(name = "subjects")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Subject {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;

    @Enumerated(EnumType.STRING)
    private Section section;

    @Enumerated(EnumType.STRING)
    private SubjectGroup subjectGroup;


    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<SubjectByClassroom> subjectByClassrooms;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
