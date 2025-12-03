package com.xiaomu.labservice.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class RepairIntegrationTest extends BaseIntegrationTest {

    @Test
    void create_and_process_repair_flow() throws Exception {
        Map<String, Object> payload = Map.of(
            "equipmentId", 1,
            "title", "设备故障测试",
            "description", "风扇异响",
            "urgency", 2
        );

        var createResp = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/repairs")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andReturn().getResponse();
        assertThat(createResp.getStatus()).isEqualTo(200);
        JsonNode root = objectMapper.readTree(createResp.getContentAsString());
        Long id = root.path("data").path("id").asLong();
        assertThat(id).isGreaterThan(0);

        var processResp = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/repairs/admin/" + id + "/process")
                .header("Authorization", "Bearer " + adminToken))
            .andReturn().getResponse();
        assertThat(processResp.getStatus()).isEqualTo(200);
    }
}

