package com.truhoster.school_management_system.note.entity;

import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.note.enums.Evaluation;
import com.truhoster.school_management_system.student.entity.Student;
import com.truhoster.school_management_system.subject.entity.Subject;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;


@Entity
@Data
@Builder
@Table(name = "notes")
@AllArgsConstructor
@NoArgsConstructor
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Double note;

    @Enumerated(EnumType.STRING)
    private Evaluation evaluation;


    @ManyToOne()
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne()
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne()
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @CreationTimestamp
    private Timestamp printDate;

    @UpdateTimestamp
    private Timestamp updatedAt;
}