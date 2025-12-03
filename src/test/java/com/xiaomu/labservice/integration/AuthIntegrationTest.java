package com.xiaomu.labservice.integration;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthIntegrationTest extends BaseIntegrationTest {

    @Test
    void login_and_get_me_and_logout() throws Exception {
        String token = adminToken;

        var meResp = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/me")
                .header("Authorization", "Bearer " + token))
            .andReturn().getResponse();
        assertThat(meResp.getStatus()).isEqualTo(200);

        var logoutResp = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
            .andReturn().getResponse();
        assertThat(logoutResp.getStatus()).isEqualTo(200);
    }

    @Test
    void unauthorized_access_should_fail() throws Exception {
        var resp = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/labs"))
            .andReturn().getResponse();
        assertThat(resp.getStatus()).isIn(401, 403);
    }
}

