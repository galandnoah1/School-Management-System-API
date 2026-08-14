package com.truhoster.school_management_system.printhistory.entity;

import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.note.enums.Trimester;
import com.truhoster.school_management_system.printhistory.enums.PrintAction;
import com.truhoster.school_management_system.reportcard.entity.ReportCard;
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
@Table(name = "print_histories")
@AllArgsConstructor
@NoArgsConstructor
public class PrintHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private PrintAction action;

    // Nullable : renseigné uniquement pour PRINTED/DOWNLOADED
    // Reste null pour GENERATED
    @ManyToOne
    @JoinColumn(name = "reportcard_id")
    private ReportCard reportCard;

    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @Enumerated(EnumType.STRING)
    private Trimester trimester;

    // GENERATED : nombre de bulletins générés (écrasé à chaque régénération)
    // PRINTED/DOWNLOADED : nombre de fois où l'action a eu lieu (incrémenté à chaque fois)
    private Integer count;

    // Nullable pour l'instant. Pour PRINTED/DOWNLOADED, reflète la DERNIÈRE personne ayant agi.
    private String performedBy;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp performedAt;

    @UpdateTimestamp
    private Timestamp updatedAt;
}