package com.tremran.mdd.controller.v1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.tremran.mdd.model.PostEntity;
import com.tremran.mdd.model.TopicEntity;
import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.service.PostService;

class PostControllerTest {

    @Mock
    private PostService postService;

    private PostController postController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postController = new PostController(postService);
    }

    @Test
    void createPostReturnsCreatedPostForAuthenticatedUser() {
        UserDetails userDetails = User.withUsername("tester@example.com")
                .password("password")
                .roles("USER")
                .build();

        PostEntity post = new PostEntity();
        post.setId(1L);
        UserEntity author = new UserEntity();
        author.setEmail("tester@example.com");
        author.setPseudo("tester");
        post.setAuthor(author);
        TopicEntity topic = new TopicEntity();
        topic.setId(2L);
        post.setTopic(topic);
        post.setTitle("Hello World");
        post.setContent("This is a test post.");
        post.setPublishedAt(LocalDate.of(2026, 7, 14));

        when(postService.createPost("tester@example.com", 2L, "Hello World", "This is a test post.", "2026-07-14"))
                .thenReturn(post);

        var response = postController.createPost(userDetails, new PostController.CreatePostRequest(2L, "Hello World", "This is a test post.", "2026-07-14"));

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("id", 1L);
        assertThat(body).containsEntry("title", "Hello World");
        assertThat(body).containsEntry("content", "This is a test post.");
        @SuppressWarnings("unchecked")
        Map<String, Object> authorMap = (Map<String, Object>) body.get("author");
        assertThat(authorMap).containsEntry("email", "tester@example.com");
        assertThat(body).containsEntry("topicId", 2L);
        assertThat(body).containsEntry("publishedAt", LocalDate.of(2026, 7, 14));
    }

    @Test
    void getPostReturnsNotFoundWhenPostMissing() {
        when(postService.findPostById(999L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = postController.getPost(999L);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void getPostReturnsPostDetailsWhenFound() {
        PostEntity post = new PostEntity();
        post.setId(3L);
        UserEntity author = new UserEntity();
        author.setEmail("author@example.com");
        author.setPseudo("author");
        post.setAuthor(author);
        TopicEntity topic = new TopicEntity();
        topic.setId(5L);
        post.setTopic(topic);
        post.setTitle("Found Post");
        post.setContent("Found post content.");
        post.setPublishedAt(LocalDate.of(2026, 7, 14));

        when(postService.findPostById(3L)).thenReturn(Optional.of(post));

        ResponseEntity<?> response = postController.getPost(3L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertThat(body).containsEntry("id", 3L);
        assertThat(body).containsEntry("title", "Found Post");
        assertThat(body).containsEntry("content", "Found post content.");
        @SuppressWarnings("unchecked")
        Map<String, Object> authorMap = (Map<String, Object>) body.get("author");
        assertThat(authorMap).containsEntry("pseudo", "author");
        assertThat(body).containsEntry("topicId", 5L);
    }

}
