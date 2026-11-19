package com.vendora.marketplace.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import com.vendora.marketplace.dto.ChatMessageDto;
import com.vendora.marketplace.dto.ChatMessageResponse;
import com.vendora.marketplace.dto.ConversationResponse;
import com.vendora.marketplace.service.ChatService;

@RestController
@RequestMapping("/api/listings/conversations")
public class ConversationController {

    private final ChatService chatService;

    public ConversationController(ChatService chatService) {
        this.chatService = chatService;
    }

    // 1. Start or get conversation for a listing
    @Operation(summary = "Start or get conversation for a listing")
    @PostMapping("/start")
    public ResponseEntity<ConversationResponse> startOrGetConversation(
            @RequestParam Long listingId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerBuyerId,
            @Parameter(hidden = true) @RequestParam(value = "buyerId", required = false) Long paramBuyerId) {

        Long effectiveBuyerId = headerBuyerId != null ? headerBuyerId : paramBuyerId;
        ConversationResponse response = chatService.startOrGetConversation(listingId, effectiveBuyerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get my active conversations
    @Operation(summary = "Get my active conversations")
    @GetMapping("/my")
    public ResponseEntity<List<ConversationResponse>> getMyConversations(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @Parameter(hidden = true) @RequestParam(value = "userId", required = false) Long paramUserId) {

        Long effectiveUserId = headerUserId != null ? headerUserId : paramUserId;
        return ResponseEntity.ok(chatService.getMyConversations(effectiveUserId));
    }

    // 3. Send message in a conversation
    @Operation(summary = "Send message in a conversation")
    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<ChatMessageResponse> sendMessage(
            @PathVariable Long conversationId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerSenderId,
            @Parameter(hidden = true) @RequestParam(value = "senderId", required = false) Long paramSenderId,
            @Valid @RequestBody ChatMessageDto dto) {

        Long effectiveSenderId = headerSenderId != null ? headerSenderId : paramSenderId;
        ChatMessageResponse response = chatService.sendMessage(conversationId, effectiveSenderId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 4. Get messages in a conversation
    @Operation(summary = "Get messages in a conversation")
    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<ChatMessageResponse>> getMessages(
            @PathVariable Long conversationId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @Parameter(hidden = true) @RequestParam(value = "userId", required = false) Long paramUserId) {

        Long effectiveUserId = headerUserId != null ? headerUserId : paramUserId;
        return ResponseEntity.ok(chatService.getMessages(conversationId, effectiveUserId));
    }
}
