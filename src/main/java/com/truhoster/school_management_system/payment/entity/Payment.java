package com.truhoster.school_management_system.payment.entity;
import com.truhoster.school_management_system.classroom.entity.Classroom;
import com.truhoster.school_management_system.payment.enums.PaymentStatus;
import com.truhoster.school_management_system.payment.enums.PaymentType;
import com.truhoster.school_management_system.student.entity.Student;
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
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private PaymentType type;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // Snapshot de la classe au moment du paiement (utile si l'élève change de classe en cours d'année)
    @ManyToOne
    @JoinColumn(name = "classroom_id")
    private Classroom classroom;

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp paymentDate;
}