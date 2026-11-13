package com.university.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private String obtainToken() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"demo\",\"password\":\"demo-password\"}"))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode node = objectMapper.readTree(result.getResponse().getContentAsString());
        return node.get("token").asText();
    }

    @Test
    void loginReturnsToken() throws Exception {
        String token = obtainToken();
        assertThat(token).isNotBlank();
    }

    @Test
    void studentsRequiresAuth() throws Exception {
        mockMvc.perform(get("/api/v1/students"))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentsReturnsCursorPage() throws Exception {
        String token = obtainToken();
        MvcResult result = mockMvc.perform(get("/api/v1/students?limit=5")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.has("data")).isTrue();
        assertThat(body.has("hasMore")).isTrue();
        assertThat(body.get("data").isArray()).isTrue();
    }

    @Test
    void nearbyLocationsReturnsGeoJson() throws Exception {
        String token = obtainToken();
        MvcResult result = mockMvc.perform(
                        get("/api/v1/locations/nearby?latitude=40.7128&longitude=-74.0060&radius_km=5.0")
                                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode body = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(body.get("type").asText()).isEqualTo("FeatureCollection");
        assertThat(body.has("features")).isTrue();
    }

    @Test
    void createScheduleConflictReturns409() throws Exception {
        String token = obtainToken();
        // First create succeeds
        String payload = "{\"courseId\":1,\"locationId\":1,"
                + "\"startTime\":\"2026-01-01T10:00:00\",\"endTime\":\"2026-01-01T12:00:00\"}";
        mockMvc.perform(post("/api/v1/schedules")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());
        // Duplicate overlapping schedule returns 409
        mockMvc.perform(post("/api/v1/schedules")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict());
    }

    @Test
    void rateLimitHeadersPresentOnResponse() throws Exception {
        String token = obtainToken();
        MvcResult result = mockMvc.perform(get("/api/v1/students")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();
        assertThat(result.getResponse().getHeader("RateLimit-Limit")).isNotNull();
        assertThat(result.getResponse().getHeader("RateLimit-Remaining")).isNotNull();
        assertThat(result.getResponse().getHeader("RateLimit-Reset")).isNotNull();
    }
}
