package com.truhoster.school_management_system.note.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.truhoster.school_management_system.note.enums.Trimester;
import com.truhoster.school_management_system.reportcard.entity.ReportCard;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;


@Entity
@Data
@Builder
@Table(name = "notelines")
@AllArgsConstructor
@NoArgsConstructor
public class NoteLine {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String subject;
    private Double note1;
    private Double note2;
    private double average;
    private int coefficient;
    private double notecoefficie;
    private String appreciation;
    private String teacher;

    @ManyToOne()
    @JoinColumn(name = "reportcard_id")
    private ReportCard reportCard;

    @Enumerated(EnumType.STRING)
    private Trimester trimester;

    @UpdateTimestamp
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}
