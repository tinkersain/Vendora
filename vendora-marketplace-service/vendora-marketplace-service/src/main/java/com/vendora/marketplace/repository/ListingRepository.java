package com.vendora.marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vendora.marketplace.entity.Listing;
import com.vendora.marketplace.entity.ListingCategory;
import com.vendora.marketplace.entity.ListingStatus;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findByStatus(ListingStatus status);

    List<Listing> findByCategoryAndStatus(
            ListingCategory category,
            ListingStatus status);

    List<Listing> findBySellerId(Long sellerId);

    List<Listing> findByTitleContainingIgnoreCase(String keyword);

    List<Listing> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    @Query("SELECT l FROM Listing l WHERE " +
           "LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(CAST(l.category AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Listing> searchListingsAcrossFields(@Param("keyword") String keyword);

    List<Listing> findBySellerIdAndStatus(
            Long sellerId,
            ListingStatus status);
}
