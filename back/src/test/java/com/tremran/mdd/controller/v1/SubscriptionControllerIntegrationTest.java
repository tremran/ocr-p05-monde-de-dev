package com.tremran.mdd.controller.v1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tremran.mdd.model.SubscriptionEntity;
import com.tremran.mdd.model.TopicEntity;
import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.repository.SubscriptionRepository;
import com.tremran.mdd.repository.TopicRepository;
import com.tremran.mdd.repository.UserRepository;
import com.tremran.mdd.service.JwtService;

@SpringBootTest
@ActiveProfiles("test")
class SubscriptionControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private JwtService jwtService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        subscriptionRepository.deleteAll();
        userRepository.deleteAll();
        topicRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
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

    private UserEntity createUser(String email, String pseudo) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPseudo(pseudo);
        user.setPassword("password");
        return userRepository.save(user);
    }

    private TopicEntity createTopic(String name, String description) {
        TopicEntity topic = new TopicEntity();
        topic.setName(name);
        topic.setDescription(description);
        return topicRepository.save(topic);
    }

    private SubscriptionEntity createSubscription(UserEntity user, TopicEntity topic) {
        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setUser(user);
        subscription.setTopic(topic);
        return subscriptionRepository.save(subscription);
    }

    private String createToken(UserEntity user) {
        UserDetails userDetails = User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
        return jwtService.generateToken(userDetails);
    }
}