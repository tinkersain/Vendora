package com.vendora.marketplace.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.marketplace.dto.ListingRequest;
import com.vendora.marketplace.dto.ListingResponse;
import com.vendora.marketplace.dto.ListingUpdateRequest;
import com.vendora.marketplace.entity.Listing;
import com.vendora.marketplace.entity.ListingCategory;
import com.vendora.marketplace.entity.ListingStatus;
import com.vendora.marketplace.repository.ListingRepository;

@Service
public class ListingService {

    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    // 1. Create a new listing
    @Transactional
    public ListingResponse createListing(ListingRequest request, Long authenticatedSellerId) {

        Listing listing = new Listing();

        listing.setTitle(request.getTitle().trim());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        listing.setQuantity(request.getQuantity() != null && request.getQuantity() > 0 ? request.getQuantity() : 1);
        listing.setCategory(request.getCategory());
        listing.setCondition(request.getCondition());
        listing.setStatus(ListingStatus.AVAILABLE);

        // Derive seller ID from trusted authenticated user context
        listing.setSellerId(authenticatedSellerId != null ? authenticatedSellerId : request.getSellerId());
        listing.setImageUrl(request.getImageUrl());

        Listing savedListing = listingRepository.save(listing);

        return mapToResponse(savedListing);
    }

    // 2. Retrieve all active listings
    @Transactional(readOnly = true)
    public List<ListingResponse> getAllListings() {

        return listingRepository.findByStatus(ListingStatus.AVAILABLE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 3. Retrieve listing by ID
    @Transactional(readOnly = true)
    public ListingResponse getListingById(Long id) {

        Listing listing = findListingOrThrow(id);

        return mapToResponse(listing);
    }

    // 4. Retrieve listings by seller
    @Transactional(readOnly = true)
    public List<ListingResponse> getListingsBySeller(Long sellerId) {

        return listingRepository.findBySellerId(sellerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 5. Search listings across title, description, and category
    @Transactional(readOnly = true)
    public List<ListingResponse> searchListings(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Search keyword cannot be empty");
        }

        return listingRepository.searchListingsAcrossFields(keyword.trim())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 6. Filter listings by category
    @Transactional(readOnly = true)
    public List<ListingResponse> getListingsByCategory(
            ListingCategory category) {

        return listingRepository.findByCategoryAndStatus(
                        category,
                        ListingStatus.AVAILABLE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 7. Update an existing listing
    @Transactional
    public ListingResponse updateListing(
            Long listingId,
            Long sellerId,
            ListingUpdateRequest request) {

        Listing listing = findListingOrThrow(listingId);

        verifyOwnership(listing, sellerId);

        if (listing.getStatus() != ListingStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only available listings can be updated");
        }

        listing.setTitle(request.getTitle().trim());
        listing.setDescription(request.getDescription());
        listing.setPrice(request.getPrice());
        if (request.getQuantity() != null && request.getQuantity() > 0) {
            listing.setQuantity(request.getQuantity());
        }
        listing.setCategory(request.getCategory());
        listing.setCondition(request.getCondition());
        listing.setImageUrl(request.getImageUrl());

        Listing updatedListing = listingRepository.save(listing);

        return mapToResponse(updatedListing);
    }

    // 8. Change listing status
    @Transactional
    public ListingResponse updateListingStatus(
            Long listingId,
            Long sellerId,
            ListingStatus newStatus) {

        Listing listing = findListingOrThrow(listingId);

        verifyOwnership(listing, sellerId);

        if (listing.getStatus() != ListingStatus.AVAILABLE && newStatus != ListingStatus.REMOVED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Only available listings can change status");
        }

        if (newStatus != ListingStatus.AVAILABLE
                && newStatus != ListingStatus.RESERVED
                && newStatus != ListingStatus.SOLD
                && newStatus != ListingStatus.REMOVED) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status transition");
        }

        listing.setStatus(newStatus);

        Listing updatedListing = listingRepository.save(listing);

        return mapToResponse(updatedListing);
    }

    // 9. Delete a listing
    @Transactional
    public void deleteListing(Long listingId, Long sellerId) {

        Listing listing = findListingOrThrow(listingId);

        verifyOwnership(listing, sellerId);

        listingRepository.delete(listing);
    }

    // Helper method: Find listing or throw 404
    public Listing findListingOrThrow(Long listingId) {

        return listingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Listing not found with ID: " + listingId));
    }

    // Helper method: Verify listing ownership
    private void verifyOwnership(Listing listing, Long sellerId) {

        if (!listing.getSellerId().equals(sellerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not authorized to modify this listing");
        }
    }

    // Convert Entity into Response DTO
    public ListingResponse mapToResponse(Listing listing) {

        ListingResponse response = new ListingResponse();

        response.setId(listing.getId());
        response.setTitle(listing.getTitle());
        response.setDescription(listing.getDescription());
        response.setPrice(listing.getPrice());
        response.setQuantity(listing.getQuantity());
        response.setCategory(listing.getCategory());
        response.setCondition(listing.getCondition());
        response.setStatus(listing.getStatus());
        response.setSellerId(listing.getSellerId());
        response.setImageUrl(listing.getImageUrl());
        response.setCreatedAt(listing.getCreatedAt());
        response.setUpdatedAt(listing.getUpdatedAt());

        return response;
    }
}
