package com.vendora.marketplace.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

@Service
public class ChatService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ListingRepository listingRepository;

    public ChatService(
            ConversationRepository conversationRepository,
            ChatMessageRepository chatMessageRepository,
            ListingRepository listingRepository) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.listingRepository = listingRepository;
    }

    // 1. Start or retrieve existing conversation for a listing
    @Transactional
    public ConversationResponse startOrGetConversation(Long listingId, Long buyerId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Listing not found with ID: " + listingId));

        if (listing.getSellerId().equals(buyerId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "You cannot initiate a conversation on your own listing");
        }

        Conversation conversation = conversationRepository
                .findByListingIdAndBuyerId(listingId, buyerId)
                .orElseGet(() -> {
                    Conversation newConv = new Conversation();
                    newConv.setListingId(listingId);
                    newConv.setBuyerId(buyerId);
                    newConv.setSellerId(listing.getSellerId());
                    return conversationRepository.save(newConv);
                });

        return mapToConversationResponse(conversation);
    }

    // 2. Get user's active conversations
    @Transactional(readOnly = true)
    public List<ConversationResponse> getMyConversations(Long userId) {
        return conversationRepository.findByBuyerIdOrSellerId(userId, userId)
                .stream()
                .map(this::mapToConversationResponse)
                .toList();
    }

    // 3. Send message within a conversation
    @Transactional
    public ChatMessageResponse sendMessage(Long conversationId, Long senderId, ChatMessageDto dto) {
        Conversation conversation = findConversationOrThrow(conversationId);

        verifyParticipant(conversation, senderId);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setConversationId(conversationId);
        chatMessage.setSenderId(senderId);
        chatMessage.setMessage(dto.getMessage().trim());

        ChatMessage saved = chatMessageRepository.save(chatMessage);
        return mapToMessageResponse(saved);
    }

    // 4. Retrieve messages in a conversation
    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getMessages(Long conversationId, Long userId) {
        Conversation conversation = findConversationOrThrow(conversationId);

        verifyParticipant(conversation, userId);

        return chatMessageRepository.findByConversationIdOrderBySentAtAsc(conversationId)
                .stream()
                .map(this::mapToMessageResponse)
                .toList();
    }

    private Conversation findConversationOrThrow(Long id) {
        return conversationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Conversation not found with ID: " + id));
    }

    private void verifyParticipant(Conversation conversation, Long userId) {
        if (!conversation.getBuyerId().equals(userId) && !conversation.getSellerId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not a participant in this conversation");
        }
    }

    private ConversationResponse mapToConversationResponse(Conversation conv) {
        return new ConversationResponse(
                conv.getId(),
                conv.getListingId(),
                conv.getBuyerId(),
                conv.getSellerId(),
                conv.getCreatedAt()
        );
    }

    private ChatMessageResponse mapToMessageResponse(ChatMessage msg) {
        return new ChatMessageResponse(
                msg.getId(),
                msg.getConversationId(),
                msg.getSenderId(),
                msg.getMessage(),
                msg.getSentAt()
        );
    }
}
