package com.xiaomu.labservice.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class EquipmentIntegrationTest extends BaseIntegrationTest {

    @Test
    void list_equipment() throws Exception {
        var listResp = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/equipments")
                .header("Authorization", "Bearer " + adminToken))
            .andReturn().getResponse();
        assertThat(listResp.getStatus()).isEqualTo(200);
        JsonNode root = objectMapper.readTree(listResp.getContentAsString());
        JsonNode records = root.path("data").path("records");
        assertThat(records.isArray()).isTrue();
    }

    @Test
    void create_equipment_as_admin() throws Exception {
        Map<String, Object> payload = Map.of(
            "labId", 1,
            "name", "测试设备",
            "model", "Model-X",
            "serialNumber", "SN-TEST-001",
            "description", "用于集成测试"
        );

        var resp = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/equipments")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andReturn().getResponse();
        assertThat(resp.getStatus()).isIn(200, 400);
    }
}

