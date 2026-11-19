package com.vendora.scrap.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.vendora.scrap.dto.GiveawayCreateDto;
import com.vendora.scrap.dto.GiveawayInterestDto;
import com.vendora.scrap.dto.GiveawayInterestResponse;
import com.vendora.scrap.dto.GiveawayMessageDto;
import com.vendora.scrap.dto.GiveawayMessageResponse;
import com.vendora.scrap.dto.GiveawayResponse;
import com.vendora.scrap.entity.Giveaway;
import com.vendora.scrap.entity.GiveawayInterest;
import com.vendora.scrap.entity.GiveawayMessage;
import com.vendora.scrap.repository.GiveawayInterestRepository;
import com.vendora.scrap.repository.GiveawayMessageRepository;
import com.vendora.scrap.repository.GiveawayRepository;

@Service
public class GiveawayService {

    private final GiveawayRepository giveawayRepository;
    private final GiveawayInterestRepository interestRepository;
    private final GiveawayMessageRepository messageRepository;

    public GiveawayService(
            GiveawayRepository giveawayRepository,
            GiveawayInterestRepository interestRepository,
            GiveawayMessageRepository messageRepository) {
        this.giveawayRepository = giveawayRepository;
        this.interestRepository = interestRepository;
        this.messageRepository = messageRepository;
    }

    // 1. Create a new giveaway
    @Transactional
    public GiveawayResponse createGiveaway(Long ownerId, GiveawayCreateDto dto) {
        Giveaway giveaway = new Giveaway();
        giveaway.setOwnerId(ownerId);
        giveaway.setTitle(dto.getTitle().trim());
        giveaway.setDescription(dto.getDescription());
        giveaway.setCategory(dto.getCategory());
        giveaway.setStatus("AVAILABLE");

        Giveaway saved = giveawayRepository.save(giveaway);
        return mapToResponse(saved);
    }

    // 2. Retrieve all available giveaways
    @Transactional(readOnly = true)
    public List<GiveawayResponse> getAllAvailable() {
        return giveawayRepository.findByStatus("AVAILABLE")
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // 3. Retrieve giveaway by ID
    @Transactional(readOnly = true)
    public GiveawayResponse getById(Long id) {
        Giveaway giveaway = findGiveawayOrThrow(id);
        return mapToResponse(giveaway);
    }

    // 4. Express interest in a giveaway
    @Transactional
    public GiveawayInterestResponse expressInterest(Long giveawayId, Long userId, GiveawayInterestDto dto) {
        Giveaway giveaway = findGiveawayOrThrow(giveawayId);

        if (!"AVAILABLE".equalsIgnoreCase(giveaway.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Giveaway is no longer available");
        }

        if (giveaway.getOwnerId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "You cannot express interest in your own giveaway");
        }

        GiveawayInterest interest = new GiveawayInterest();
        interest.setGiveawayId(giveawayId);
        interest.setUserId(userId);
        interest.setMessage(dto != null ? dto.getMessage() : null);
        interest.setStatus("PENDING");

        GiveawayInterest saved = interestRepository.save(interest);
        return mapToInterestResponse(saved);
    }

    // 5. View interests for a giveaway (Owner only)
    @Transactional(readOnly = true)
    public List<GiveawayInterestResponse> getInterestsForGiveaway(Long giveawayId, Long ownerId) {
        Giveaway giveaway = findGiveawayOrThrow(giveawayId);

        if (!giveaway.getOwnerId().equals(ownerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only the owner can view interests for this giveaway");
        }

        return interestRepository.findByGiveawayId(giveawayId)
                .stream()
                .map(this::mapToInterestResponse)
                .toList();
    }

    // 6. Accept an interest (Owner only)
    @Transactional
    public GiveawayInterestResponse acceptInterest(Long giveawayId, Long interestId, Long ownerId) {
        Giveaway giveaway = findGiveawayOrThrow(giveawayId);

        if (!giveaway.getOwnerId().equals(ownerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only the owner can accept interests for this giveaway");
        }

        if ("CLAIMED".equalsIgnoreCase(giveaway.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "This giveaway has already been claimed and given away");
        }

        if ("CANCELLED".equalsIgnoreCase(giveaway.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cannot accept an interest for a cancelled giveaway");
        }

        GiveawayInterest interest = interestRepository.findById(interestId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Interest not found with ID: " + interestId));

        interest.setStatus("ACCEPTED");
        giveaway.setStatus("CLAIMED");

        giveawayRepository.save(giveaway);
        GiveawayInterest saved = interestRepository.save(interest);

        // Reject other pending interests for this item
        List<GiveawayInterest> allInterests = interestRepository.findByGiveawayId(giveawayId);
        for (GiveawayInterest other : allInterests) {
            if (!other.getId().equals(interestId) && "PENDING".equalsIgnoreCase(other.getStatus())) {
                other.setStatus("REJECTED");
                interestRepository.save(other);
            }
        }

        return mapToInterestResponse(saved);
    }

    // 7. Send message regarding a giveaway
    @Transactional
    public GiveawayMessageResponse sendMessage(Long giveawayId, Long senderId, GiveawayMessageDto dto) {
        findGiveawayOrThrow(giveawayId);

        GiveawayMessage msg = new GiveawayMessage();
        msg.setGiveawayId(giveawayId);
        msg.setSenderId(senderId);
        msg.setMessage(dto.getMessage().trim());

        GiveawayMessage saved = messageRepository.save(msg);
        return mapToMessageResponse(saved);
    }

    // 8. Retrieve messages for a giveaway
    @Transactional(readOnly = true)
    public List<GiveawayMessageResponse> getMessages(Long giveawayId) {
        findGiveawayOrThrow(giveawayId);

        return messageRepository.findByGiveawayIdOrderByCreatedAtAsc(giveawayId)
                .stream()
                .map(this::mapToMessageResponse)
                .toList();
    }

    // 9. Cancel giveaway (Owner only)
    @Transactional
    public GiveawayResponse cancelGiveaway(Long giveawayId, Long ownerId) {
        Giveaway giveaway = findGiveawayOrThrow(giveawayId);

        if (!giveaway.getOwnerId().equals(ownerId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Only the owner can cancel this giveaway");
        }

        if ("CLAIMED".equalsIgnoreCase(giveaway.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Cannot cancel a giveaway that has already been awarded and claimed by a student");
        }

        if ("CANCELLED".equalsIgnoreCase(giveaway.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "This giveaway has already been cancelled");
        }

        giveaway.setStatus("CANCELLED");

        // Mark any remaining pending interests as REJECTED
        List<GiveawayInterest> allInterests = interestRepository.findByGiveawayId(giveawayId);
        for (GiveawayInterest interest : allInterests) {
            if ("PENDING".equalsIgnoreCase(interest.getStatus())) {
                interest.setStatus("REJECTED");
                interestRepository.save(interest);
            }
        }

        return mapToResponse(giveawayRepository.save(giveaway));
    }

    private Giveaway findGiveawayOrThrow(Long id) {
        return giveawayRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Giveaway not found with ID: " + id));
    }

    private GiveawayResponse mapToResponse(Giveaway g) {
        return new GiveawayResponse(
                g.getId(),
                g.getOwnerId(),
                g.getTitle(),
                g.getDescription(),
                g.getCategory(),
                g.getStatus(),
                g.getCreatedAt(),
                g.getUpdatedAt()
        );
    }

    private GiveawayInterestResponse mapToInterestResponse(GiveawayInterest i) {
        return new GiveawayInterestResponse(
                i.getId(),
                i.getGiveawayId(),
                i.getUserId(),
                i.getMessage(),
                i.getStatus(),
                i.getCreatedAt()
        );
    }

    private GiveawayMessageResponse mapToMessageResponse(GiveawayMessage m) {
        return new GiveawayMessageResponse(
                m.getId(),
                m.getGiveawayId(),
                m.getSenderId(),
                m.getMessage(),
                m.getCreatedAt()
        );
    }
}
