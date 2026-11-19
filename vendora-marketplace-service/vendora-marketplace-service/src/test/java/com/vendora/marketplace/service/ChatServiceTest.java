package com.vendora.marketplace.service;

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

import com.vendora.marketplace.dto.ChatMessageDto;
import com.vendora.marketplace.dto.ChatMessageResponse;
import com.vendora.marketplace.dto.ConversationResponse;
import com.vendora.marketplace.entity.ChatMessage;
import com.vendora.marketplace.entity.Conversation;
import com.vendora.marketplace.entity.Listing;
import com.vendora.marketplace.repository.ChatMessageRepository;
import com.vendora.marketplace.repository.ConversationRepository;
import com.vendora.marketplace.repository.ListingRepository;

class ChatServiceTest {

    private ConversationRepository conversationRepository;
    private ChatMessageRepository chatMessageRepository;
    private ListingRepository listingRepository;
    private ChatService chatService;

    @BeforeEach
    void setUp() {
        conversationRepository = mock(ConversationRepository.class);
        chatMessageRepository = mock(ChatMessageRepository.class);
        listingRepository = mock(ListingRepository.class);
        chatService = new ChatService(conversationRepository, chatMessageRepository, listingRepository);
    }

    @Test
    void testStartOrGetConversationSuccess() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(conversationRepository.findByListingIdAndBuyerId(1L, 20L)).thenReturn(Optional.empty());

        Conversation saved = new Conversation();
        saved.setId(100L);
        saved.setListingId(1L);
        saved.setBuyerId(20L);
        saved.setSellerId(10L);

        when(conversationRepository.save(any(Conversation.class))).thenReturn(saved);

        ConversationResponse resp = chatService.startOrGetConversation(1L, 20L);
        assertNotNull(resp);
        assertEquals(100L, resp.getId());
        assertEquals(20L, resp.getBuyerId());
        assertEquals(10L, resp.getSellerId());
    }

    @Test
    void testStartConversationOwnListing() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> chatService.startOrGetConversation(1L, 10L));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testSendMessageSuccess() {
        Conversation conv = new Conversation();
        conv.setId(5L);
        conv.setBuyerId(20L);
        conv.setSellerId(10L);

        when(conversationRepository.findById(5L)).thenReturn(Optional.of(conv));

        ChatMessage saved = new ChatMessage();
        saved.setId(1L);
        saved.setConversationId(5L);
        saved.setSenderId(20L);
        saved.setMessage("Hello seller");

        when(chatMessageRepository.save(any(ChatMessage.class))).thenReturn(saved);

        ChatMessageResponse resp = chatService.sendMessage(5L, 20L, new ChatMessageDto("Hello seller"));
        assertNotNull(resp);
        assertEquals("Hello seller", resp.getMessage());
    }

    @Test
    void testSendMessageForbidden() {
        Conversation conv = new Conversation();
        conv.setId(5L);
        conv.setBuyerId(20L);
        conv.setSellerId(10L);

        when(conversationRepository.findById(5L)).thenReturn(Optional.of(conv));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> chatService.sendMessage(5L, 999L, new ChatMessageDto("Intruder")));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
