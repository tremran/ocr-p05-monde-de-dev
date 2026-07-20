package com.tremran.mdd.controller.v1;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tremran.mdd.model.PostEntity;
import com.tremran.mdd.model.TopicEntity;
import com.tremran.mdd.model.UserEntity;

@SpringBootTest
@ActiveProfiles("test")
class CommentControllerIntegrationTest extends ControllerIntegrationTestSupport {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
    void getComments_withValidJwt_shouldReturnCommentsForPost() throws Exception {
        UserEntity author = createUser("reader@test.com", "reader");
        TopicEntity topic = createTopic("Read Topic", "Topic for read test");
        PostEntity post = createPost(author, topic, "Read Post", "Content for read post", LocalDate.now());

        UserEntity commenter = createUser("commenter@test.com", "commenter");
        createComment(post, commenter, "First comment");

        String token = createToken(author);

        mockMvc.perform(get("/api/v1/post/{postId}/comment", post.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("First comment"))
                .andExpect(jsonPath("$[0].author.email").value("commenter@test.com"))
                .andExpect(jsonPath("$[0].author.pseudo").value("commenter"));
    }

    @Test
    void addComment_withValidJwt_shouldCreateComment() throws Exception {
        UserEntity author = createUser("poster@test.com", "poster");
        TopicEntity topic = createTopic("Topic", "Description");
        PostEntity post = createPost(author, topic, "Post", "Body", LocalDate.now());

        UserEntity commenter = createUser("newcomment@test.com", "newcomment");
        String token = createToken(commenter);
        String body = objectMapper.writeValueAsString(new AddCommentPayload("Nice article"));

        mockMvc.perform(post("/api/v1/post/{postId}/comment", post.getId())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Nice article"))
                .andExpect(jsonPath("$.author.email").value("newcomment@test.com"))
                .andExpect(jsonPath("$.author.pseudo").value("newcomment"));
    }

    @Test
    void getComments_withMissingPost_shouldReturnNotFound() throws Exception {
        UserEntity user = createUser("missing@test.com", "missing");
        String token = createToken(user);

        mockMvc.perform(get("/api/v1/post/{postId}/comment", 999L)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Post not found"));
    }

    private record AddCommentPayload(String content) {
    }
}