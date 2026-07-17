package com.tremran.mdd.controller.v1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.tremran.mdd.model.TopicEntity;
import com.tremran.mdd.model.UserEntity;

@SpringBootTest
@ActiveProfiles("test")
class SubscriptionControllerIntegrationTest extends ControllerIntegrationTestSupport {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        subscriptionRepository.deleteAll();
        userRepository.deleteAll();
        topicRepository.deleteAll();
        mockMvc = createMockMvc();
    }

    @Test
    void subscribe_withValidJwt_shouldCreateSubscription() throws Exception {
        UserEntity user = createUser("subscription@test.com", "subscriber");
        TopicEntity topic = createTopic("Java", "Java topic");
        String token = createToken(user);

        mockMvc.perform(post("/api/v1/subscription/{topicId}/", topic.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicId").value(topic.getId()))
                .andExpect(jsonPath("$.subscribed").value(true));

        assertThat(subscriptionRepository.existsByUserIdAndTopicId(user.getId(), topic.getId())).isTrue();
    }

    @Test
    void subscribe_withExistingSubscription_shouldReturnConflict() throws Exception {
        UserEntity user = createUser("conflict@test.com", "conflict");
        TopicEntity topic = createTopic("Spring", "Spring topic");
        createSubscription(user, topic);
        String token = createToken(user);

        mockMvc.perform(post("/api/v1/subscription/{topicId}/", topic.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Subscription already exists"));
    }

    @Test
    void unsubscribe_withValidJwt_shouldDeleteSubscription() throws Exception {
        UserEntity user = createUser("unsubscribe@test.com", "unsubscribed");
        TopicEntity topic = createTopic("H2", "H2 topic");
        createSubscription(user, topic);
        String token = createToken(user);

        mockMvc.perform(delete("/api/v1/subscription/{topicId}", topic.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topicId").value(topic.getId()))
                .andExpect(jsonPath("$.subscribed").value(false));

        assertThat(subscriptionRepository.existsByUserIdAndTopicId(user.getId(), topic.getId())).isFalse();
    }

    @Test
    void unsubscribe_withMissingSubscription_shouldReturnNotFound() throws Exception {
        UserEntity user = createUser("missing-subscription@test.com", "missing-subscription");
        TopicEntity topic = createTopic("Kotlin", "Kotlin topic");
        String token = createToken(user);

        mockMvc.perform(delete("/api/v1/subscription/{topicId}", topic.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Subscription not found"));
    }
}