package com.vendora.payment.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.payment.dto.PaymentRequest;
import com.vendora.payment.dto.PaymentResponse;
import com.vendora.payment.entity.PaymentMethod;
import com.vendora.payment.entity.PaymentStatus;
import com.vendora.payment.service.PaymentService;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new PaymentResponse();
        sampleResponse.setId(1L);
        sampleResponse.setTransactionReference("VEN-REF-100");
        sampleResponse.setBuyerId(10L);
        sampleResponse.setSellerId(20L);
        sampleResponse.setAmount(BigDecimal.valueOf(500.00));
        sampleResponse.setCurrency("INR");
        sampleResponse.setPaymentMethod(PaymentMethod.UPI);
        sampleResponse.setPaymentStatus(PaymentStatus.SUCCESS);
        sampleResponse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("createPayment - Success with token header")
    void testCreatePayment_Success() {
        PaymentRequest request = new PaymentRequest();
        request.setSellerId(20L);
        request.setAmount(BigDecimal.valueOf(500.00));
        request.setPaymentMethod(PaymentMethod.UPI);

        when(paymentService.createPayment(any(PaymentRequest.class))).thenReturn(sampleResponse);

        ResponseEntity<PaymentResponse> response = paymentController.createPayment(10L, "IDEM-1", request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("VEN-REF-100", response.getBody().getTransactionReference());
    }

    @Test
    @DisplayName("createPayment - Throws 401 Unauthorized when buyerId is null")
    void testCreatePayment_Unauthorized() {
        PaymentRequest request = new PaymentRequest();
        assertThrows(ResponseStatusException.class,
                () -> paymentController.createPayment(null, null, request));
    }

    @Test
    @DisplayName("getMyPayments - Success")
    void testGetMyPayments_Success() {
        when(paymentService.getPaymentsByBuyer(10L)).thenReturn(Collections.singletonList(sampleResponse));

        ResponseEntity<List<PaymentResponse>> response = paymentController.getMyPayments(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getMyPayments - Throws 401 when user is null")
    void testGetMyPayments_Unauthorized() {
        assertThrows(ResponseStatusException.class,
                () -> paymentController.getMyPayments(null));
    }

    @Test
    @DisplayName("getMyReceivedPayments - Success")
    void testGetMyReceivedPayments_Success() {
        when(paymentService.getPaymentsBySeller(20L)).thenReturn(Collections.singletonList(sampleResponse));

        ResponseEntity<List<PaymentResponse>> response = paymentController.getMyReceivedPayments(20L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getAllPayments - Admin permitted")
    void testGetAllPayments_Admin() {
        when(paymentService.getAllPayments()).thenReturn(Collections.singletonList(sampleResponse));

        ResponseEntity<List<PaymentResponse>> response = paymentController.getAllPayments("ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getAllPayments - Non-admin forbidden")
    void testGetAllPayments_Forbidden() {
        assertThrows(ResponseStatusException.class,
                () -> paymentController.getAllPayments("STUDENT"));
    }

    @Test
    @DisplayName("getPaymentById - Success")
    void testGetPaymentById() {
        when(paymentService.getPaymentById(1L)).thenReturn(sampleResponse);

        ResponseEntity<PaymentResponse> response = paymentController.getPaymentById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("getPaymentByReference - Success")
    void testGetPaymentByReference() {
        when(paymentService.getPaymentByReference("VEN-REF-100")).thenReturn(sampleResponse);

        ResponseEntity<PaymentResponse> response = paymentController.getPaymentByReference("VEN-REF-100");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("VEN-REF-100", response.getBody().getTransactionReference());
    }

    @Test
    @DisplayName("getPaymentsByBuyer - Self permitted")
    void testGetPaymentsByBuyer_Self() {
        when(paymentService.getPaymentsByBuyer(10L)).thenReturn(Collections.singletonList(sampleResponse));

        ResponseEntity<List<PaymentResponse>> response = paymentController.getPaymentsByBuyer(10L, 10L, "STUDENT");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("getPaymentsByBuyer - Other user forbidden")
    void testGetPaymentsByBuyer_Forbidden() {
        assertThrows(ResponseStatusException.class,
                () -> paymentController.getPaymentsByBuyer(10L, 99L, "STUDENT"));
    }

    @Test
    @DisplayName("getPaymentsBySeller - Self permitted")
    void testGetPaymentsBySeller_Self() {
        when(paymentService.getPaymentsBySeller(20L)).thenReturn(Collections.singletonList(sampleResponse));

        ResponseEntity<List<PaymentResponse>> response = paymentController.getPaymentsBySeller(20L, 20L, "STUDENT");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("getPaymentsBySeller - Other user forbidden")
    void testGetPaymentsBySeller_Forbidden() {
        assertThrows(ResponseStatusException.class,
                () -> paymentController.getPaymentsBySeller(20L, 99L, "STUDENT"));
    }

    @Test
    @DisplayName("updatePaymentStatus - Success")
    void testUpdatePaymentStatus() {
        sampleResponse.setPaymentStatus(PaymentStatus.REFUNDED);
        when(paymentService.updatePaymentStatus(1L, PaymentStatus.REFUNDED)).thenReturn(sampleResponse);

        ResponseEntity<PaymentResponse> response = paymentController.updatePaymentStatus(1L, PaymentStatus.REFUNDED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(PaymentStatus.REFUNDED, response.getBody().getPaymentStatus());
    }
}
