package com.xiaomu.labservice.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;

public class LabIntegrationTest extends BaseIntegrationTest {

    @Test
    void list_labs_and_get_detail() throws Exception {
        var listResp = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/labs")
                .header("Authorization", "Bearer " + adminToken))
            .andReturn().getResponse();
        assertThat(listResp.getStatus()).isEqualTo(200);
        JsonNode root = objectMapper.readTree(listResp.getContentAsString());
        JsonNode records = root.path("data").path("records");
        assertThat(records.isArray()).isTrue();
        assertThat(records.size()).isGreaterThanOrEqualTo(1);

        var detailResp = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/labs/1")
                .header("Authorization", "Bearer " + adminToken))
            .andReturn().getResponse();
        assertThat(detailResp.getStatus()).isEqualTo(200);
    }
}

