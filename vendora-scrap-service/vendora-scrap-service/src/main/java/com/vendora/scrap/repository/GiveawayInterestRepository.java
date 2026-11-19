package com.vendora.scrap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vendora.scrap.entity.GiveawayInterest;

@Repository
public interface GiveawayInterestRepository extends JpaRepository<GiveawayInterest, Long> {

    List<GiveawayInterest> findByGiveawayId(Long giveawayId);

    List<GiveawayInterest> findByUserId(Long userId);
}
