package com.tremran.mdd.controller.v1;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.tremran.mdd.model.TopicEntity;
import com.tremran.mdd.model.UserEntity;

@SpringBootTest
@ActiveProfiles("test")
class FeedControllerIntegrationTest extends ControllerIntegrationTestSupport {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        subscriptionRepository.deleteAll();
        commentRepository.deleteAll();
        userRepository.deleteAll();
        topicRepository.deleteAll();
        mockMvc = createMockMvc();
    }

    @Test
    void getFeed_withoutSort_shouldReturnPublishedAtDescByDefault() throws Exception {
        UserEntity user = createUser("feed@test.com", "feed");
        TopicEntity topic = createTopic("Feed Topic", "Feed description");
        createSubscription(user, topic);

        createPost(user, topic, "Ancien", "Premier", LocalDate.of(2026, 7, 10));
        createPost(user, topic, "Recent", "Deuxieme", LocalDate.of(2026, 7, 20));

        String token = createToken(user);

        mockMvc.perform(get("/api/v1/feed")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Recent"))
                .andExpect(jsonPath("$[1].title").value("Ancien"));
    }

    @Test
    void getFeed_withSortAsc_shouldReturnPublishedAtAsc() throws Exception {
        UserEntity user = createUser("feed-asc@test.com", "feed-asc");
        TopicEntity topic = createTopic("Feed Topic", "Feed description");
        createSubscription(user, topic);

        createPost(user, topic, "Ancien", "Premier", LocalDate.of(2026, 7, 10));
        createPost(user, topic, "Recent", "Deuxieme", LocalDate.of(2026, 7, 20));

        String token = createToken(user);

        mockMvc.perform(get("/api/v1/feed")
                .param("sort", "ASC")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Ancien"))
                .andExpect(jsonPath("$[1].title").value("Recent"));
    }
}
