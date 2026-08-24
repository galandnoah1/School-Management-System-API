package com.truhoster.school_management_system.payment.service;

import com.truhoster.school_management_system.note.enums.Trimester;
import com.truhoster.school_management_system.payment.dto.PaymentRequest;
import com.truhoster.school_management_system.payment.dto.PaymentResponse;
import com.truhoster.school_management_system.payment.dto.PaymentSummaryResponse;
import com.truhoster.school_management_system.payment.entity.Payment;
import com.truhoster.school_management_system.payment.enums.PaymentStatus;
import com.truhoster.school_management_system.payment.enums.PaymentType;
import com.truhoster.school_management_system.payment.mapper.PaymentMapper;
import com.truhoster.school_management_system.payment.repository.PaymentRepository;
import com.truhoster.school_management_system.schoolfee.entity.SchoolFee;
import com.truhoster.school_management_system.schoolfee.repository.SchoolFeeRepository;
import com.truhoster.school_management_system.student.entity.Student;
import com.truhoster.school_management_system.student.repository.StudentRepository;
import jakarta.validation.Valid;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;




@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final StudentRepository studentRepository;
    private final SchoolFeeRepository schoolFeeRepository;

    /**
     * Enregistre un nouveau paiement pour un élève.
     * L'inscription doit être payée en une seule fois, montant exact (pas de partiel, pas de surplus).
     * Les tranches acceptent des paiements partiels (avances) mais ne peuvent jamais dépasser
     * le montant requis au total.
     * Le statut (ADVANCE/COMPLETED) est calculé automatiquement selon la somme cumulée.
     *
     * @param request les données du paiement (élève, type, montant)
     * @return le DTO du paiement créé
     * @throws EntityNotFoundException si l'élève ou la grille de frais de sa classe n'existe pas
     * @throws IllegalStateException si le type est déjà entièrement payé, si le montant dépasse
     *         ce qui est requis, ou si l'inscription n'est pas payée en une seule fois exacte
     */
    @Transactional
    public PaymentResponse create(@Valid  PaymentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Student not found with id: " + request.getStudentId()));

        SchoolFee schoolFee = schoolFeeRepository.findByClassroomId(student.getClassroom().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucune grille de frais définie pour la classe de cet élève"));

        double requiredAmount = getRequiredAmount(schoolFee, request.getType());
        double alreadyPaid = sumPayments(request.getStudentId(), request.getType());

        if (alreadyPaid >= requiredAmount) {
            throw new IllegalStateException(
                    "Le " + request.getType() + " est déjà entièrement payé pour cet élève");
        }

        if (request.getType() == PaymentType.INSCRIPTION) {
            if (!request.getAmount().equals(requiredAmount)) {
                throw new IllegalStateException(
                        "L'inscription doit être payée en une seule fois, montant exact requis: " + requiredAmount);
            }
        } else {
            double newTotal = alreadyPaid + request.getAmount();
            if (newTotal > requiredAmount) {
                throw new IllegalStateException(
                        "Ce paiement dépasse le montant restant dû (" + (requiredAmount - alreadyPaid) + ")");
            }
        }

        Payment payment = paymentMapper.toEntity(request);

        double newTotal = alreadyPaid + request.getAmount();
        payment.setStatus(newTotal >= requiredAmount ? PaymentStatus.COMPLETED : PaymentStatus.ADVANCE);

        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toDTO(saved);
    }

    /**
     * Récupère tous les paiements d'un élève.
     *
     * @param studentId l'identifiant de l'élève
     * @return la liste des DTOs des paiements de cet élève
     */
    @Transactional()
    public List<PaymentResponse> getByStudent(Integer studentId) {
        return paymentMapper.toDTOList(paymentRepository.findByStudentId(studentId));
    }

    /**
     * Construit un résumé de la situation financière d'un élève : montant requis, montant payé
     * et statut pour chaque type, ainsi que son éligibilité à l'impression de chaque trimestre.
     *
     * @param studentId l'identifiant de l'élève
     * @return le résumé financier de l'élève
     * @throws EntityNotFoundException si l'élève ou sa grille de frais n'existe pas
     */
    @Transactional()
    public PaymentSummaryResponse getSummary(Integer studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));

        SchoolFee schoolFee = schoolFeeRepository.findByClassroomId(student.getClassroom().getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucune grille de frais définie pour la classe de cet élève"));

        double inscriptionPaid = sumPayments(studentId, PaymentType.INSCRIPTION);
        double tranche1Paid = sumPayments(studentId, PaymentType.TRANCHE1);
        double tranche2Paid = sumPayments(studentId, PaymentType.TRANCHE2);

        boolean inscriptionDone = inscriptionPaid >= schoolFee.getInscriptionAmount();
        boolean tranche1Done = tranche1Paid >= schoolFee.getTranche1Amount();
        boolean tranche2Done = tranche2Paid >= schoolFee.getTranche2Amount();

        return PaymentSummaryResponse.builder()
                .studentId(studentId)
                .student(student.getFirstname() + " " + student.getLastname())
                .inscriptionRequired(schoolFee.getInscriptionAmount())
                .inscriptionPaid(inscriptionPaid)
                .inscriptionStatus(inscriptionDone ? "COMPLETED" : "ADVANCE")
                .tranche1Required(schoolFee.getTranche1Amount())
                .tranche1Paid(tranche1Paid)
                .tranche1Status(tranche1Done ? "COMPLETED" : "ADVANCE")
                .tranche2Required(schoolFee.getTranche2Amount())
                .tranche2Paid(tranche2Paid)
                .tranche2Status(tranche2Done ? "COMPLETED" : "ADVANCE")
                .eligibleForTrimester1(inscriptionDone && tranche1Done)
                .eligibleForTrimester2AndTrimester3(tranche2Done)
                .build();
    }

    /**
     * Vérifie si un élève est éligible à l'impression du bulletin d'un trimestre donné,
     * selon ses paiements (Inscription+Tranche1 pour T1, Tranche2 pour T2/T3).
     *
     * @param studentId l'identifiant de l'élève
     * @param trimester le trimestre concerné
     * @return true si l'élève peut imprimer son bulletin pour ce trimestre
     */
    @Transactional()
    public boolean isEligibleForPrinting(Integer studentId, Trimester trimester) {
        PaymentSummaryResponse summary = getSummary(studentId);
        return trimester == Trimester.T1
                ? summary.isEligibleForTrimester1()
                : summary.isEligibleForTrimester2AndTrimester3();
    }

    /**
     * Détermine le montant requis pour un type de paiement, à partir de la grille de frais.
     *
     * @param schoolFee la grille de frais applicable
     * @param type le type de paiement concerné
     * @return le montant requis pour ce type
     */
    private double getRequiredAmount(SchoolFee schoolFee, PaymentType type) {
        return switch (type) {
            case INSCRIPTION -> schoolFee.getInscriptionAmount();
            case TRANCHE1 -> schoolFee.getTranche1Amount();
            case TRANCHE2 -> schoolFee.getTranche2Amount();
        };
    }

    /**
     * Calcule la somme de tous les paiements déjà effectués par un élève pour un type donné.
     *
     * @param studentId l'identifiant de l'élève
     * @param type le type de paiement concerné
     * @return la somme des montants déjà payés pour ce type
     */
    private double sumPayments(Integer studentId, PaymentType type) {
        return paymentRepository.findByStudentIdAndType(studentId, type).stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }
}