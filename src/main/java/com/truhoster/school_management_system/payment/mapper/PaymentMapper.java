package com.truhoster.school_management_system.payment.mapper;

import com.truhoster.school_management_system.payment.dto.PaymentRequest;
import com.truhoster.school_management_system.payment.dto.PaymentResponse;
import com.truhoster.school_management_system.payment.entity.Payment;
import com.truhoster.school_management_system.student.entity.Student;
import com.truhoster.school_management_system.student.repository.StudentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PaymentMapper {

    private final StudentRepository studentRepository;

    /**
     * Convertit un PaymentRequest en entité Payment.
     * Récupère l'élève à partir de son id, et prend sa classe actuelle en snapshot.
     * Ne définit pas le statut (calculé dans PaymentService selon la logique métier).
     *
     * @param request les données envoyées par le client
     * @return l'entité Payment prête à être persistée
     * @throws EntityNotFoundException si l'élève n'existe pas
     */
    public Payment toEntity(PaymentRequest request) {
        if (request == null) {
            return null;
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Student not found with id: " + request.getStudentId()));

        return Payment.builder()
                .type(request.getType())
                .amount(request.getAmount())
                .student(student)
                .classroom(student.getClassroom())
                .build();
    }

    /**
     * Convertit une entité Payment en PaymentResponse.
     *
     * @param payment l'entité à convertir
     * @return le DTO exposé au client, ou null si l'entité est null
     */
    public PaymentResponse toDTO(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentResponse.builder()
                .id(payment.getId())
                .type(payment.getType().name())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .student(payment.getStudent() != null
                        ? payment.getStudent().getFirstname() + " " + payment.getStudent().getLastname()
                        : null)
                .classroom(payment.getClassroom() != null ? payment.getClassroom().getName() : null)
                .paymentDate(payment.getPaymentDate())
                .build();
    }

    /**
     * Convertit une liste d'entités Payment en liste de PaymentResponse.
     *
     * @param payments la liste d'entités à convertir
     * @return la liste des DTOs correspondants
     */
    public List<PaymentResponse> toDTOList(List<Payment> payments) {
        if (payments == null) {
            return Collections.emptyList();
        }
        return payments.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}