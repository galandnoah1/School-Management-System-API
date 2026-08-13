package com.truhoster.school_management_system.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Integer id;
    private String type;
    private Double amount;
    private String status;
    private String student;
    private String classroom;
    private Timestamp paymentDate;
}