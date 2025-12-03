package com.xiaomu.labservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String adminToken;
    protected String userToken;

    @BeforeEach
    void setUpTokens() throws Exception {
        if (adminToken == null) {
            adminToken = login("admin", "admin123");
        }
        if (userToken == null) {
            userToken = login("user1", "admin123");
        }
    }

    protected String login(String username, String password) throws Exception {
        Map<String, String> payload = Map.of("username", username, "password", password);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
            .andReturn();

        String content = result.getResponse().getContentAsString();
        Map<?, ?> resp = objectMapper.readValue(content, Map.class);
        assertThat(resp.get("code")).isEqualTo(200);
        Map<?, ?> data = (Map<?, ?>) resp.get("data");
        assertThat(data).isNotNull();
        String accessToken = (String) data.get("accessToken");
        assertThat(accessToken).isNotBlank();
        return accessToken;
    }
}

