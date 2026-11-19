package com.vendora.scrap.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import com.vendora.scrap.dto.GiveawayCreateDto;
import com.vendora.scrap.dto.GiveawayInterestDto;
import com.vendora.scrap.dto.GiveawayInterestResponse;
import com.vendora.scrap.dto.GiveawayMessageDto;
import com.vendora.scrap.dto.GiveawayMessageResponse;
import com.vendora.scrap.dto.GiveawayResponse;
import com.vendora.scrap.service.GiveawayService;

@RestController
@RequestMapping("/api/giveaways")
public class GiveawayController {

    private final GiveawayService giveawayService;

    public GiveawayController(GiveawayService giveawayService) {
        this.giveawayService = giveawayService;
    }

    // 1. Create a giveaway (reads trusted X-User-Id from Gateway)
    @Operation(summary = "Create a giveaway (Uses login token to assign owner)")
    @PostMapping
    public ResponseEntity<GiveawayResponse> createGiveaway(
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerOwnerId,
            @Parameter(hidden = true) @RequestParam(value = "ownerId", required = false) Long paramOwnerId,
            @Valid @RequestBody GiveawayCreateDto dto) {

        Long effectiveOwnerId = headerOwnerId != null ? headerOwnerId : paramOwnerId;
        GiveawayResponse response = giveawayService.createGiveaway(effectiveOwnerId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. View all available giveaways
    @GetMapping
    public ResponseEntity<List<GiveawayResponse>> getAllAvailable() {
        return ResponseEntity.ok(giveawayService.getAllAvailable());
    }

    // 3. View giveaway by ID
    @Operation(summary = "View giveaway by ID")
    @GetMapping("/{id}")
    public ResponseEntity<GiveawayResponse> getById(
            @Parameter(description = "ID of the giveaway item", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(giveawayService.getById(id));
    }

    // 4. Express interest in a giveaway
    @Operation(summary = "Express interest in a giveaway item")
    @PostMapping("/{id}/interest")
    public ResponseEntity<GiveawayInterestResponse> expressInterest(
            @Parameter(description = "ID of the giveaway item you want to request", example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerUserId,
            @Parameter(hidden = true) @RequestParam(value = "userId", required = false) Long paramUserId,
            @Valid @RequestBody(required = false) GiveawayInterestDto dto) {

        Long effectiveUserId = headerUserId != null ? headerUserId : paramUserId;
        GiveawayInterestResponse response = giveawayService.expressInterest(id, effectiveUserId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 5. View interests for a giveaway (Owner only)
    @Operation(summary = "View interests for a giveaway (Owner only)")
    @GetMapping("/{id}/interests")
    public ResponseEntity<List<GiveawayInterestResponse>> getInterestsForGiveaway(
            @Parameter(description = "ID of your giveaway item", example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerOwnerId,
            @Parameter(hidden = true) @RequestParam(value = "ownerId", required = false) Long paramOwnerId) {

        Long effectiveOwnerId = headerOwnerId != null ? headerOwnerId : paramOwnerId;
        return ResponseEntity.ok(giveawayService.getInterestsForGiveaway(id, effectiveOwnerId));
    }

    // 6. Accept an interest (Owner only)
    @Operation(summary = "Accept an interest in a giveaway (Owner only)")
    @PatchMapping("/{id}/interests/{interestId}/accept")
    public ResponseEntity<GiveawayInterestResponse> acceptInterest(
            @Parameter(description = "ID of your giveaway item", example = "1")
            @PathVariable Long id,
            @Parameter(description = "ID of the interest request to accept", example = "1")
            @PathVariable Long interestId,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerOwnerId,
            @Parameter(hidden = true) @RequestParam(value = "ownerId", required = false) Long paramOwnerId) {

        Long effectiveOwnerId = headerOwnerId != null ? headerOwnerId : paramOwnerId;
        return ResponseEntity.ok(giveawayService.acceptInterest(id, interestId, effectiveOwnerId));
    }

    // 7. Send message regarding giveaway
    @Operation(summary = "Send message regarding giveaway")
    @PostMapping("/{id}/messages")
    public ResponseEntity<GiveawayMessageResponse> sendMessage(
            @Parameter(description = "ID of the giveaway item", example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerSenderId,
            @Parameter(hidden = true) @RequestParam(value = "senderId", required = false) Long paramSenderId,
            @Valid @RequestBody GiveawayMessageDto dto) {

        Long effectiveSenderId = headerSenderId != null ? headerSenderId : paramSenderId;
        GiveawayMessageResponse response = giveawayService.sendMessage(id, effectiveSenderId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 8. View messages for giveaway
    @Operation(summary = "View messages for giveaway")
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<GiveawayMessageResponse>> getMessages(
            @Parameter(description = "ID of the giveaway item", example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(giveawayService.getMessages(id));
    }

    // 9. Cancel giveaway (Owner only)
    @Operation(summary = "Cancel giveaway (Owner only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<GiveawayResponse> cancelGiveaway(
            @Parameter(description = "ID of your giveaway item to cancel", example = "1")
            @PathVariable Long id,
            @Parameter(hidden = true) @RequestHeader(value = "X-User-Id", required = false) Long headerOwnerId,
            @Parameter(hidden = true) @RequestParam(value = "ownerId", required = false) Long paramOwnerId) {

        Long effectiveOwnerId = headerOwnerId != null ? headerOwnerId : paramOwnerId;
        return ResponseEntity.ok(giveawayService.cancelGiveaway(id, effectiveOwnerId));
    }
}
