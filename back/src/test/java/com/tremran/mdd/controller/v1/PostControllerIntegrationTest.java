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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tremran.mdd.model.TopicEntity;
import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.repository.PostRepository;
import com.tremran.mdd.repository.TopicRepository;
import com.tremran.mdd.repository.UserRepository;
import com.tremran.mdd.service.JwtService;

@SpringBootTest
@ActiveProfiles("test")
class PostControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JwtService jwtService;


    UserEntity createITUser() {
        UserEntity user = new UserEntity();
        user.setEmail("integration@test.com");
        user.setPseudo("integration");
        user.setPassword("password");
        user = userRepository.save(user);
        return user;
    }

    TopicEntity createITTopic() {
        TopicEntity topic = new TopicEntity();
        topic.setName("Integration Topic");
        topic.setDescription("Topic for integration test");
        topic = topicRepository.save(topic);
        return topic;
    }

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        userRepository.deleteAll();
        topicRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    @Test
    void createPost_withValidJwt_shouldReturnCreatedPost() throws Exception {
        UserEntity user = createITUser();
        TopicEntity topic = createITTopic();

        UserDetails userDetails = User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
        String token = jwtService.generateToken(userDetails);

        String body = objectMapper.writeValueAsString(new CreatePostPayload(topic.getId(), "Post Title", "Post content", LocalDate.now().toString()));

        mockMvc.perform(post("/api/v1/post")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Post Title"))
                .andExpect(jsonPath("$.content").value("Post content"))
                .andExpect(jsonPath("$.author.email").value(user.getEmail()))
                .andExpect(jsonPath("$.topicId").value(topic.getId()));
    }

    @Test
    void getPost_withValidJwt_shouldReturnPostDetails() throws Exception {
        UserEntity user = new UserEntity();
        user.setEmail("reader@test.com");
        user.setPseudo("reader");
        user.setPassword("password");
        user = userRepository.save(user);

        TopicEntity topic = new TopicEntity();
        topic.setName("Read Topic");
        topic.setDescription("Topic for read test");
        topic = topicRepository.save(topic);

        com.tremran.mdd.model.PostEntity post = new com.tremran.mdd.model.PostEntity();
        post.setAuthor(user);
        post.setTopic(topic);
        post.setTitle("Read Post");
        post.setContent("Content for read post");
        post.setPublishedAt(LocalDate.now());
        post = postRepository.save(post);

        UserDetails userDetails = User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
        String token = jwtService.generateToken(userDetails);

        mockMvc.perform(get("/api/v1/post/{postId}", post.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(post.getId()))
                .andExpect(jsonPath("$.title").value("Read Post"))
                .andExpect(jsonPath("$.author.email").value(user.getEmail()))
                .andExpect(jsonPath("$.topicId").value(topic.getId()));
    }

    @Test
    void getPost_withMissingPost_shouldReturnNotFoundError() throws Exception {
    UserEntity user = new UserEntity();
    user.setEmail("missing@test.com");
    user.setPseudo("missing");
    user.setPassword("password");
    user = userRepository.save(user);

    UserDetails userDetails = User.withUsername(user.getEmail())
        .password(user.getPassword())
        .roles("USER")
        .build();
    String token = jwtService.generateToken(userDetails);

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
