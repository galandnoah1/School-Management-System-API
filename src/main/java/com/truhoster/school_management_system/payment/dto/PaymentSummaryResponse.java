package com.truhoster.school_management_system.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryResponse {
    private Integer studentId;
    private String student;
    private Double inscriptionRequired;
    private Double inscriptionPaid;
    private String inscriptionStatus;
    private Double tranche1Required;
    private Double tranche1Paid;
    private String tranche1Status;
    private Double tranche2Required;
    private Double tranche2Paid;
    private String tranche2Status;
    private boolean eligibleForTrimester1;
    private boolean eligibleForTrimester2AndTrimester3;
}