package com.vendora.scrap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vendora.scrap.entity.GiveawayMessage;

@Repository
public interface GiveawayMessageRepository extends JpaRepository<GiveawayMessage, Long> {

    List<GiveawayMessage> findByGiveawayIdOrderByCreatedAtAsc(Long giveawayId);
}
