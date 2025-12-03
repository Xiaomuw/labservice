package com.xiaomu.labservice.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationIntegrationTest extends BaseIntegrationTest {

    @Test
    void create_and_approve_reservation_flow() throws Exception {
        Map<String, Object> payload = Map.of(
            "labId", 1,
            "title", "集成测试预约",
            "purpose", "测试系统",
            "startTime", LocalDateTime.now().plusHours(1).toString(),
            "endTime", LocalDateTime.now().plusHours(2).toString()
        );

        var createResp = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/reservations")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andReturn().getResponse();
        assertThat(createResp.getStatus()).isEqualTo(200);
        JsonNode root = objectMapper.readTree(createResp.getContentAsString());
        Long id = root.path("data").path("id").asLong();
        assertThat(id).isGreaterThan(0);

        var approveResp = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/reservations/admin/" + id + "/approve")
                .header("Authorization", "Bearer " + adminToken))
            .andReturn().getResponse();
        assertThat(approveResp.getStatus()).isEqualTo(200);
    }
}

