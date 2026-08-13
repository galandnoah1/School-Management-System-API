package com.truhoster.school_management_system.payment.repository;

import com.truhoster.school_management_system.payment.entity.Payment;
import com.truhoster.school_management_system.payment.enums.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByStudentId(Integer studentId);

    List<Payment> findByStudentIdAndType(Integer studentId, PaymentType type);
}