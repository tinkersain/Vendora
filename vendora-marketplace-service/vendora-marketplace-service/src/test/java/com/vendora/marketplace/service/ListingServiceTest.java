package com.vendora.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.marketplace.dto.ListingRequest;
import com.vendora.marketplace.dto.ListingResponse;
import com.vendora.marketplace.dto.ListingUpdateRequest;
import com.vendora.marketplace.entity.Listing;
import com.vendora.marketplace.entity.ListingCategory;
import com.vendora.marketplace.entity.ListingCondition;
import com.vendora.marketplace.entity.ListingStatus;
import com.vendora.marketplace.repository.ListingRepository;

class ListingServiceTest {

    private ListingRepository listingRepository;
    private ListingService listingService;

    @BeforeEach
    void setUp() {
        listingRepository = mock(ListingRepository.class);
        listingService = new ListingService(listingRepository);
    }

    @Test
    void testCreateListing() {
        ListingRequest req = new ListingRequest();
        req.setTitle("Calculus Book");
        req.setDescription("Used for 1 semester");
        req.setPrice(new BigDecimal("350.00"));
        req.setQuantity(2);
        req.setCategory(ListingCategory.BOOKS);
        req.setCondition(ListingCondition.GOOD);

        Listing saved = new Listing();
        saved.setId(1L);
        saved.setTitle("Calculus Book");
        saved.setPrice(new BigDecimal("350.00"));
        saved.setQuantity(2);
        saved.setSellerId(10L);
        saved.setStatus(ListingStatus.AVAILABLE);

        when(listingRepository.save(any(Listing.class))).thenReturn(saved);

        ListingResponse resp = listingService.createListing(req, 10L);
        assertNotNull(resp);
        assertEquals(1L, resp.getId());
        assertEquals("Calculus Book", resp.getTitle());
        assertEquals(10L, resp.getSellerId());
        assertEquals(2, resp.getQuantity());
    }

    @Test
    void testGetListingById() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setTitle("Test");
        listing.setSellerId(10L);
        listing.setStatus(ListingStatus.AVAILABLE);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ListingResponse resp = listingService.getListingById(1L);
        assertEquals(1L, resp.getId());
    }

    @Test
    void testUpdateListingSuccess() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setTitle("Old Title");
        listing.setSellerId(10L);
        listing.setStatus(ListingStatus.AVAILABLE);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any(Listing.class))).thenAnswer(i -> i.getArgument(0));

        ListingUpdateRequest updateReq = new ListingUpdateRequest();
        updateReq.setTitle("New Title");
        updateReq.setPrice(new BigDecimal("400.00"));
        updateReq.setQuantity(3);
        updateReq.setCategory(ListingCategory.BOOKS);
        updateReq.setCondition(ListingCondition.LIKE_NEW);

        ListingResponse resp = listingService.updateListing(1L, 10L, updateReq);
        assertEquals("New Title", resp.getTitle());
        assertEquals(3, resp.getQuantity());
    }

    @Test
    void testUpdateListingForbidden() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);
        listing.setStatus(ListingStatus.AVAILABLE);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ListingUpdateRequest updateReq = new ListingUpdateRequest();
        updateReq.setTitle("New Title");

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> listingService.updateListing(1L, 99L, updateReq));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testDeleteListingSuccess() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        listingService.deleteListing(1L, 10L);
        verify(listingRepository).delete(listing);
    }

    @Test
    void testGetAllListings() {
        Listing l = new Listing();
        l.setId(1L);
        l.setStatus(ListingStatus.AVAILABLE);
        when(listingRepository.findByStatus(ListingStatus.AVAILABLE)).thenReturn(List.of(l));

        List<ListingResponse> res = listingService.getAllListings();
        assertEquals(1, res.size());
    }

    @Test
    void testGetListingsBySeller() {
        Listing l = new Listing();
        l.setId(1L);
        l.setSellerId(10L);
        when(listingRepository.findBySellerId(10L)).thenReturn(List.of(l));

        List<ListingResponse> res = listingService.getListingsBySeller(10L);
        assertEquals(1, res.size());
    }

    @Test
    void testSearchListings_Success() {
        Listing l = new Listing();
        l.setId(1L);
        l.setTitle("Calculus Book");
        when(listingRepository.searchListingsAcrossFields("calc")).thenReturn(List.of(l));

        List<ListingResponse> res = listingService.searchListings("calc");
        assertEquals(1, res.size());
    }

    @Test
    void testSearchListings_BlankKeyword_ThrowsBadRequest() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> listingService.searchListings("  "));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void testGetListingsByCategory() {
        Listing l = new Listing();
        l.setId(1L);
        l.setCategory(ListingCategory.BOOKS);
        when(listingRepository.findByCategoryAndStatus(ListingCategory.BOOKS, ListingStatus.AVAILABLE))
                .thenReturn(List.of(l));

        List<ListingResponse> res = listingService.getListingsByCategory(ListingCategory.BOOKS);
        assertEquals(1, res.size());
    }

    @Test
    void testUpdateListing_NotAvailable_ThrowsConflict() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);
        listing.setStatus(ListingStatus.SOLD);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ListingUpdateRequest updateReq = new ListingUpdateRequest();
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> listingService.updateListing(1L, 10L, updateReq));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testUpdateListingStatus_Success() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);
        listing.setStatus(ListingStatus.AVAILABLE);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));
        when(listingRepository.save(any(Listing.class))).thenAnswer(i -> i.getArgument(0));

        ListingResponse res = listingService.updateListingStatus(1L, 10L, ListingStatus.RESERVED);
        assertEquals(ListingStatus.RESERVED, res.getStatus());
    }

    @Test
    void testUpdateListingStatus_NotAvailable_ThrowsConflict() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);
        listing.setStatus(ListingStatus.SOLD);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> listingService.updateListingStatus(1L, 10L, ListingStatus.RESERVED));
        assertEquals(HttpStatus.CONFLICT, ex.getStatusCode());
    }

    @Test
    void testUpdateListingStatus_Forbidden() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> listingService.updateListingStatus(1L, 999L, ListingStatus.SOLD));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testDeleteListing_Forbidden() {
        Listing listing = new Listing();
        listing.setId(1L);
        listing.setSellerId(10L);

        when(listingRepository.findById(1L)).thenReturn(Optional.of(listing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> listingService.deleteListing(1L, 999L));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testFindListingOrThrow_NotFound() {
        when(listingRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> listingService.getListingById(999L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}
