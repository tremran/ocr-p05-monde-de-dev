package com.tremran.mdd.controller.v1;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tremran.mdd.model.TopicEntity;
import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.repository.PostRepository;

@SpringBootTest
@ActiveProfiles("test")
class PostControllerIntegrationTest extends ControllerIntegrationTestSupport {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        topicRepository.deleteAll();
        mockMvc = createMockMvc();
    }

    @Test
    void createPost_withValidJwt_shouldReturnCreatedPost() throws Exception {
        UserEntity user = createUser("integration@test.com", "integration");
        TopicEntity topic = createTopic("Integration Topic", "Topic for integration test");
        String token = createToken(user);

        String body = objectMapper.writeValueAsString(new CreatePostPayload(topic.getId(), "Post Title", "Post content", LocalDate.now().toString()));

        mockMvc.perform(post("/api/v1/post")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Post Title"))
                .andExpect(jsonPath("$.content").value("Post content"))
                .andExpect(jsonPath("$.author.email").value(user.getEmail()));
    }

    @Test
    void getPost_withValidJwt_shouldReturnPostDetails() throws Exception {
        UserEntity user = createUser("reader@test.com", "reader");
        TopicEntity topic = createTopic("Read Topic", "Topic for read test");

        com.tremran.mdd.model.PostEntity post = new com.tremran.mdd.model.PostEntity();
        post.setAuthor(user);
        post.setTopic(topic);
        post.setTitle("Read Post");
        post.setContent("Content for read post");
        post.setPublishedAt(LocalDate.now());
        post = postRepository.save(post);

        String token = createToken(user);

        mockMvc.perform(get("/api/v1/post/{postId}", post.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("Read Post"))
                .andExpect(jsonPath("$.author.email").value(user.getEmail()))
            .andExpect(jsonPath("$.topic.id").value(topic.getId()))
            .andExpect(jsonPath("$.topic.name").value(topic.getName()))
            .andExpect(jsonPath("$.topic.description").value(topic.getDescription()));
    }

    @Test
    void getPost_withMissingPost_shouldReturnNotFoundError() throws Exception {
        UserEntity user = createUser("missing@test.com", "missing");
        String token = createToken(user);

        mockMvc.perform(get("/api/v1/post/{postId}", 999L)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Post not found"));
    }

    @Test
    void requestWithoutAuthorizationHeader_shouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/post/1"))
                .andExpect(status().isForbidden());
    }

    private record CreatePostPayload(Long topicId, String title, String content, String publishedAt) {
    }
}
