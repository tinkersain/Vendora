package com.vendora.marketplace.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

import com.vendora.marketplace.dto.MarketplaceRequestCreateDto;
import com.vendora.marketplace.dto.MarketplaceRequestResponse;
import com.vendora.marketplace.entity.MarketplaceRequestStatus;
import com.vendora.marketplace.service.MarketplaceRequestService;

@RestController
@RequestMapping("/api/listings")
public class MarketplaceRequestController {

    private final MarketplaceRequestService requestService;

    public MarketplaceRequestController(MarketplaceRequestService requestService) {
        this.requestService = requestService;
    }

    // 1. Buyer submits request for a listing
    @Operation(summary = "Buyer submits request for a listing (Uses login token)")
    @PostMapping("/{listingId}/requests")
    public ResponseEntity<MarketplaceRequestResponse> createRequest(
            @PathVariable Long listingId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerBuyerId,
            @Parameter(hidden = true) @RequestParam(value = "buyerId", required = false) Long paramBuyerId,
            @Valid @RequestBody MarketplaceRequestCreateDto dto) {

        Long effectiveBuyerId = headerBuyerId != null ? headerBuyerId : paramBuyerId;
        MarketplaceRequestResponse response = requestService.createRequest(listingId, effectiveBuyerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Seller views requests for their listing
    @Operation(summary = "Seller views requests for their listing")
    @GetMapping("/{listingId}/requests")
    public ResponseEntity<List<MarketplaceRequestResponse>> getRequestsForListing(
            @PathVariable Long listingId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerSellerId,
            @Parameter(hidden = true) @RequestParam(value = "sellerId", required = false) Long paramSellerId) {

        Long effectiveSellerId = headerSellerId != null ? headerSellerId : paramSellerId;
        return ResponseEntity.ok(requestService.getRequestsForListing(listingId, effectiveSellerId));
    }

    // 3. Buyer views all requests they created
    @Operation(summary = "Buyer views all requests they created")
    @GetMapping("/requests/my")
    public ResponseEntity<List<MarketplaceRequestResponse>> getMyRequests(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerBuyerId,
            @Parameter(hidden = true) @RequestParam(value = "buyerId", required = false) Long paramBuyerId) {

        Long effectiveBuyerId = headerBuyerId != null ? headerBuyerId : paramBuyerId;
        return ResponseEntity.ok(requestService.getMyRequests(effectiveBuyerId));
    }

    // 4. Seller updates request status (ACCEPTED, REJECTED)
    @Operation(summary = "Seller updates request status (ACCEPTED, REJECTED)")
    @PatchMapping("/requests/{requestId}/status")
    public ResponseEntity<MarketplaceRequestResponse> updateRequestStatus(
            @PathVariable Long requestId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerSellerId,
            @Parameter(hidden = true) @RequestParam(value = "sellerId", required = false) Long paramSellerId,
            @RequestParam MarketplaceRequestStatus status) {

        Long effectiveSellerId = headerSellerId != null ? headerSellerId : paramSellerId;
        return ResponseEntity.ok(requestService.updateRequestStatus(requestId, effectiveSellerId, status));
    }

    // 5. Buyer cancels their request
    @Operation(summary = "Buyer cancels their request")
    @PatchMapping("/requests/{requestId}/cancel")
    public ResponseEntity<MarketplaceRequestResponse> cancelRequest(
            @PathVariable Long requestId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerBuyerId,
            @Parameter(hidden = true) @RequestParam(value = "buyerId", required = false) Long paramBuyerId) {

        Long effectiveBuyerId = headerBuyerId != null ? headerBuyerId : paramBuyerId;
        return ResponseEntity.ok(requestService.cancelRequest(requestId, effectiveBuyerId));
    }
}
