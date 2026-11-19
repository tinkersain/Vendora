package com.vendora.scrap.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.vendora.scrap.entity.Giveaway;
import com.vendora.scrap.entity.GiveawayInterest;
import com.vendora.scrap.entity.GiveawayMessage;
import com.vendora.scrap.entity.QuantityUnit;
import com.vendora.scrap.entity.ScrapMaterialType;
import com.vendora.scrap.entity.ScrapRequest;
import com.vendora.scrap.entity.ScrapStatus;

class ScrapDtoAndEntityTest {

    @Test
    @DisplayName("ScrapRequest entity and DTO tests")
    void testScrapRequestEntityAndDtos() throws Exception {
        LocalDateTime pickupTime = LocalDateTime.now().plusDays(2);
        LocalDateTime now = LocalDateTime.now();

        ScrapRequest req = new ScrapRequest();
        req.setId(1L);
        req.setUserId(10L);
        req.setMaterialType(ScrapMaterialType.METAL);
        req.setDescription("Old metal pipes");
        req.setEstimatedQuantity(BigDecimal.valueOf(20.5));
        req.setQuantityUnit(QuantityUnit.KG);
        req.setPickupLocation("Hostel Block A");
        req.setPreferredPickupTime(pickupTime);
        req.setStatus(ScrapStatus.PENDING);
        req.setAssignedStaffId(5L);
        req.setCreatedAt(now);
        req.setUpdatedAt(now);

        assertEquals(1L, req.getId());
        assertEquals(10L, req.getUserId());
        assertEquals(ScrapMaterialType.METAL, req.getMaterialType());
        assertEquals("Old metal pipes", req.getDescription());
        assertEquals(BigDecimal.valueOf(20.5), req.getEstimatedQuantity());
        assertEquals(QuantityUnit.KG, req.getQuantityUnit());
        assertEquals("Hostel Block A", req.getPickupLocation());
        assertEquals(pickupTime, req.getPreferredPickupTime());
        assertEquals(ScrapStatus.PENDING, req.getStatus());
        assertEquals(5L, req.getAssignedStaffId());
        assertEquals(now, req.getCreatedAt());
        assertEquals(now, req.getUpdatedAt());

        // Lifecycle callbacks
        java.lang.reflect.Method onCreate = ScrapRequest.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(req);
        assertNotNull(req.getCreatedAt());
        assertNotNull(req.getUpdatedAt());

        java.lang.reflect.Method onUpdate = ScrapRequest.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(req);
        assertNotNull(req.getUpdatedAt());

        ScrapRequestDto dto = new ScrapRequestDto();
        dto.setUserId(10L);
        dto.setMaterialType(ScrapMaterialType.METAL);
        dto.setDescription("Old metal pipes");
        dto.setEstimatedQuantity(BigDecimal.valueOf(20.5));
        dto.setQuantityUnit(QuantityUnit.KG);
        dto.setPickupLocation("Hostel Block A");
        dto.setPreferredPickupTime(pickupTime);

        assertEquals(10L, dto.getUserId());
        assertEquals(ScrapMaterialType.METAL, dto.getMaterialType());
        assertEquals("Old metal pipes", dto.getDescription());
        assertEquals(BigDecimal.valueOf(20.5), dto.getEstimatedQuantity());
        assertEquals(QuantityUnit.KG, dto.getQuantityUnit());
        assertEquals("Hostel Block A", dto.getPickupLocation());
        assertEquals(pickupTime, dto.getPreferredPickupTime());

        ScrapResponseDto resp = new ScrapResponseDto();
        resp.setId(1L);
        resp.setUserId(10L);
        resp.setMaterialType(ScrapMaterialType.METAL);
        resp.setDescription("Old metal pipes");
        resp.setEstimatedQuantity(BigDecimal.valueOf(20.5));
        resp.setQuantityUnit(QuantityUnit.KG);
        resp.setPickupLocation("Hostel Block A");
        resp.setPreferredPickupTime(pickupTime);
        resp.setStatus(ScrapStatus.PENDING);
        resp.setAssignedStaffId(5L);
        resp.setCreatedAt(now);
        resp.setUpdatedAt(now);

        assertEquals(1L, resp.getId());
        assertEquals(10L, resp.getUserId());
        assertEquals(ScrapMaterialType.METAL, resp.getMaterialType());
        assertEquals("Old metal pipes", resp.getDescription());
        assertEquals(BigDecimal.valueOf(20.5), resp.getEstimatedQuantity());
        assertEquals(QuantityUnit.KG, resp.getQuantityUnit());
        assertEquals("Hostel Block A", resp.getPickupLocation());
        assertEquals(pickupTime, resp.getPreferredPickupTime());
        assertEquals(ScrapStatus.PENDING, resp.getStatus());
        assertEquals(5L, resp.getAssignedStaffId());
        assertEquals(now, resp.getCreatedAt());
        assertEquals(now, resp.getUpdatedAt());

        ScrapAssignmentDto assign = new ScrapAssignmentDto();
        assign.setAssignedStaffId(50L);
        assertEquals(50L, assign.getAssignedStaffId());
    }

    @Test
    @DisplayName("Giveaway entity and DTO tests")
    void testGiveawayEntityAndDtos() throws Exception {
        LocalDateTime now = LocalDateTime.now();

        Giveaway g = new Giveaway();
        g.setId(10L);
        g.setOwnerId(5L);
        g.setTitle("Old Chair");
        g.setDescription("Comfortable chair");
        g.setCategory("FURNITURE");
        g.setStatus("AVAILABLE");
        g.setCreatedAt(now);
        g.setUpdatedAt(now);

        assertEquals(10L, g.getId());
        assertEquals(5L, g.getOwnerId());
        assertEquals("Old Chair", g.getTitle());
        assertEquals("Comfortable chair", g.getDescription());
        assertEquals("FURNITURE", g.getCategory());
        assertEquals("AVAILABLE", g.getStatus());
        assertEquals(now, g.getCreatedAt());
        assertEquals(now, g.getUpdatedAt());

        // Lifecycle callbacks
        java.lang.reflect.Method onCreate = Giveaway.class.getDeclaredMethod("onCreate");
        onCreate.setAccessible(true);
        onCreate.invoke(g);
        assertNotNull(g.getCreatedAt());
        assertNotNull(g.getUpdatedAt());

        java.lang.reflect.Method onUpdate = Giveaway.class.getDeclaredMethod("onUpdate");
        onUpdate.setAccessible(true);
        onUpdate.invoke(g);
        assertNotNull(g.getUpdatedAt());

        GiveawayInterest gi = new GiveawayInterest();
        gi.setId(1L);
        gi.setGiveawayId(10L);
        gi.setUserId(20L);
        gi.setMessage("Interested");
        gi.setStatus("PENDING");
        gi.setCreatedAt(now);

        assertEquals(1L, gi.getId());
        assertEquals(10L, gi.getGiveawayId());
        assertEquals(20L, gi.getUserId());
        assertEquals("Interested", gi.getMessage());
        assertEquals("PENDING", gi.getStatus());
        assertEquals(now, gi.getCreatedAt());

        java.lang.reflect.Method giOnCreate = GiveawayInterest.class.getDeclaredMethod("onCreate");
        giOnCreate.setAccessible(true);
        giOnCreate.invoke(gi);
        assertNotNull(gi.getCreatedAt());

        GiveawayMessage gm = new GiveawayMessage();
        gm.setId(100L);
        gm.setGiveawayId(10L);
        gm.setSenderId(20L);
        gm.setMessage("Can pick up at 5pm");
        gm.setCreatedAt(now);

        assertEquals(100L, gm.getId());
        assertEquals(10L, gm.getGiveawayId());
        assertEquals(20L, gm.getSenderId());
        assertEquals("Can pick up at 5pm", gm.getMessage());
        assertEquals(now, gm.getCreatedAt());

        GiveawayCreateDto createDto = new GiveawayCreateDto("Old Chair", "Comfortable chair", "FURNITURE");
        assertEquals("Old Chair", createDto.getTitle());
        createDto.setTitle("Updated");
        createDto.setDescription("Desc");
        createDto.setCategory("CAT");
        assertEquals("Updated", createDto.getTitle());
        assertEquals("Desc", createDto.getDescription());
        assertEquals("CAT", createDto.getCategory());

        GiveawayInterestDto intDto = new GiveawayInterestDto("Hello");
        assertEquals("Hello", intDto.getMessage());
        intDto.setMessage("New message");
        assertEquals("New message", intDto.getMessage());

        GiveawayInterestDto emptyIntDto = new GiveawayInterestDto();
        emptyIntDto.setMessage("Empty");
        assertEquals("Empty", emptyIntDto.getMessage());

        GiveawayInterestResponse intResp = new GiveawayInterestResponse(1L, 10L, 20L, "Interested", "PENDING", now);
        assertEquals(1L, intResp.getId());
        assertEquals(10L, intResp.getGiveawayId());
        assertEquals(20L, intResp.getUserId());
        assertEquals("Interested", intResp.getMessage());
        assertEquals("PENDING", intResp.getStatus());
        assertEquals(now, intResp.getCreatedAt());

        GiveawayInterestResponse emptyIntResp = new GiveawayInterestResponse();
        emptyIntResp.setId(2L);
        emptyIntResp.setGiveawayId(11L);
        emptyIntResp.setUserId(21L);
        emptyIntResp.setMessage("Msg");
        emptyIntResp.setStatus("ACCEPTED");
        emptyIntResp.setCreatedAt(now);
        assertEquals(2L, emptyIntResp.getId());
        assertEquals(11L, emptyIntResp.getGiveawayId());
        assertEquals(21L, emptyIntResp.getUserId());
        assertEquals("Msg", emptyIntResp.getMessage());
        assertEquals("ACCEPTED", emptyIntResp.getStatus());
        assertEquals(now, emptyIntResp.getCreatedAt());

        GiveawayMessageDto msgDto = new GiveawayMessageDto("Hey");
        assertEquals("Hey", msgDto.getMessage());
        msgDto.setMessage("Hey there");
        assertEquals("Hey there", msgDto.getMessage());

        GiveawayMessageDto emptyMsgDto = new GiveawayMessageDto();
        emptyMsgDto.setMessage("Default");
        assertEquals("Default", emptyMsgDto.getMessage());

        GiveawayMessageResponse msgResp = new GiveawayMessageResponse(100L, 10L, 20L, "Hello", now);
        assertEquals(100L, msgResp.getId());
        assertEquals(10L, msgResp.getGiveawayId());
        assertEquals(20L, msgResp.getSenderId());
        assertEquals("Hello", msgResp.getMessage());
        assertEquals(now, msgResp.getCreatedAt());

        GiveawayMessageResponse emptyMsgResp = new GiveawayMessageResponse();
        emptyMsgResp.setId(101L);
        emptyMsgResp.setGiveawayId(11L);
        emptyMsgResp.setSenderId(21L);
        emptyMsgResp.setMessage("Hello 2");
        emptyMsgResp.setCreatedAt(now);
        assertEquals(101L, emptyMsgResp.getId());
        assertEquals(11L, emptyMsgResp.getGiveawayId());
        assertEquals(21L, emptyMsgResp.getSenderId());
        assertEquals("Hello 2", emptyMsgResp.getMessage());
        assertEquals(now, emptyMsgResp.getCreatedAt());

        GiveawayResponse giveResp = new GiveawayResponse(10L, 5L, "Old Chair", "Comfortable", "FURNITURE", "AVAILABLE", now, now);
        assertEquals(10L, giveResp.getId());
        assertEquals(5L, giveResp.getOwnerId());
        assertEquals("Old Chair", giveResp.getTitle());
        assertEquals("Comfortable", giveResp.getDescription());
        assertEquals("FURNITURE", giveResp.getCategory());
        assertEquals("AVAILABLE", giveResp.getStatus());
        assertEquals(now, giveResp.getCreatedAt());
        assertEquals(now, giveResp.getUpdatedAt());

        GiveawayResponse emptyGiveResp = new GiveawayResponse();
        emptyGiveResp.setId(11L);
        emptyGiveResp.setOwnerId(6L);
        emptyGiveResp.setTitle("Desk");
        emptyGiveResp.setDescription("Wooden");
        emptyGiveResp.setCategory("FURNITURE");
        emptyGiveResp.setStatus("TAKEN");
        emptyGiveResp.setCreatedAt(now);
        emptyGiveResp.setUpdatedAt(now);
        assertEquals(11L, emptyGiveResp.getId());
        assertEquals(6L, emptyGiveResp.getOwnerId());
        assertEquals("Desk", emptyGiveResp.getTitle());
        assertEquals("Wooden", emptyGiveResp.getDescription());
        assertEquals("FURNITURE", emptyGiveResp.getCategory());
        assertEquals("TAKEN", emptyGiveResp.getStatus());
        assertEquals(now, emptyGiveResp.getCreatedAt());
        assertEquals(now, emptyGiveResp.getUpdatedAt());
    }

    @Test
    @DisplayName("Enums coverage")
    void testEnums() {
        assertEquals(ScrapMaterialType.PAPER, ScrapMaterialType.valueOf("PAPER"));
        assertEquals(ScrapMaterialType.PLASTIC, ScrapMaterialType.valueOf("PLASTIC"));
        assertEquals(ScrapMaterialType.METAL, ScrapMaterialType.valueOf("METAL"));
        assertEquals(ScrapMaterialType.E_WASTE, ScrapMaterialType.valueOf("E_WASTE"));
        assertEquals(ScrapMaterialType.CARDBOARD, ScrapMaterialType.valueOf("CARDBOARD"));
        assertEquals(ScrapMaterialType.GLASS, ScrapMaterialType.valueOf("GLASS"));
        assertEquals(ScrapMaterialType.CLOTH, ScrapMaterialType.valueOf("CLOTH"));
        assertEquals(ScrapMaterialType.MIXED, ScrapMaterialType.valueOf("MIXED"));
        assertEquals(ScrapMaterialType.OTHER, ScrapMaterialType.valueOf("OTHER"));

        assertEquals(QuantityUnit.KG, QuantityUnit.valueOf("KG"));
        assertEquals(QuantityUnit.PIECES, QuantityUnit.valueOf("PIECES"));
        assertEquals(QuantityUnit.BAGS, QuantityUnit.valueOf("BAGS"));
        assertEquals(QuantityUnit.BOXES, QuantityUnit.valueOf("BOXES"));

        assertEquals(ScrapStatus.PENDING, ScrapStatus.valueOf("PENDING"));
        assertEquals(ScrapStatus.ASSIGNED, ScrapStatus.valueOf("ASSIGNED"));
        assertEquals(ScrapStatus.PICKUP_IN_PROGRESS, ScrapStatus.valueOf("PICKUP_IN_PROGRESS"));
        assertEquals(ScrapStatus.COLLECTED, ScrapStatus.valueOf("COLLECTED"));
        assertEquals(ScrapStatus.CLOSED, ScrapStatus.valueOf("CLOSED"));
        assertEquals(ScrapStatus.CANCELLED, ScrapStatus.valueOf("CANCELLED"));
    }
}
