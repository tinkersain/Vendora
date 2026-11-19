package com.vendora.marketplace.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vendora.marketplace.entity.Conversation;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByListingIdAndBuyerId(Long listingId, Long buyerId);

    List<Conversation> findByBuyerIdOrSellerId(Long buyerId, Long sellerId);
}
