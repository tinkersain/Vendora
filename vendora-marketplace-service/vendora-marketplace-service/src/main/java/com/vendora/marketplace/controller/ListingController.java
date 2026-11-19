package com.vendora.marketplace.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import com.vendora.marketplace.dto.ListingRequest;
import com.vendora.marketplace.dto.ListingResponse;
import com.vendora.marketplace.dto.ListingUpdateRequest;
import com.vendora.marketplace.entity.ListingCategory;
import com.vendora.marketplace.entity.ListingStatus;
import com.vendora.marketplace.service.ListingService;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    // 1. Create a new listing (trusted X-User-Id from Gateway)
    @Operation(summary = "Create a new listing (Uses login token to assign seller)")
    @PostMapping
    public ResponseEntity<ListingResponse> createListing(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long authenticatedUserId,
            @Valid @RequestBody ListingRequest request) {

        ListingResponse response = listingService.createListing(request, authenticatedUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get all listings
    @GetMapping
    public ResponseEntity<List<ListingResponse>> getAllListings() {

        return ResponseEntity.ok(listingService.getAllListings());
    }

    // 3. Get listing by ID
    @GetMapping("/{id}")
    public ResponseEntity<ListingResponse> getListingById(@PathVariable Long id) {

        return ResponseEntity.ok(listingService.getListingById(id));
    }

    // 4. Get listings by seller
    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ListingResponse>> getListingsBySeller(@PathVariable Long sellerId) {

        return ResponseEntity.ok(listingService.getListingsBySeller(sellerId));
    }

    // 5. Search listings
    @GetMapping("/search")
    public ResponseEntity<List<ListingResponse>> searchListings(@RequestParam String keyword) {

        return ResponseEntity.ok(listingService.searchListings(keyword));
    }

    // 6. Filter listings by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ListingResponse>> getListingsByCategory(
            @PathVariable ListingCategory category) {

        return ResponseEntity.ok(listingService.getListingsByCategory(category));
    }

    // 7. Update listing
    @Operation(summary = "Update an existing listing by its ID")
    @PutMapping("/{id}")
    public ResponseEntity<ListingResponse> updateListing(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerSellerId,
            @Parameter(hidden = true) @RequestParam(value = "sellerId", required = false) Long paramSellerId,
            @Valid @RequestBody ListingUpdateRequest request) {

        Long effectiveSellerId = headerSellerId != null ? headerSellerId : paramSellerId;
        return ResponseEntity.ok(listingService.updateListing(id, effectiveSellerId, request));
    }

    // 8. Change listing status
    @Operation(summary = "Update listing status (AVAILABLE, RESERVED, SOLD)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<ListingResponse> updateListingStatus(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerSellerId,
            @Parameter(hidden = true) @RequestParam(value = "sellerId", required = false) Long paramSellerId,
            @RequestParam ListingStatus status) {

        Long effectiveSellerId = headerSellerId != null ? headerSellerId : paramSellerId;
        return ResponseEntity.ok(listingService.updateListingStatus(id, effectiveSellerId, status));
    }

    // 9. Delete listing
    @Operation(summary = "Delete an existing listing by ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListing(
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerSellerId,
            @Parameter(hidden = true) @RequestParam(value = "sellerId", required = false) Long paramSellerId) {

        Long effectiveSellerId = headerSellerId != null ? headerSellerId : paramSellerId;
        listingService.deleteListing(id, effectiveSellerId);

        return ResponseEntity.noContent().build();
    }
}
