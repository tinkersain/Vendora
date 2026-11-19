package com.vendora.marketplace.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.vendora.marketplace.entity.ChatMessage;
import com.vendora.marketplace.entity.Conversation;
import com.vendora.marketplace.entity.Listing;
import com.vendora.marketplace.entity.ListingCategory;
import com.vendora.marketplace.entity.ListingCondition;
import com.vendora.marketplace.entity.ListingStatus;
import com.vendora.marketplace.entity.MarketplaceRequest;
import com.vendora.marketplace.entity.MarketplaceRequestStatus;

class MarketplaceDtoAndEntityTest {

    @Test
    @DisplayName("Listing entity and DTO tests")
    void testListingEntityAndDtos() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setTitle("Desk Lamp");
        listing.setDescription("LED desk lamp with dimmer");
        listing.setPrice(BigDecimal.valueOf(250.00));
        listing.setQuantity(2);
        listing.setCategory(ListingCategory.ELECTRONICS);
        listing.setCondition(ListingCondition.LIKE_NEW);
        listing.setStatus(ListingStatus.AVAILABLE);
        listing.setSellerId(5L);
        listing.setImageUrl("http://lamp.jpg");
        listing.setCreatedAt(LocalDateTime.now());
        listing.setUpdatedAt(LocalDateTime.now());

        assertEquals(1L, listing.getId());
        assertEquals("Desk Lamp", listing.getTitle());
        assertEquals("LED desk lamp with dimmer", listing.getDescription());
        assertEquals(BigDecimal.valueOf(250.00), listing.getPrice());
        assertEquals(2, listing.getQuantity());
        assertEquals(ListingCategory.ELECTRONICS, listing.getCategory());
        assertEquals(ListingCondition.LIKE_NEW, listing.getCondition());
        assertEquals(ListingStatus.AVAILABLE, listing.getStatus());
        assertEquals(5L, listing.getSellerId());
        assertEquals("http://lamp.jpg", listing.getImageUrl());
        assertNotNull(listing.getCreatedAt());
        assertNotNull(listing.getUpdatedAt());

        ListingRequest req = new ListingRequest();
        req.setTitle("Desk Lamp");
        req.setDescription("LED");
        req.setPrice(BigDecimal.valueOf(250.00));
        req.setQuantity(2);
        req.setCategory(ListingCategory.ELECTRONICS);
        req.setCondition(ListingCondition.LIKE_NEW);
        req.setImageUrl("http://lamp.jpg");
        req.setSellerId(5L);

        assertEquals("Desk Lamp", req.getTitle());
        assertEquals("LED", req.getDescription());
        assertEquals(BigDecimal.valueOf(250.00), req.getPrice());
        assertEquals(2, req.getQuantity());
        assertEquals(ListingCategory.ELECTRONICS, req.getCategory());
        assertEquals(ListingCondition.LIKE_NEW, req.getCondition());
        assertEquals("http://lamp.jpg", req.getImageUrl());
        assertEquals(5L, req.getSellerId());

        ListingUpdateRequest updateReq = new ListingUpdateRequest();
        updateReq.setTitle("Updated Title");
        updateReq.setDescription("Updated Desc");
        updateReq.setPrice(BigDecimal.valueOf(300.00));
        updateReq.setQuantity(1);
        updateReq.setCategory(ListingCategory.OTHER);
        updateReq.setCondition(ListingCondition.FAIR);
        updateReq.setImageUrl("http://updated.jpg");

        assertEquals("Updated Title", updateReq.getTitle());
        assertEquals("Updated Desc", updateReq.getDescription());
        assertEquals(BigDecimal.valueOf(300.00), updateReq.getPrice());
        assertEquals(1, updateReq.getQuantity());
        assertEquals(ListingCategory.OTHER, updateReq.getCategory());
        assertEquals(ListingCondition.FAIR, updateReq.getCondition());
        assertEquals("http://updated.jpg", updateReq.getImageUrl());

        LocalDateTime now = LocalDateTime.now();
        ListingResponse resp = new ListingResponse();
        resp.setId(1L);
        resp.setTitle("Desk Lamp");
        resp.setDescription("LED");
        resp.setPrice(BigDecimal.valueOf(250.00));
        resp.setQuantity(2);
        resp.setCategory(ListingCategory.ELECTRONICS);
        resp.setCondition(ListingCondition.LIKE_NEW);
        resp.setStatus(ListingStatus.AVAILABLE);
        resp.setSellerId(5L);
        resp.setImageUrl("http://lamp.jpg");
        resp.setCreatedAt(now);
        resp.setUpdatedAt(now);

        assertEquals(1L, resp.getId());
        assertEquals("Desk Lamp", resp.getTitle());
        assertEquals("LED", resp.getDescription());
        assertEquals(BigDecimal.valueOf(250.00), resp.getPrice());
        assertEquals(2, resp.getQuantity());
        assertEquals(ListingCategory.ELECTRONICS, resp.getCategory());
        assertEquals(ListingCondition.LIKE_NEW, resp.getCondition());
        assertEquals(ListingStatus.AVAILABLE, resp.getStatus());
        assertEquals(5L, resp.getSellerId());
        assertEquals("http://lamp.jpg", resp.getImageUrl());
        assertEquals(now, resp.getCreatedAt());
        assertEquals(now, resp.getUpdatedAt());
    }

    @Test
    @DisplayName("Listing entity lifecycle callbacks")
    void testListingLifecycle() throws Exception {
        Listing listing = new Listing();
        java.lang.reflect.Method onCreate = Listing.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(listing);

        assertNotNull(listing.getCreatedAt());
        assertNotNull(listing.getUpdatedAt());
        assertEquals(ListingStatus.AVAILABLE, listing.getStatus());

        java.lang.reflect.Method onUpdate = Listing.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(listing);
        assertNotNull(listing.getUpdatedAt());
    }

    @Test
    @DisplayName("Conversation and ChatMessage entity and DTO tests")
    void testChatEntitiesAndDtos() {
        LocalDateTime now = LocalDateTime.now();
        Conversation conv = new Conversation();
        conv.setId(10L);
        conv.setListingId(1L);
        conv.setBuyerId(20L);
        conv.setSellerId(5L);
        conv.setCreatedAt(now);

        assertEquals(10L, conv.getId());
        assertEquals(1L, conv.getListingId());
        assertEquals(20L, conv.getBuyerId());
        assertEquals(5L, conv.getSellerId());
        assertEquals(now, conv.getCreatedAt());

        ChatMessage msg = new ChatMessage();
        msg.setId(50L);
        msg.setConversationId(10L);
        msg.setSenderId(20L);
        msg.setMessage("Is this still available?");
        msg.setSentAt(now);

        assertEquals(50L, msg.getId());
        assertEquals(10L, msg.getConversationId());
        assertEquals(20L, msg.getSenderId());
        assertEquals("Is this still available?", msg.getMessage());
        assertEquals(now, msg.getSentAt());

        ChatMessageDto msgDto = new ChatMessageDto("Hello");
        assertEquals("Hello", msgDto.getMessage());
        msgDto.setMessage("Updated");
        assertEquals("Updated", msgDto.getMessage());

        ChatMessageDto emptyMsgDto = new ChatMessageDto();
        emptyMsgDto.setMessage("Default");
        assertEquals("Default", emptyMsgDto.getMessage());

        ChatMessageResponse msgResp = new ChatMessageResponse(1L, 10L, 20L, "Hello", now);
        assertEquals(1L, msgResp.getId());
        assertEquals(10L, msgResp.getConversationId());
        assertEquals(20L, msgResp.getSenderId());
        assertEquals("Hello", msgResp.getMessage());
        assertEquals(now, msgResp.getSentAt());

        ChatMessageResponse emptyMsgResp = new ChatMessageResponse();
        emptyMsgResp.setId(2L);
        emptyMsgResp.setConversationId(11L);
        emptyMsgResp.setSenderId(21L);
        emptyMsgResp.setMessage("World");
        emptyMsgResp.setSentAt(now);
        assertEquals(2L, emptyMsgResp.getId());
        assertEquals(11L, emptyMsgResp.getConversationId());
        assertEquals(21L, emptyMsgResp.getSenderId());
        assertEquals("World", emptyMsgResp.getMessage());
        assertEquals(now, emptyMsgResp.getSentAt());

        ConversationResponse convResp = new ConversationResponse(10L, 1L, 20L, 5L, now);
        assertEquals(10L, convResp.getId());
        assertEquals(1L, convResp.getListingId());
        assertEquals(20L, convResp.getBuyerId());
        assertEquals(5L, convResp.getSellerId());
        assertEquals(now, convResp.getCreatedAt());

        ConversationResponse emptyConvResp = new ConversationResponse();
        emptyConvResp.setId(11L);
        emptyConvResp.setListingId(2L);
        emptyConvResp.setBuyerId(21L);
        emptyConvResp.setSellerId(6L);
        emptyConvResp.setCreatedAt(now);
        assertEquals(11L, emptyConvResp.getId());
        assertEquals(2L, emptyConvResp.getListingId());
        assertEquals(21L, emptyConvResp.getBuyerId());
        assertEquals(6L, emptyConvResp.getSellerId());
        assertEquals(now, emptyConvResp.getCreatedAt());
    }

    @Test
    @DisplayName("MarketplaceRequest entity and DTO tests")
    void testMarketplaceRequestEntitiesAndDtos() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        MarketplaceRequest req = new MarketplaceRequest();
        req.setId(100L);
        req.setListingId(1L);
        req.setBuyerId(20L);
        req.setRequestedQuantity(2);
        req.setMessage("Can pick up today");
        req.setStatus(MarketplaceRequestStatus.PENDING);
        req.setCreatedAt(now);
        req.setUpdatedAt(now);

        assertEquals(100L, req.getId());
        assertEquals(1L, req.getListingId());
        assertEquals(20L, req.getBuyerId());
        assertEquals(2, req.getRequestedQuantity());
        assertEquals("Can pick up today", req.getMessage());
        assertEquals(MarketplaceRequestStatus.PENDING, req.getStatus());
        assertEquals(now, req.getCreatedAt());
        assertEquals(now, req.getUpdatedAt());

        // Lifecycle callbacks
        java.lang.reflect.Method onCreate = MarketplaceRequest.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(req);
        assertNotNull(req.getCreatedAt());
        assertNotNull(req.getUpdatedAt());
        assertEquals(MarketplaceRequestStatus.PENDING, req.getStatus());

        java.lang.reflect.Method onUpdate = MarketplaceRequest.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(req);
        assertNotNull(req.getUpdatedAt());

        MarketplaceRequestCreateDto createDto = new MarketplaceRequestCreateDto(1, "Pick up now");
        assertEquals(1, createDto.getRequestedQuantity());
        assertEquals("Pick up now", createDto.getMessage());
        createDto.setRequestedQuantity(2);
        createDto.setMessage("Pick up later");
        assertEquals(2, createDto.getRequestedQuantity());
        assertEquals("Pick up later", createDto.getMessage());

        MarketplaceRequestCreateDto emptyCreateDto = new MarketplaceRequestCreateDto();
        emptyCreateDto.setRequestedQuantity(5);
        emptyCreateDto.setMessage("Hello");
        assertEquals(5, emptyCreateDto.getRequestedQuantity());
        assertEquals("Hello", emptyCreateDto.getMessage());

        MarketplaceRequestResponse resp = new MarketplaceRequestResponse(
                100L, 1L, 20L, 2, "Pick up later",
                MarketplaceRequestStatus.ACCEPTED, now, now
        );
        assertEquals(100L, resp.getId());
        assertEquals(1L, resp.getListingId());
        assertEquals(20L, resp.getBuyerId());
        assertEquals(2, resp.getRequestedQuantity());
        assertEquals("Pick up later", resp.getMessage());
        assertEquals(MarketplaceRequestStatus.ACCEPTED, resp.getStatus());
        assertEquals(now, resp.getCreatedAt());
        assertEquals(now, resp.getUpdatedAt());

        MarketplaceRequestResponse emptyResp = new MarketplaceRequestResponse();
        emptyResp.setId(101L);
        emptyResp.setListingId(2L);
        emptyResp.setBuyerId(21L);
        emptyResp.setRequestedQuantity(3);
        emptyResp.setMessage("Test");
        emptyResp.setStatus(MarketplaceRequestStatus.REJECTED);
        emptyResp.setCreatedAt(now);
        emptyResp.setUpdatedAt(now);
        assertEquals(101L, emptyResp.getId());
        assertEquals(2L, emptyResp.getListingId());
        assertEquals(21L, emptyResp.getBuyerId());
        assertEquals(3, emptyResp.getRequestedQuantity());
        assertEquals("Test", emptyResp.getMessage());
        assertEquals(MarketplaceRequestStatus.REJECTED, emptyResp.getStatus());
        assertEquals(now, emptyResp.getCreatedAt());
        assertEquals(now, emptyResp.getUpdatedAt());
    }

    @Test
    @DisplayName("Enums coverage")
    void testEnums() {
        assertEquals(ListingCategory.BOOKS, ListingCategory.valueOf("BOOKS"));
        assertEquals(ListingCategory.ELECTRONICS, ListingCategory.valueOf("ELECTRONICS"));
        assertEquals(ListingCategory.FURNITURE, ListingCategory.valueOf("FURNITURE"));
        assertEquals(ListingCategory.CLOTHING, ListingCategory.valueOf("CLOTHING"));
        assertEquals(ListingCategory.OTHER, ListingCategory.valueOf("OTHER"));

        assertEquals(ListingCondition.NEW, ListingCondition.valueOf("NEW"));
        assertEquals(ListingCondition.LIKE_NEW, ListingCondition.valueOf("LIKE_NEW"));
        assertEquals(ListingCondition.GOOD, ListingCondition.valueOf("GOOD"));
        assertEquals(ListingCondition.FAIR, ListingCondition.valueOf("FAIR"));

        assertEquals(ListingStatus.AVAILABLE, ListingStatus.valueOf("AVAILABLE"));
        assertEquals(ListingStatus.RESERVED, ListingStatus.valueOf("RESERVED"));
        assertEquals(ListingStatus.SOLD, ListingStatus.valueOf("SOLD"));

        assertEquals(MarketplaceRequestStatus.PENDING, MarketplaceRequestStatus.valueOf("PENDING"));
        assertEquals(MarketplaceRequestStatus.ACCEPTED, MarketplaceRequestStatus.valueOf("ACCEPTED"));
        assertEquals(MarketplaceRequestStatus.REJECTED, MarketplaceRequestStatus.valueOf("REJECTED"));
        assertEquals(MarketplaceRequestStatus.CANCELLED, MarketplaceRequestStatus.valueOf("CANCELLED"));
    }
}
