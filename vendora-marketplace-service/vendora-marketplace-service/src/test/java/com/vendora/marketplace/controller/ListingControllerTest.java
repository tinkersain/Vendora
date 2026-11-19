package com.vendora.marketplace.controller;

import java.math.BigDecimal;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.vendora.marketplace.dto.ListingRequest;
import com.vendora.marketplace.dto.ListingResponse;
import com.vendora.marketplace.dto.ListingUpdateRequest;
import com.vendora.marketplace.entity.ListingCategory;
import com.vendora.marketplace.entity.ListingCondition;
import com.vendora.marketplace.entity.ListingStatus;
import com.vendora.marketplace.service.ListingService;

@ExtendWith(MockitoExtension.class)
class ListingControllerTest {

    @Mock
    private ListingService listingService;

    @InjectMocks
    private ListingController listingController;

    private ListingResponse sampleListing;

    @BeforeEach
    void setUp() {
        sampleListing = new ListingResponse();
        sampleListing.setId(1L);
        sampleListing.setTitle("Engineering Textbook");
        sampleListing.setDescription("Hardcover 3rd Edition");
        sampleListing.setPrice(BigDecimal.valueOf(450.00));
        sampleListing.setQuantity(1);
        sampleListing.setCategory(ListingCategory.BOOKS);
        sampleListing.setCondition(ListingCondition.GOOD);
        sampleListing.setStatus(ListingStatus.AVAILABLE);
        sampleListing.setSellerId(10L);
    }

    @Test
    @DisplayName("createListing - returns 201 Created")
    void testCreateListing() {
        ListingRequest request = new ListingRequest();
        when(listingService.createListing(any(ListingRequest.class), eq(10L))).thenReturn(sampleListing);

        ResponseEntity<ListingResponse> response = listingController.createListing(10L, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Engineering Textbook", response.getBody().getTitle());
    }

    @Test
    @DisplayName("getAllListings - returns 200 OK")
    void testGetAllListings() {
        when(listingService.getAllListings()).thenReturn(Collections.singletonList(sampleListing));

        ResponseEntity<List<ListingResponse>> response = listingController.getAllListings();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getListingById - returns 200 OK")
    void testGetListingById() {
        when(listingService.getListingById(1L)).thenReturn(sampleListing);

        ResponseEntity<ListingResponse> response = listingController.getListingById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("getListingsBySeller - returns 200 OK")
    void testGetListingsBySeller() {
        when(listingService.getListingsBySeller(10L)).thenReturn(Collections.singletonList(sampleListing));

        ResponseEntity<List<ListingResponse>> response = listingController.getListingsBySeller(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("searchListings - returns 200 OK")
    void testSearchListings() {
        when(listingService.searchListings("Textbook")).thenReturn(Collections.singletonList(sampleListing));

        ResponseEntity<List<ListingResponse>> response = listingController.searchListings("Textbook");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getListingsByCategory - returns 200 OK")
    void testGetListingsByCategory() {
        when(listingService.getListingsByCategory(ListingCategory.BOOKS)).thenReturn(Collections.singletonList(sampleListing));

        ResponseEntity<List<ListingResponse>> response = listingController.getListingsByCategory(ListingCategory.BOOKS);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("updateListing - returns 200 OK with header sellerId")
    void testUpdateListing() {
        ListingUpdateRequest updateReq = new ListingUpdateRequest();
        when(listingService.updateListing(eq(1L), eq(10L), any(ListingUpdateRequest.class))).thenReturn(sampleListing);

        ResponseEntity<ListingResponse> response = listingController.updateListing(1L, 10L, null, updateReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("updateListingStatus - returns 200 OK")
    void testUpdateListingStatus() {
        sampleListing.setStatus(ListingStatus.SOLD);
        when(listingService.updateListingStatus(1L, 10L, ListingStatus.SOLD)).thenReturn(sampleListing);

        ResponseEntity<ListingResponse> response = listingController.updateListingStatus(1L, null, 10L, ListingStatus.SOLD);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ListingStatus.SOLD, response.getBody().getStatus());
    }

    @Test
    @DisplayName("deleteListing - returns 204 No Content")
    void testDeleteListing() {
        doNothing().when(listingService).deleteListing(1L, 10L);

        ResponseEntity<Void> response = listingController.deleteListing(1L, 10L, null);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
