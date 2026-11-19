package com.vendora.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.marketplace.dto.MarketplaceRequestCreateDto;
import com.vendora.marketplace.dto.MarketplaceRequestResponse;
import com.vendora.marketplace.entity.Listing;
import com.vendora.marketplace.entity.ListingStatus;
import com.vendora.marketplace.entity.MarketplaceRequest;
import com.vendora.marketplace.entity.MarketplaceRequestStatus;
import com.vendora.marketplace.repository.ListingRepository;
import com.vendora.marketplace.repository.MarketplaceRequestRepository;

class MarketplaceRequestServiceTest {

    private MarketplaceRequestRepository requestRepository;
    private ListingRepository listingRepository;
    private MarketplaceRequestService requestService;

    @BeforeEach
    void setUp() {
        requestRepository = mock(MarketplaceRequestRepository.class);
        listingRepository = mock(ListingRepository.class);
        requestService = new MarketplaceRequestService(requestRepository, listingRepository);
    }

    @Test
    void testCreateRequestSuccess() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);
        listing.setQuantity(5);
        listing.setStatus(ListingStatus.AVAILABLE);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        MarketplaceRequest saved = new MarketplaceRequest();
        saved.setId(100L);
        saved.setListingId(1L);
        saved.setBuyerId(20L);
        saved.setRequestedQuantity(2);
        saved.setStatus(MarketplaceRequestStatus.PENDING);

        when(requestRepository.save(any(MarketplaceRequest.class))).thenReturn(saved);

        MarketplaceRequestResponse resp = requestService.createRequest(1L, 20L, new MarketplaceRequestCreateDto(2, "I want to buy"));
        assertNotNull(resp);
        assertEquals(100L, resp.getId());
        assertEquals(MarketplaceRequestStatus.PENDING, resp.getStatus());
    }

    @Test
    void testCreateRequestOwnListing() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);
        listing.setQuantity(5);
        listing.setStatus(ListingStatus.AVAILABLE);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> requestService.createRequest(1L, 10L, new MarketplaceRequestCreateDto(1, "Self buy")));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testCreateRequestQuantityExceeded() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);
        listing.setQuantity(2);
        listing.setStatus(ListingStatus.AVAILABLE);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> requestService.createRequest(1L, 20L, new MarketplaceRequestCreateDto(5, "Too many")));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testAcceptRequestReducesQuantity() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);
        listing.setQuantity(3);
        listing.setStatus(ListingStatus.AVAILABLE);

        MarketplaceRequest req = new MarketplaceRequest();
        req.setId(50L);
        req.setListingId(1L);
        req.setBuyerId(20L);
        req.setRequestedQuantity(3);
        req.setStatus(MarketplaceRequestStatus.PENDING);

        when(requestRepository.findById(50L)).thenReturn(Optional.of(req));
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(requestRepository.save(any(MarketplaceRequest.class))).thenAnswer(i -> i.getArgument(0));

        MarketplaceRequestResponse resp = requestService.updateRequestStatus(50L, 10L, MarketplaceRequestStatus.ACCEPTED);
        assertEquals(MarketplaceRequestStatus.ACCEPTED, resp.getStatus());
        assertEquals(0, listing.getQuantity());
        assertEquals(ListingStatus.SOLD, listing.getStatus());
    }

    @Test
    void testGetRequestsForListing_Success() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);

        MarketplaceRequest req = new MarketplaceRequest();
        req.setId(50L);
        req.setListingId(1L);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(requestRepository.findByListingId(1L)).thenReturn(java.util.List.of(req));

        java.util.List<MarketplaceRequestResponse> list = requestService.getRequestsForListing(1L, 10L);
        assertEquals(1, list.size());
    }

    @Test
    void testGetRequestsForListing_Forbidden() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> requestService.getRequestsForListing(1L, 999L));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testGetMyRequests() {
        MarketplaceRequest req = new MarketplaceRequest();
        req.setId(50L);
        req.setBuyerId(20L);

        when(requestRepository.findByBuyerId(20L)).thenReturn(java.util.List.of(req));

        java.util.List<MarketplaceRequestResponse> list = requestService.getMyRequests(20L);
        assertEquals(1, list.size());
    }

    @Test
    void testUpdateRequestStatus_Rejected() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);

        MarketplaceRequest req = new MarketplaceRequest();
        req.setId(50L);
        req.setListingId(1L);
        req.setStatus(MarketplaceRequestStatus.PENDING);

        when(requestRepository.findById(50L)).thenReturn(Optional.of(req));
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(requestRepository.save(any(MarketplaceRequest.class))).thenAnswer(i -> i.getArgument(0));

        MarketplaceRequestResponse resp = requestService.updateRequestStatus(50L, 10L, MarketplaceRequestStatus.REJECTED);
        assertEquals(MarketplaceRequestStatus.REJECTED, resp.getStatus());
    }

    @Test
    void testUpdateRequestStatus_Forbidden() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);

        MarketplaceRequest req = new MarketplaceRequest();
        req.setId(50L);
        req.setListingId(1L);

        when(requestRepository.findById(50L)).thenReturn(Optional.of(req));
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> requestService.updateRequestStatus(50L, 999L, MarketplaceRequestStatus.REJECTED));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testUpdateRequestStatus_NotPending_ThrowsBadRequest() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);

        MarketplaceRequest req = new MarketplaceRequest();
        req.setId(50L);
        req.setListingId(1L);
        req.setStatus(MarketplaceRequestStatus.ACCEPTED);

        when(requestRepository.findById(50L)).thenReturn(Optional.of(req));
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> requestService.updateRequestStatus(50L, 10L, MarketplaceRequestStatus.REJECTED));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testUpdateRequestStatus_NotEnoughQuantity_ThrowsConflict() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);
        listing.setQuantity(1);
        listing.setStatus(ListingStatus.AVAILABLE);

        MarketplaceRequest req = new MarketplaceRequest();
        req.setId(50L);
        req.setListingId(1L);
        req.setRequestedQuantity(5);
        req.setStatus(MarketplaceRequestStatus.PENDING);

        when(requestRepository.findById(50L)).thenReturn(Optional.of(req));
        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> requestService.updateRequestStatus(50L, 10L, MarketplaceRequestStatus.ACCEPTED));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testCancelRequest_Success() {
        MarketplaceRequest req = new MarketplaceRequest();
        req.setId(50L);
        req.setBuyerId(20L);
        req.setStatus(MarketplaceRequestStatus.PENDING);

        when(requestRepository.findById(50L)).thenReturn(Optional.of(req));
        when(requestRepository.save(any(MarketplaceRequest.class))).thenAnswer(i -> i.getArgument(0));

        MarketplaceRequestResponse resp = requestService.cancelRequest(50L, 20L);
        assertEquals(MarketplaceRequestStatus.CANCELLED, resp.getStatus());
    }

    @Test
    void testCancelRequest_Forbidden() {
        MarketplaceRequest req = new MarketplaceRequest();
        req.setId(50L);
        req.setBuyerId(20L);

        when(requestRepository.findById(50L)).thenReturn(Optional.of(req));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> requestService.cancelRequest(50L, 999L));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testCancelRequest_NotPending_ThrowsBadRequest() {
        MarketplaceRequest req = new MarketplaceRequest();
        req.setId(50L);
        req.setBuyerId(20L);
        req.setStatus(MarketplaceRequestStatus.ACCEPTED);

        when(requestRepository.findById(50L)).thenReturn(Optional.of(req));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> requestService.cancelRequest(50L, 20L));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }
}
