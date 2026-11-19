package com.vendora.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vendora.payment.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionReference(String transactionReference);

    Optional<Payment> findByIdempotencyKey(String idempotencyKey);

    List<Payment> findByBuyerId(Long buyerId);

    List<Payment> findBySellerId(Long sellerId);
}
