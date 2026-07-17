package com.tremran.mdd.controller.v1;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class TopicControllerIntegrationTest extends ControllerIntegrationTestSupport {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        subscriptionRepository.deleteAll();
        userRepository.deleteAll();
        topicRepository.deleteAll();
        mockMvc = createMockMvc();
    }

    @Test
    void getTopics_withValidJwt_shouldReturnTopicsWithRegisteredFlag() throws Exception {
        UserEntity user = createUser("topics@test.com", "topics-user");
        TopicEntity registeredTopic = createTopic("Spring", "Spring topic");
        TopicEntity freeTopic = createTopic("Docker", "Docker topic");
        createSubscription(user, registeredTopic);
        String token = createToken(user);

        mockMvc.perform(get("/api/v1/topic")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[?(@.id == %s && @.registered == true)]", registeredTopic.getId()).value(hasSize(1)))
            .andExpect(jsonPath("$[?(@.id == %s && @.registered == false)]", freeTopic.getId()).value(hasSize(1)));
    }
}