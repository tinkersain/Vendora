package com.vendora.scrap.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.scrap.dto.GiveawayCreateDto;
import com.vendora.scrap.dto.GiveawayInterestDto;
import com.vendora.scrap.dto.GiveawayInterestResponse;
import com.vendora.scrap.dto.GiveawayMessageDto;
import com.vendora.scrap.dto.GiveawayMessageResponse;
import com.vendora.scrap.dto.GiveawayResponse;
import com.vendora.scrap.entity.Giveaway;
import com.vendora.scrap.entity.GiveawayInterest;
import com.vendora.scrap.entity.GiveawayMessage;
import com.vendora.scrap.repository.GiveawayInterestRepository;
import com.vendora.scrap.repository.GiveawayMessageRepository;
import com.vendora.scrap.repository.GiveawayRepository;

class GiveawayServiceTest {

    private GiveawayRepository giveawayRepository;
    private GiveawayInterestRepository interestRepository;
    private GiveawayMessageRepository messageRepository;
    private GiveawayService giveawayService;

    @BeforeEach
    void setUp() {
        giveawayRepository = mock(GiveawayRepository.class);
        interestRepository = mock(GiveawayInterestRepository.class);
        messageRepository = mock(GiveawayMessageRepository.class);
        giveawayService = new GiveawayService(giveawayRepository, interestRepository, messageRepository);
    }

    @Test
    void testCreateGiveaway() {
        Giveaway saved = new Giveaway();
        saved.setId(1L);
        saved.setOwnerId(10L);
        saved.setTitle("Free Lab Coat");
        saved.setStatus("AVAILABLE");

        when(giveawayRepository.save(any(Giveaway.class))).thenReturn(saved);

        GiveawayResponse resp = giveawayService.createGiveaway(10L, new GiveawayCreateDto("Free Lab Coat", "Size M", "CLOTHING"));
        assertNotNull(resp);
        assertEquals(1L, resp.getId());
        assertEquals("AVAILABLE", resp.getStatus());
    }

    @Test
    void testExpressInterestSuccess() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);
        g.setStatus("AVAILABLE");

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));

        GiveawayInterest saved = new GiveawayInterest();
        saved.setId(100L);
        saved.setGiveawayId(1L);
        saved.setUserId(20L);
        saved.setStatus("PENDING");

        when(interestRepository.save(any(GiveawayInterest.class))).thenReturn(saved);

        GiveawayInterestResponse resp = giveawayService.expressInterest(1L, 20L, new GiveawayInterestDto("Can I collect?"));
        assertNotNull(resp);
        assertEquals(100L, resp.getId());
        assertEquals("PENDING", resp.getStatus());
    }

    @Test
    void testExpressInterestOwnGiveaway() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);
        g.setStatus("AVAILABLE");

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> giveawayService.expressInterest(1L, 10L, new GiveawayInterestDto("Mine")));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testAcceptInterest() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);
        g.setStatus("AVAILABLE");

        GiveawayInterest interest = new GiveawayInterest();
        interest.setId(50L);
        interest.setStatus("PENDING");

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));
        when(interestRepository.findById(50L)).thenReturn(Optional.of(interest));
        when(interestRepository.save(any(GiveawayInterest.class))).thenAnswer(i -> i.getArgument(0));

        GiveawayInterestResponse resp = giveawayService.acceptInterest(1L, 50L, 10L);
        assertEquals("ACCEPTED", resp.getStatus());
        assertEquals("CLAIMED", g.getStatus());
    }

    @Test
    void testSendMessageAndGetMessages() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));

        GiveawayMessage msg = new GiveawayMessage();
        msg.setId(200L);
        msg.setMessage("Is it available?");
        when(messageRepository.save(any(GiveawayMessage.class))).thenReturn(msg);
        when(messageRepository.findByGiveawayIdOrderByCreatedAtAsc(1L)).thenReturn(List.of(msg));

        GiveawayMessageResponse sent = giveawayService.sendMessage(1L, 20L, new GiveawayMessageDto("Is it available?"));
        assertEquals("Is it available?", sent.getMessage());

        List<GiveawayMessageResponse> list = giveawayService.getMessages(1L);
        assertEquals(1, list.size());
    }

    @Test
    void testGetAllAvailable() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setStatus("AVAILABLE");
        when(giveawayRepository.findByStatus("AVAILABLE")).thenReturn(List.of(g));

        List<GiveawayResponse> list = giveawayService.getAllAvailable();
        assertEquals(1, list.size());
    }

    @Test
    void testGetById_Success() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));

        GiveawayResponse resp = giveawayService.getById(1L);
        assertNotNull(resp);
        assertEquals(1L, resp.getId());
    }

    @Test
    void testGetById_NotFound() {
        when(giveawayRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> giveawayService.getById(999L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testExpressInterest_NotAvailable_ThrowsConflict() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setStatus("CLAIMED");
        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> giveawayService.expressInterest(1L, 20L, new GiveawayInterestDto("Want")));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testGetInterestsForGiveaway_Success() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);

        GiveawayInterest gi = new GiveawayInterest();
        gi.setId(5L);

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));
        when(interestRepository.findByGiveawayId(1L)).thenReturn(List.of(gi));

        List<GiveawayInterestResponse> list = giveawayService.getInterestsForGiveaway(1L, 10L);
        assertEquals(1, list.size());
    }

    @Test
    void testGetInterestsForGiveaway_Forbidden() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> giveawayService.getInterestsForGiveaway(1L, 999L));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testAcceptInterest_Forbidden() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> giveawayService.acceptInterest(1L, 50L, 999L));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testAcceptInterest_AlreadyClaimed_ThrowsBadRequest() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);
        g.setStatus("CLAIMED");

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> giveawayService.acceptInterest(1L, 50L, 10L));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testAcceptInterest_Cancelled_ThrowsBadRequest() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);
        g.setStatus("CANCELLED");

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> giveawayService.acceptInterest(1L, 50L, 10L));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testAcceptInterest_InterestNotFound() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);
        g.setStatus("AVAILABLE");

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));
        when(interestRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> giveawayService.acceptInterest(1L, 999L, 10L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void testCancelGiveaway_Success() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);
        g.setStatus("AVAILABLE");

        GiveawayInterest pending = new GiveawayInterest();
        pending.setId(5L);
        pending.setStatus("PENDING");

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));
        when(interestRepository.findByGiveawayId(1L)).thenReturn(List.of(pending));
        when(giveawayRepository.save(any(Giveaway.class))).thenAnswer(i -> i.getArgument(0));

        GiveawayResponse resp = giveawayService.cancelGiveaway(1L, 10L);
        assertEquals("CANCELLED", resp.getStatus());
        assertEquals("REJECTED", pending.getStatus());
    }

    @Test
    void testCancelGiveaway_Forbidden() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> giveawayService.cancelGiveaway(1L, 999L));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testCancelGiveaway_AlreadyClaimed() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);
        g.setStatus("CLAIMED");

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> giveawayService.cancelGiveaway(1L, 10L));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testCancelGiveaway_AlreadyCancelled() {
        Giveaway g = new Giveaway();
        g.setId(1L);
        g.setOwnerId(10L);
        g.setStatus("CANCELLED");

        when(giveawayRepository.findById(1L)).thenReturn(Optional.of(g));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> giveawayService.cancelGiveaway(1L, 10L));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
