package com.tremran.mdd.controller.v1;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void login_withInvalidCredentials_shouldReturnUnauthorizedError() throws Exception {
        UserEntity user = new UserEntity();
        user.setEmail("login@test.com");
        user.setPseudo("login");
        user.setPassword("$2a$10$cm2NIg777PDZ51VBAYMkT.VRvBbPTbcpkmIAhKwMNUD8x0QExnFwO");
        userRepository.save(user);

        String body = objectMapper.writeValueAsString(new LoginPayload("login@test.com", "wrong-password"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

            @ParameterizedTest
            @ValueSource(strings = {
                "Short1!",
                "alllowercase1!",
                "ALLUPPERCASE1!",
                "NoDigitPass!",
                "NoSpecial123A"
            })
            void register_withWeakPassword_shouldReturnBadRequest(String weakPassword) throws Exception {
            String body = objectMapper.writeValueAsString(
                new RegisterPayload("weak@test.com", "weak-user", weakPassword));

            mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message", containsString("password")))
                .andExpect(content().string(containsString("special character (=+_-$#!?)")));
            }

            @Test
            void register_withStrongPassword_shouldCreateUser() throws Exception {
            String body = objectMapper.writeValueAsString(
                new RegisterPayload("strong@test.com", "strong-user", "StrongPass123!"));

            mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value("strong@test.com"))
                .andExpect(jsonPath("$.user.pseudo").value("strong-user"));
            }

    private record LoginPayload(String email, String password) {
    }

    private record RegisterPayload(String email, String pseudo, String password) {
    }
}