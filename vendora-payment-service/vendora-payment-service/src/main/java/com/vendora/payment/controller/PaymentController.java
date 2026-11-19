package com.vendora.payment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.vendora.payment.dto.PaymentRequest;
import com.vendora.payment.dto.PaymentResponse;
import com.vendora.payment.entity.PaymentStatus;
import com.vendora.payment.service.PaymentService;

@Tag(name = "Payment Controller", description = "Endpoints for managing campus marketplace and service payments")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // 1. Create simulated payment transaction (Idempotency supported)
    @Operation(summary = "Initiate a new payment transaction (Uses login token to assign buyer)")
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerBuyerId,
            @Parameter(description = "Optional idempotency key to prevent double charge (e.g. VEN-ORD-101)", required = false)
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKeyHeader,
            @Valid @RequestBody PaymentRequest request) {

        if (headerBuyerId != null) {
            request.setBuyerId(headerBuyerId);
        }

        if (request.getBuyerId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Buyer ID could not be identified from token");
        }

        if (idempotencyKeyHeader != null && !idempotencyKeyHeader.isBlank() && request.getIdempotencyKey() == null) {
            request.setIdempotencyKey(idempotencyKeyHeader.trim());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.createPayment(request));
    }

    // 2. View my payment history (current logged-in user as buyer)
    @Operation(summary = "View my payment history as buyer (Uses login token)")
    @GetMapping("/my")
    public ResponseEntity<List<PaymentResponse>> getMyPayments(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {

        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID could not be identified from token");
        }

        return ResponseEntity.ok(paymentService.getPaymentsByBuyer(currentUserId));
    }

    // 3. View payments received as seller (current logged-in user)
    @Operation(summary = "View payments received as seller (Uses login token)")
    @GetMapping("/my/received")
    public ResponseEntity<List<PaymentResponse>> getMyReceivedPayments(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long currentUserId) {

        if (currentUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID could not be identified from token");
        }

        return ResponseEntity.ok(paymentService.getPaymentsBySeller(currentUserId));
    }

    // 4. Get all payments (ADMIN only)
    @Operation(summary = "Get all campus payment transactions (Admin only)")
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role) {

        if (!"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only administrators can view all payment transactions");
        }

        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    // 5. Get payment by ID
    @Operation(summary = "Get payment details by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @Parameter(description = "Payment ID", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // 6. Get payment by transaction reference
    @Operation(summary = "Get payment details by transaction reference")
    @GetMapping("/reference/{reference}")
    public ResponseEntity<PaymentResponse> getPaymentByReference(
            @Parameter(description = "Transaction reference (e.g. VEN-1234567890AB)", example = "VEN-1234567890AB")
            @PathVariable String reference) {

        return ResponseEntity.ok(
                paymentService.getPaymentByReference(reference));
    }

    // 7. Get payments by buyer (Admin or self only)
    @Operation(summary = "Get payments made by a specific buyer")
    @GetMapping("/buyer/{buyerId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByBuyer(
            @Parameter(description = "Buyer User ID", example = "5")
            @PathVariable Long buyerId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long currentUserId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role) {

        if (currentUserId != null && !currentUserId.equals(buyerId) && !"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are only permitted to view your own payments");
        }

        return ResponseEntity.ok(paymentService.getPaymentsByBuyer(buyerId));
    }

    // 8. Get payments by seller (Admin or self only)
    @Operation(summary = "Get payments received by a specific seller")
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsBySeller(
            @Parameter(description = "Seller User ID", example = "4")
            @PathVariable Long sellerId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long currentUserId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Role", defaultValue = "STUDENT") String role) {

        if (currentUserId != null && !currentUserId.equals(sellerId) && !"ADMIN".equalsIgnoreCase(role)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are only permitted to view your own received payments");
        }

        return ResponseEntity.ok(paymentService.getPaymentsBySeller(sellerId));
    }

    // 9. Update payment status (simulated gateway callback)
    @Operation(summary = "Update payment status (simulated processing: PENDING -> SUCCESS/FAILED, SUCCESS -> REFUNDED)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @Parameter(description = "Payment ID", example = "1")
            @PathVariable Long id,
            @Parameter(description = "New payment status: SUCCESS, FAILED, REFUNDED")
            @RequestParam PaymentStatus status) {

        return ResponseEntity.ok(
                paymentService.updatePaymentStatus(id, status));
    }
}
