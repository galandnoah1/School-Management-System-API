package com.truhoster.school_management_system.payment.controller;

import com.truhoster.school_management_system.payment.dto.PaymentRequest;
import com.truhoster.school_management_system.payment.dto.PaymentResponse;
import com.truhoster.school_management_system.payment.dto.PaymentSummaryResponse;
import com.truhoster.school_management_system.payment.service.PaymentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Gestion des paiements des élèves et éligibilité à l'impression des bulletins")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(summary = "Enregistrer un paiement")
    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest request) {
        log.info("POST /payments - enregistrement d'un paiement: {}", request);
        PaymentResponse response = paymentService.create(request);
        log.info("Paiement enregistré avec succès pour l'étudiant id={}, statut={}",
                request.getStudentId(), response.getStatus());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Lister les paiements d'un élève")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<PaymentResponse>> getByStudent(
            @Parameter(description = "Identifiant de l'élève") @PathVariable Integer studentId) {
        log.info("GET /payments/student/{}", studentId);
        return ResponseEntity.ok(paymentService.getByStudent(studentId));
    }

    @Operation(summary = "Résumé financier d'un élève", description = "Montants requis/payés par type et éligibilité à l'impression par trimestre.")
    @GetMapping("/student/{studentId}/summary")
    public ResponseEntity<PaymentSummaryResponse> getSummary(
            @Parameter(description = "Identifiant de l'élève") @PathVariable Integer studentId) {
        log.info("GET /payments/student/{}/summary", studentId);
        return ResponseEntity.ok(paymentService.getSummary(studentId));
    }
}