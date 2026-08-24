package com.truhoster.school_management_system.reportcard.entity;


import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.note.entity.NoteLine;
import com.truhoster.school_management_system.note.enums.Trimester;
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

@Entity
@Data
@Builder
@Table(name = "reportcards")
@AllArgsConstructor
@NoArgsConstructor
public class ReportCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private Integer ranking; // rang du trimestre
    private Double average; // moyenne du trimestre
    private Double average1; // moyenne au premier cc du trimestre
    private Double average2; //moyenne au dernier cc du trimestre
    private Double firstaverage; // moyenne du premier
    private Double lastaverage; // moyenne du dernier
    private Double overallaverage; // moyenne generale
    private String appreciation; // appreciation en fonction de la moyenne du trimestre

    @Enumerated(EnumType.STRING)
    private Trimester trimester;

    @ManyToOne()
    private Student student;

    @ManyToOne()
    private Classroom classroom;

    @OneToMany(mappedBy = "reportCard")
    private List<NoteLine> noteLines;

    @CreationTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updated;
}
