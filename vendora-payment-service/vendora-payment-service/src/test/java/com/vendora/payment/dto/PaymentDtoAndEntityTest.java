package com.vendora.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.vendora.payment.entity.Payment;
import com.vendora.payment.entity.PaymentMethod;
import com.vendora.payment.entity.PaymentStatus;

class PaymentDtoAndEntityTest {

    @Test
    @DisplayName("Payment Entity - test getters and setters")
    void testPaymentEntity() {
        Payment p = new Payment();
        p.setId(1L);
        p.setTransactionReference("VEN-TXN-1");
        p.setIdempotencyKey("KEY-1");
        p.setReferenceType("LISTING");
        p.setReferenceId(5L);
        p.setBuyerId(10L);
        p.setSellerId(20L);
        p.setListingId(5L);
        p.setAmount(BigDecimal.valueOf(150.00));
        p.setCurrency("INR");
        p.setPaymentMethod(PaymentMethod.UPI);
        p.setPaymentStatus(PaymentStatus.SUCCESS);
        p.setDescription("Test Payment");
        p.setCreatedAt(LocalDateTime.now());
        p.setUpdatedAt(LocalDateTime.now());

        assertEquals(1L, p.getId());
        assertEquals("VEN-TXN-1", p.getTransactionReference());
        assertEquals("KEY-1", p.getIdempotencyKey());
        assertEquals("LISTING", p.getReferenceType());
        assertEquals(5L, p.getReferenceId());
        assertEquals(10L, p.getBuyerId());
        assertEquals(20L, p.getSellerId());
        assertEquals(5L, p.getListingId());
        assertEquals(BigDecimal.valueOf(150.00), p.getAmount());
        assertEquals("INR", p.getCurrency());
        assertEquals(PaymentMethod.UPI, p.getPaymentMethod());
        assertEquals(PaymentStatus.SUCCESS, p.getPaymentStatus());
        assertEquals("Test Payment", p.getDescription());
        assertNotNull(p.getCreatedAt());
        assertNotNull(p.getUpdatedAt());
    }

    @Test
    @DisplayName("Payment Entity - lifecycle callbacks")
    void testPaymentLifecycle() throws Exception {
        Payment p = new Payment();
        p.setListingId(100L);

        java.lang.reflect.Method onCreate = Payment.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(p);

        assertNotNull(p.getCreatedAt());
        assertNotNull(p.getUpdatedAt());
        assertEquals("INR", p.getCurrency());
        assertEquals("LISTING", p.getReferenceType());
        assertEquals(100L, p.getReferenceId());

        java.lang.reflect.Method onUpdate = Payment.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(p);
        assertNotNull(p.getUpdatedAt());
    }

    @Test
    @DisplayName("PaymentRequest and PaymentResponse DTO tests")
    void testPaymentDtos() {
        PaymentRequest req = new PaymentRequest();
        req.setBuyerId(10L);
        req.setSellerId(20L);
        req.setListingId(5L);
        req.setReferenceType("LISTING");
        req.setReferenceId(5L);
        req.setIdempotencyKey("KEY-123");
        req.setAmount(BigDecimal.valueOf(99.99));
        req.setPaymentMethod(PaymentMethod.WALLET);
        req.setDescription("Wallet payment");

        assertEquals(10L, req.getBuyerId());
        assertEquals(20L, req.getSellerId());
        assertEquals(5L, req.getListingId());
        assertEquals("LISTING", req.getReferenceType());
        assertEquals(5L, req.getReferenceId());
        assertEquals("KEY-123", req.getIdempotencyKey());
        assertEquals(BigDecimal.valueOf(99.99), req.getAmount());
        assertEquals(PaymentMethod.WALLET, req.getPaymentMethod());
        assertEquals("Wallet payment", req.getDescription());

        LocalDateTime now = LocalDateTime.now();
        PaymentResponse resp = new PaymentResponse();
        resp.setId(10L);
        resp.setTransactionReference("REF-1");
        resp.setIdempotencyKey("KEY-1");
        resp.setReferenceType("LISTING");
        resp.setReferenceId(5L);
        resp.setBuyerId(10L);
        resp.setSellerId(20L);
        resp.setListingId(5L);
        resp.setAmount(BigDecimal.valueOf(99.99));
        resp.setCurrency("INR");
        resp.setPaymentMethod(PaymentMethod.WALLET);
        resp.setPaymentStatus(PaymentStatus.PENDING);
        resp.setDescription("Desc");
        resp.setCreatedAt(now);
        resp.setUpdatedAt(now);

        assertEquals(10L, resp.getId());
        assertEquals("REF-1", resp.getTransactionReference());
        assertEquals("KEY-1", resp.getIdempotencyKey());
        assertEquals("LISTING", resp.getReferenceType());
        assertEquals(5L, resp.getReferenceId());
        assertEquals(10L, resp.getBuyerId());
        assertEquals(20L, resp.getSellerId());
        assertEquals(5L, resp.getListingId());
        assertEquals(BigDecimal.valueOf(99.99), resp.getAmount());
        assertEquals("INR", resp.getCurrency());
        assertEquals(PaymentMethod.WALLET, resp.getPaymentMethod());
        assertEquals(PaymentStatus.PENDING, resp.getPaymentStatus());
        assertEquals("Desc", resp.getDescription());
        assertEquals(now, resp.getCreatedAt());
        assertEquals(now, resp.getUpdatedAt());
    }

    @Test
    @DisplayName("Enums coverage")
    void testEnums() {
        assertEquals(PaymentMethod.UPI, PaymentMethod.valueOf("UPI"));
        assertEquals(PaymentMethod.CARD, PaymentMethod.valueOf("CARD"));
        assertEquals(PaymentMethod.NET_BANKING, PaymentMethod.valueOf("NET_BANKING"));
        assertEquals(PaymentMethod.WALLET, PaymentMethod.valueOf("WALLET"));
        assertEquals(PaymentMethod.CASH, PaymentMethod.valueOf("CASH"));

        assertEquals(PaymentStatus.PENDING, PaymentStatus.valueOf("PENDING"));
        assertEquals(PaymentStatus.SUCCESS, PaymentStatus.valueOf("SUCCESS"));
        assertEquals(PaymentStatus.FAILED, PaymentStatus.valueOf("FAILED"));
        assertEquals(PaymentStatus.REFUNDED, PaymentStatus.valueOf("REFUNDED"));
    }
}
