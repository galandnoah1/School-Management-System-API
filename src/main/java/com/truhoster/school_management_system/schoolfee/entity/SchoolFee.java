package com.truhoster.school_management_system.schoolfee.entity;
import com.truhoster.school_management_system.classroom.entity.Classroom;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.security.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@Builder
@Table(name = "school_fees")
@AllArgsConstructor
@NoArgsConstructor
public class SchoolFee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    // Libellé libre pour identifier la grille (ex: "Frais 6eme 2026/2027")
    private String label;

    private Double inscriptionAmount;

    private Double tranche1Amount;
    private LocalDate tranche1Deadline;

    private Double tranche2Amount;
    private LocalDate tranche2Deadline;

    @ManyToMany
    @JoinTable(
            name = "school_fee_classrooms",
            joinColumns = @JoinColumn(name = "school_fee_id"),
            inverseJoinColumns = @JoinColumn(name = "classroom_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_classroom_single_feegrid",
                    columnNames = "classroom_id"
            )
    )
    private List<Classroom> classrooms;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createdAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}