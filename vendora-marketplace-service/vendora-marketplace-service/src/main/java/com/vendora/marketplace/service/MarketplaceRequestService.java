package com.vendora.marketplace.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.marketplace.dto.MarketplaceRequestCreateDto;
import com.vendora.marketplace.dto.MarketplaceRequestResponse;
import com.vendora.marketplace.entity.Listing;
import com.vendora.marketplace.entity.ListingStatus;
import com.vendora.marketplace.entity.MarketplaceRequest;
import com.vendora.marketplace.entity.MarketplaceRequestStatus;
import com.vendora.marketplace.repository.ListingRepository;
import com.vendora.marketplace.repository.MarketplaceRequestRepository;

@Service
public class MarketplaceRequestService {

    private final MarketplaceRequestRepository requestRepository;
    private final ListingRepository listingRepository;

    public MarketplaceRequestService(
            MarketplaceRequestRepository requestRepository,
            ListingRepository listingRepository) {
        this.requestRepository = requestRepository;
        this.listingRepository = listingRepository;
    }

    // 1. Buyer creates a purchase request
    @Transactional
    public MarketplaceRequestResponse createRequest(
            Long listingId, Long buyerId, MarketplaceRequestCreateDto dto) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Listing not found with ID: " + listingId));

        if (listing.getStatus() != ListingStatus.AVAILABLE) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Listing is not currently available for requests");
        }

        if (listing.getSellerId().equals(buyerId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "You cannot submit a request for your own listing");
        }

        if (dto.getRequestedQuantity() > listing.getQuantity()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Requested quantity (" + dto.getRequestedQuantity()
                            + ") exceeds available quantity (" + listing.getQuantity() + ")");
        }

        MarketplaceRequest request = new MarketplaceRequest();
        request.setListingId(listingId);
        request.setBuyerId(buyerId);
        request.setRequestedQuantity(dto.getRequestedQuantity());
        request.setMessage(dto.getMessage());
        request.setStatus(MarketplaceRequestStatus.PENDING);

        MarketplaceRequest saved = requestRepository.save(request);
        return mapToResponse(saved);
    }

    // 2. Seller views all requests for their listing
    @Transactional(readOnly = true)
    public List<MarketplaceRequestResponse> getRequestsForListing(Long listingId, Long sellerId) {

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Listing not found with ID: " + listingId));

        if (!listing.getSellerId().equals(sellerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not authorized to view requests for this listing");
        }

        return requestRepository.findByListingId(listingId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 3. Buyer views all requests they created
    @Transactional(readOnly = true)
    public List<MarketplaceRequestResponse> getMyRequests(Long buyerId) {

        return requestRepository.findByBuyerId(buyerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 4. Seller updates request status (ACCEPTED or REJECTED)
    @Transactional
    public MarketplaceRequestResponse updateRequestStatus(
            Long requestId, Long sellerId, MarketplaceRequestStatus newStatus) {

        MarketplaceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Request not found with ID: " + requestId));

        Listing listing = listingRepository.findById(request.getListingId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Listing not found with ID: " + request.getListingId()));

        if (!listing.getSellerId().equals(sellerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You are not authorized to respond to this request");
        }

        if (request.getStatus() != MarketplaceRequestStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only pending requests can be updated");
        }

        if (newStatus == MarketplaceRequestStatus.ACCEPTED) {
            if (listing.getStatus() != ListingStatus.AVAILABLE || listing.getQuantity() < request.getRequestedQuantity()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Not enough quantity available to accept this request");
            }

            int remaining = listing.getQuantity() - request.getRequestedQuantity();
            listing.setQuantity(remaining);
            if (remaining == 0) {
                listing.setStatus(ListingStatus.SOLD);
            }
            listingRepository.save(listing);
        }

        request.setStatus(newStatus);
        MarketplaceRequest saved = requestRepository.save(request);
        return mapToResponse(saved);
    }

    // 5. Buyer cancels pending request
    @Transactional
    public MarketplaceRequestResponse cancelRequest(Long requestId, Long buyerId) {

        MarketplaceRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Request not found with ID: " + requestId));

        if (!request.getBuyerId().equals(buyerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "You can only cancel your own requests");
        }

        if (request.getStatus() != MarketplaceRequestStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Only pending requests can be cancelled");
        }

        request.setStatus(MarketplaceRequestStatus.CANCELLED);
        MarketplaceRequest saved = requestRepository.save(request);
        return mapToResponse(saved);
    }

    public MarketplaceRequestResponse mapToResponse(MarketplaceRequest request) {
        return new MarketplaceRequestResponse(
                request.getId(),
                request.getListingId(),
                request.getBuyerId(),
                request.getRequestedQuantity(),
                request.getMessage(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }
}
