package com.xiaomu.labservice.integration;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;

public class PerformanceTest extends BaseIntegrationTest {

    @Test
    void lab_list_response_time_under_500ms() throws Exception {
        long start = System.currentTimeMillis();
        var resp = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/labs")
                .header("Authorization", "Bearer " + adminToken))
            .andReturn().getResponse();
        long duration = System.currentTimeMillis() - start;
        assertThat(resp.getStatus()).isEqualTo(200);
        assertThat(duration).isLessThan(500);
    }
}

