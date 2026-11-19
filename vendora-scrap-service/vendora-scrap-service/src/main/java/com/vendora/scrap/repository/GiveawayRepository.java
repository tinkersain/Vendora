package com.vendora.scrap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vendora.scrap.entity.Giveaway;

@Repository
public interface GiveawayRepository extends JpaRepository<Giveaway, Long> {

    List<Giveaway> findByStatus(String status);

    List<Giveaway> findByOwnerId(Long ownerId);
}
