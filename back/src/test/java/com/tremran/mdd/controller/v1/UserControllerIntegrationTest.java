package com.tremran.mdd.controller.v1;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tremran.mdd.model.UserEntity;

@SpringBootTest
@ActiveProfiles("test")
class UserControllerIntegrationTest extends ControllerIntegrationTestSupport {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        mockMvc = createMockMvc();
    }

    @Test
    void getCurrentUser_withValidJwt_shouldReturnAuthenticatedUser() throws Exception {
        UserEntity user = createUser("me@test.com", "moi", "password");
        String token = createToken(user);

        mockMvc.perform(get("/api/v1/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("me@test.com"))
                .andExpect(jsonPath("$.pseudo").value("moi"));
    }

    @Test
    void updateCurrentUser_withValidJwt_shouldReturnUpdatedUser() throws Exception {
        UserEntity user = createUser("before@test.com", "before", "password");
        String token = createToken(user);
        String body = objectMapper.writeValueAsString(new UpdateMePayload("after@test.com", "after", "StrongPass123"));

        mockMvc.perform(put("/api/v1/me")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.email").value("after@test.com"))
                .andExpect(jsonPath("$.pseudo").value("after"));
    }

    private record UpdateMePayload(String email, String pseudo, String password) {
    }
}