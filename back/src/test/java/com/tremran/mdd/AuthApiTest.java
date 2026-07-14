package com.tremran.mdd;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.HttpStatus;

import com.tremran.mdd.controller.AuthController;

@SpringBootTest
@ActiveProfiles("test")
class AuthApiTest {

    @Autowired
    private AuthController authController;

    @Test
    void registerShouldCreateUser() {
        String uniqueEmail = "test-" + UUID.randomUUID() + "@example.com";
        var response = authController.register(new AuthController.RegisterRequest(uniqueEmail, "tester", "StrongPass123!"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
