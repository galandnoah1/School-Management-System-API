package com.truhoster.school_management_system.payment.dto;

import com.truhoster.school_management_system.payment.enums.PaymentType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "L'élève est obligatoire")
    private Integer studentId;

    @NotNull(message = "Le type de paiement est obligatoire")
    private PaymentType type;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private Double amount;
}