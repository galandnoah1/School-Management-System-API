package com.truhoster.school_management_system.subject.entity;

import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.classroom.enums.Section;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Builder
@Entity
@Table(name = "subjectbyclassrooms",  uniqueConstraints = @UniqueConstraint(
        name = "uk_classroom_subject",
        columnNames = {"classroom_id", "subject_id"}
))
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SubjectByClassroom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private int coefficient;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
