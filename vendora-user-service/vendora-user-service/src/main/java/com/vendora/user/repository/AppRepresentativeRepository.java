package com.vendora.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vendora.user.entity.AppRepresentative;

@Repository
public interface AppRepresentativeRepository extends JpaRepository<AppRepresentative, Long> {

    Optional<AppRepresentative> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}
