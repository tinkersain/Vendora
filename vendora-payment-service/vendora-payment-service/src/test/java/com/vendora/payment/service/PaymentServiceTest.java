package com.vendora.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.payment.dto.PaymentRequest;
import com.vendora.payment.dto.PaymentResponse;
import com.vendora.payment.entity.Payment;
import com.vendora.payment.entity.PaymentMethod;
import com.vendora.payment.entity.PaymentStatus;
import com.vendora.payment.repository.PaymentRepository;

class PaymentServiceTest {

    private PaymentRepository paymentRepository;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        paymentService = new PaymentService(paymentRepository);
    }

    @Test
    void testCreatePaymentSuccess() {
        PaymentRequest req = new PaymentRequest();
        req.setBuyerId(1L);
        req.setSellerId(2L);
        req.setListingId(10L);
        req.setAmount(new BigDecimal("500.00"));
        req.setPaymentMethod(PaymentMethod.UPI);
        req.setIdempotencyKey("KEY-12345");

        Payment saved = new Payment();
        saved.setId(100L);
        saved.setTransactionReference("VEN-TEST1234");
        saved.setIdempotencyKey("KEY-12345");
        saved.setBuyerId(1L);
        saved.setSellerId(2L);
        saved.setAmount(new BigDecimal("500.00"));
        saved.setPaymentStatus(PaymentStatus.PENDING);

        when(paymentRepository.findByIdempotencyKey("KEY-12345")).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        PaymentResponse resp = paymentService.createPayment(req);
        assertNotNull(resp);
        assertEquals(100L, resp.getId());
        assertEquals("VEN-TEST1234", resp.getTransactionReference());
        assertEquals(PaymentStatus.PENDING, resp.getPaymentStatus());
    }

    @Test
    void testCreatePaymentIdempotentReturnsExisting() {
        PaymentRequest req = new PaymentRequest();
        req.setBuyerId(1L);
        req.setSellerId(2L);
        req.setAmount(new BigDecimal("500.00"));
        req.setPaymentMethod(PaymentMethod.UPI);
        req.setIdempotencyKey("KEY-EXISTING");

        Payment existing = new Payment();
        existing.setId(99L);
        existing.setTransactionReference("VEN-EXISTING");
        existing.setIdempotencyKey("KEY-EXISTING");
        existing.setPaymentStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findByIdempotencyKey("KEY-EXISTING")).thenReturn(Optional.of(existing));

        PaymentResponse resp = paymentService.createPayment(req);
        assertNotNull(resp);
        assertEquals(99L, resp.getId());
        assertEquals("VEN-EXISTING", resp.getTransactionReference());
    }

    @Test
    void testCreatePaymentSameBuyerAndSeller() {
        PaymentRequest req = new PaymentRequest();
        req.setBuyerId(1L);
        req.setSellerId(1L);
        req.setAmount(new BigDecimal("500.00"));
        req.setPaymentMethod(PaymentMethod.UPI);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> paymentService.createPayment(req));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testUpdatePaymentStatusSuccess() {
        Payment p = new Payment();
        p.setId(1L);
        p.setPaymentStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponse resp = paymentService.updatePaymentStatus(1L, PaymentStatus.SUCCESS);
        assertEquals(PaymentStatus.SUCCESS, resp.getPaymentStatus());
    }

    @Test
    void testUpdatePaymentStatusInvalidTransition() {
        Payment p = new Payment();
        p.setId(1L);
        p.setPaymentStatus(PaymentStatus.FAILED);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> paymentService.updatePaymentStatus(1L, PaymentStatus.SUCCESS));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testGetAllPayments() {
        Payment p = new Payment();
        p.setId(1L);
        when(paymentRepository.findAll()).thenReturn(java.util.List.of(p));

        java.util.List<PaymentResponse> list = paymentService.getAllPayments();
        assertEquals(1, list.size());
    }

    @Test
    void testGetPaymentById_Success() {
        Payment p = new Payment();
        p.setId(1L);
        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p));

        PaymentResponse resp = paymentService.getPaymentById(1L);
        assertNotNull(resp);
        assertEquals(1L, resp.getId());
    }

    @Test
    void testGetPaymentById_NotFound() {
        when(paymentRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> paymentService.getPaymentById(999L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetPaymentByReference_Success() {
        Payment p = new Payment();
        p.setId(1L);
        p.setTransactionReference("REF-123");
        when(paymentRepository.findByTransactionReference("REF-123")).thenReturn(Optional.of(p));

        PaymentResponse resp = paymentService.getPaymentByReference("REF-123");
        assertNotNull(resp);
        assertEquals("REF-123", resp.getTransactionReference());
    }

    @Test
    void testGetPaymentByReference_NotFound() {
        when(paymentRepository.findByTransactionReference("REF-UNKNOWN")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> paymentService.getPaymentByReference("REF-UNKNOWN"));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testGetPaymentsByBuyer() {
        Payment p = new Payment();
        p.setBuyerId(10L);
        when(paymentRepository.findByBuyerId(10L)).thenReturn(java.util.List.of(p));

        java.util.List<PaymentResponse> list = paymentService.getPaymentsByBuyer(10L);
        assertEquals(1, list.size());
    }

    @Test
    void testGetPaymentsBySeller() {
        Payment p = new Payment();
        p.setSellerId(20L);
        when(paymentRepository.findBySellerId(20L)).thenReturn(java.util.List.of(p));

        java.util.List<PaymentResponse> list = paymentService.getPaymentsBySeller(20L);
        assertEquals(1, list.size());
    }

    @Test
    void testUpdatePaymentStatus_SuccessToRefunded() {
        Payment p = new Payment();
        p.setId(1L);
        p.setPaymentStatus(PaymentStatus.SUCCESS);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponse resp = paymentService.updatePaymentStatus(1L, PaymentStatus.REFUNDED);
        assertEquals(PaymentStatus.REFUNDED, resp.getPaymentStatus());
    }

    @Test
    void testUpdatePaymentStatus_PendingToFailed() {
        Payment p = new Payment();
        p.setId(1L);
        p.setPaymentStatus(PaymentStatus.PENDING);

        when(paymentRepository.findById(1L)).thenReturn(Optional.of(p));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        PaymentResponse resp = paymentService.updatePaymentStatus(1L, PaymentStatus.FAILED);
        assertEquals(PaymentStatus.FAILED, resp.getPaymentStatus());
    }
}
