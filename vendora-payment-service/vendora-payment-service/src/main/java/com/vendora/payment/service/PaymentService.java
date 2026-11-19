package com.vendora.payment.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.payment.dto.PaymentRequest;
import com.vendora.payment.dto.PaymentResponse;
import com.vendora.payment.entity.Payment;
import com.vendora.payment.entity.PaymentStatus;
import com.vendora.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // 1. Create a new payment transaction with Idempotency protection
    @Transactional
    public PaymentResponse createPayment(PaymentRequest dto) {

        // Idempotency check: if request was already processed with same idempotency key, return existing record
        if (dto.getIdempotencyKey() != null && !dto.getIdempotencyKey().isBlank()) {
            Optional<Payment> existing = paymentRepository.findByIdempotencyKey(dto.getIdempotencyKey().trim());
            if (existing.isPresent()) {
                return mapToResponse(existing.get());
            }
        }

        if (dto.getBuyerId() != null && dto.getBuyerId().equals(dto.getSellerId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Buyer and seller cannot be the same user");
        }

        Payment payment = new Payment();

        payment.setTransactionReference(
                "VEN-" + UUID.randomUUID().toString()
                        .substring(0, 12).toUpperCase());

        payment.setIdempotencyKey(dto.getIdempotencyKey() != null && !dto.getIdempotencyKey().isBlank()
                ? dto.getIdempotencyKey().trim() : null);
        payment.setReferenceType(dto.getReferenceType() != null ? dto.getReferenceType() : "LISTING");
        payment.setReferenceId(dto.getReferenceId() != null ? dto.getReferenceId() : dto.getListingId());

        payment.setBuyerId(dto.getBuyerId());
        payment.setSellerId(dto.getSellerId());
        payment.setListingId(dto.getListingId());
        payment.setAmount(dto.getAmount());
        payment.setCurrency("INR");
        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setDescription(dto.getDescription());
        payment.setPaymentStatus(PaymentStatus.PENDING);

        Payment savedPayment = paymentRepository.save(payment);

        return mapToResponse(savedPayment);
    }

    // 2. Retrieve all payments
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {

        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 3. Retrieve payment by ID
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {

        Payment payment = findPaymentOrThrow(id);

        return mapToResponse(payment);
    }

    // 4. Retrieve payment using transaction reference
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByReference(String reference) {

        Payment payment = paymentRepository
                .findByTransactionReference(reference)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction not found with reference: " + reference));

        return mapToResponse(payment);
    }

    // 5. Retrieve payments made by a buyer
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByBuyer(Long buyerId) {

        return paymentRepository.findByBuyerId(buyerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 6. Retrieve payments received by a seller
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsBySeller(Long sellerId) {

        return paymentRepository.findBySellerId(sellerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 7. Simulate payment processing status transition
    @Transactional
    public PaymentResponse updatePaymentStatus(
            Long id, PaymentStatus newStatus) {

        Payment payment = findPaymentOrThrow(id);

        PaymentStatus currentStatus = payment.getPaymentStatus();

        boolean validTransition =
                (currentStatus == PaymentStatus.PENDING
                        && (newStatus == PaymentStatus.SUCCESS
                        || newStatus == PaymentStatus.FAILED))
                || (currentStatus == PaymentStatus.SUCCESS
                        && newStatus == PaymentStatus.REFUNDED);

        if (!validTransition) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid payment status transition from "
                            + currentStatus + " to " + newStatus);
        }

        payment.setPaymentStatus(newStatus);

        return mapToResponse(paymentRepository.save(payment));
    }

    // Helper: Find payment or return 404
    private Payment findPaymentOrThrow(Long id) {

        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Payment not found with ID: " + id));
    }

    // Convert Payment entity to response DTO
    public PaymentResponse mapToResponse(Payment payment) {

        PaymentResponse dto = new PaymentResponse();

        dto.setId(payment.getId());
        dto.setTransactionReference(payment.getTransactionReference());
        dto.setIdempotencyKey(payment.getIdempotencyKey());
        dto.setReferenceType(payment.getReferenceType());
        dto.setReferenceId(payment.getReferenceId());
        dto.setBuyerId(payment.getBuyerId());
        dto.setSellerId(payment.getSellerId());
        dto.setListingId(payment.getListingId());
        dto.setAmount(payment.getAmount());
        dto.setCurrency(payment.getCurrency());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setDescription(payment.getDescription());
        dto.setCreatedAt(payment.getCreatedAt());
        dto.setUpdatedAt(payment.getUpdatedAt());

        return dto;
    }
}
