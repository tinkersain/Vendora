package com.vendora.marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vendora.marketplace.entity.MarketplaceRequest;

@Repository
public interface MarketplaceRequestRepository extends JpaRepository<MarketplaceRequest, Long> {

    List<MarketplaceRequest> findByListingId(Long listingId);

    List<MarketplaceRequest> findByBuyerId(Long buyerId);
}
