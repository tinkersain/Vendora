package com.vendora.marketplace.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import com.vendora.marketplace.dto.ChatMessageDto;
import com.vendora.marketplace.dto.ChatMessageResponse;
import com.vendora.marketplace.dto.ConversationResponse;
import com.vendora.marketplace.service.ChatService;

@ExtendWith(MockitoExtension.class)
class ConversationControllerTest {

    @Mock
    private ChatService chatService;

    @InjectMocks
    private ConversationController conversationController;

    @Test
    @DisplayName("startOrGetConversation - returns 201 Created")
    void testStartOrGetConversation() {
        ConversationResponse resp = new ConversationResponse(1L, 100L, 10L, 20L, LocalDateTime.now());
        when(chatService.startOrGetConversation(100L, 10L)).thenReturn(resp);

        ResponseEntity<ConversationResponse> response = conversationController.startOrGetConversation(100L, 10L, null);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("getMyConversations - returns 200 OK")
    void testGetMyConversations() {
        ConversationResponse resp = new ConversationResponse(1L, 100L, 10L, 20L, LocalDateTime.now());
        when(chatService.getMyConversations(10L)).thenReturn(Collections.singletonList(resp));

        ResponseEntity<List<ConversationResponse>> response = conversationController.getMyConversations(null, 10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("sendMessage - returns 201 Created")
    void testSendMessage() {
        ChatMessageDto dto = new ChatMessageDto("Hello is this available?");
        ChatMessageResponse msgResp = new ChatMessageResponse(5L, 1L, 10L, "Hello is this available?", LocalDateTime.now());
        when(chatService.sendMessage(eq(1L), eq(10L), any(ChatMessageDto.class))).thenReturn(msgResp);

        ResponseEntity<ChatMessageResponse> response = conversationController.sendMessage(1L, 10L, null, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(5L, response.getBody().getId());
    }

    @Test
    @DisplayName("getMessages - returns 200 OK")
    void testGetMessages() {
        ChatMessageResponse msgResp = new ChatMessageResponse(5L, 1L, 10L, "Hello is this available?", LocalDateTime.now());
        when(chatService.getMessages(1L, 10L)).thenReturn(Collections.singletonList(msgResp));

        ResponseEntity<List<ChatMessageResponse>> response = conversationController.getMessages(1L, 10L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}
