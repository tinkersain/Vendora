package com.vendora.scrap.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vendora.scrap.dto.GiveawayCreateDto;
import com.vendora.scrap.dto.GiveawayInterestDto;
import com.vendora.scrap.dto.GiveawayInterestResponse;
import com.vendora.scrap.dto.GiveawayMessageDto;
import com.vendora.scrap.dto.GiveawayMessageResponse;
import com.vendora.scrap.dto.GiveawayResponse;
import com.vendora.scrap.service.GiveawayService;

@ExtendWith(MockitoExtension.class)
class GiveawayControllerTest {

    @Mock
    private GiveawayService giveawayService;

    @InjectMocks
    private GiveawayController giveawayController;

    private GiveawayResponse sampleGiveaway;

    @BeforeEach
    void setUp() {
        sampleGiveaway = new GiveawayResponse(1L, 10L, "Free Table Fan", "Working condition",
                "APPLIANCE", "AVAILABLE", LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("createGiveaway - returns 201 Created")
    void testCreateGiveaway() {
        GiveawayCreateDto dto = new GiveawayCreateDto("Free Table Fan", "Working condition", "APPLIANCE");
        when(giveawayService.createGiveaway(eq(10L), any(GiveawayCreateDto.class))).thenReturn(sampleGiveaway);

        ResponseEntity<GiveawayResponse> response = giveawayController.createGiveaway(10L, null, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("getAllAvailable - returns 200 OK")
    void testGetAllAvailable() {
        when(giveawayService.getAllAvailable()).thenReturn(Collections.singletonList(sampleGiveaway));

        ResponseEntity<List<GiveawayResponse>> response = giveawayController.getAllAvailable();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getById - returns 200 OK")
    void testGetById() {
        when(giveawayService.getById(1L)).thenReturn(sampleGiveaway);

        ResponseEntity<GiveawayResponse> response = giveawayController.getById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("expressInterest - returns 201 Created")
    void testExpressInterest() {
        GiveawayInterestDto dto = new GiveawayInterestDto("I need this for my hostel room");
        GiveawayInterestResponse interestResp = new GiveawayInterestResponse(1L, 1L, 20L, "I need this", "PENDING", LocalDateTime.now());
        when(giveawayService.expressInterest(eq(1L), eq(20L), any(GiveawayInterestDto.class))).thenReturn(interestResp);

        ResponseEntity<GiveawayInterestResponse> response = giveawayController.expressInterest(1L, 20L, null, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("getInterestsForGiveaway - returns 200 OK")
    void testGetInterestsForGiveaway() {
        GiveawayInterestResponse interestResp = new GiveawayInterestResponse(1L, 1L, 20L, "I need this", "PENDING", LocalDateTime.now());
        when(giveawayService.getInterestsForGiveaway(1L, 10L)).thenReturn(Collections.singletonList(interestResp));

        ResponseEntity<List<GiveawayInterestResponse>> response = giveawayController.getInterestsForGiveaway(1L, 10L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("acceptInterest - returns 200 OK")
    void testAcceptInterest() {
        GiveawayInterestResponse interestResp = new GiveawayInterestResponse(1L, 1L, 20L, "I need this", "ACCEPTED", LocalDateTime.now());
        when(giveawayService.acceptInterest(1L, 1L, 10L)).thenReturn(interestResp);

        ResponseEntity<GiveawayInterestResponse> response = giveawayController.acceptInterest(1L, 1L, 10L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ACCEPTED", response.getBody().getStatus());
    }

    @Test
    @DisplayName("sendMessage - returns 201 Created")
    void testSendMessage() {
        GiveawayMessageDto dto = new GiveawayMessageDto("When can I collect?");
        GiveawayMessageResponse msgResp = new GiveawayMessageResponse(100L, 1L, 20L, "When can I collect?", LocalDateTime.now());
        when(giveawayService.sendMessage(eq(1L), eq(20L), any(GiveawayMessageDto.class))).thenReturn(msgResp);

        ResponseEntity<GiveawayMessageResponse> response = giveawayController.sendMessage(1L, 20L, null, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(100L, response.getBody().getId());
    }

    @Test
    @DisplayName("getMessages - returns 200 OK")
    void testGetMessages() {
        GiveawayMessageResponse msgResp = new GiveawayMessageResponse(100L, 1L, 20L, "When can I collect?", LocalDateTime.now());
        when(giveawayService.getMessages(1L)).thenReturn(Collections.singletonList(msgResp));

        ResponseEntity<List<GiveawayMessageResponse>> response = giveawayController.getMessages(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("cancelGiveaway - returns 200 OK")
    void testCancelGiveaway() {
        sampleGiveaway.setStatus("CANCELLED");
        when(giveawayService.cancelGiveaway(1L, 10L)).thenReturn(sampleGiveaway);

        ResponseEntity<GiveawayResponse> response = giveawayController.cancelGiveaway(1L, 10L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CANCELLED", response.getBody().getStatus());
    }
}
