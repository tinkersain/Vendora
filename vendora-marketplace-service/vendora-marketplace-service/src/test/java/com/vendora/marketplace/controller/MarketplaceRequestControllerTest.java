package com.vendora.marketplace.controller;

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

import com.vendora.marketplace.dto.MarketplaceRequestCreateDto;
import com.vendora.marketplace.dto.MarketplaceRequestResponse;
import com.vendora.marketplace.entity.MarketplaceRequestStatus;
import com.vendora.marketplace.service.MarketplaceRequestService;

@ExtendWith(MockitoExtension.class)
class MarketplaceRequestControllerTest {

    @Mock
    private MarketplaceRequestService requestService;

    @InjectMocks
    private MarketplaceRequestController controller;

    private MarketplaceRequestResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new MarketplaceRequestResponse(
                1L, 100L, 10L, 1, "Interested",
                MarketplaceRequestStatus.PENDING, LocalDateTime.now(), LocalDateTime.now()
        );
    }

    @Test
    @DisplayName("createRequest - returns 201 Created")
    void testCreateRequest() {
        MarketplaceRequestCreateDto dto = new MarketplaceRequestCreateDto(1, "Interested");
        when(requestService.createRequest(eq(100L), eq(10L), any(MarketplaceRequestCreateDto.class))).thenReturn(sampleResponse);

        ResponseEntity<MarketplaceRequestResponse> response = controller.createRequest(100L, 10L, null, dto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    @DisplayName("getRequestsForListing - returns 200 OK")
    void testGetRequestsForListing() {
        when(requestService.getRequestsForListing(100L, 20L)).thenReturn(Collections.singletonList(sampleResponse));

        ResponseEntity<List<MarketplaceRequestResponse>> response = controller.getRequestsForListing(100L, 20L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("getMyRequests - returns 200 OK")
    void testGetMyRequests() {
        when(requestService.getMyRequests(10L)).thenReturn(Collections.singletonList(sampleResponse));

        ResponseEntity<List<MarketplaceRequestResponse>> response = controller.getMyRequests(10L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    @DisplayName("updateRequestStatus - returns 200 OK")
    void testUpdateRequestStatus() {
        sampleResponse.setStatus(MarketplaceRequestStatus.ACCEPTED);
        when(requestService.updateRequestStatus(1L, 20L, MarketplaceRequestStatus.ACCEPTED)).thenReturn(sampleResponse);

        ResponseEntity<MarketplaceRequestResponse> response = controller.updateRequestStatus(1L, 20L, null, MarketplaceRequestStatus.ACCEPTED);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MarketplaceRequestStatus.ACCEPTED, response.getBody().getStatus());
    }

    @Test
    @DisplayName("cancelRequest - returns 200 OK")
    void testCancelRequest() {
        sampleResponse.setStatus(MarketplaceRequestStatus.CANCELLED);
        when(requestService.cancelRequest(1L, 10L)).thenReturn(sampleResponse);

        ResponseEntity<MarketplaceRequestResponse> response = controller.cancelRequest(1L, 10L, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MarketplaceRequestStatus.CANCELLED, response.getBody().getStatus());
    }
}
