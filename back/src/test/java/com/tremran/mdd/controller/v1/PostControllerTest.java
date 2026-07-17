package com.tremran.mdd.controller.v1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import com.tremran.mdd.exception.ResourceNotFoundException;
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

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("id")).isEqualTo(1L);
        assertThat(body.get("title")).isEqualTo("Hello World");
        assertThat(body.get("content")).isEqualTo("This is a test post.");
        Map<?, ?> authorMap = (Map<?, ?>) body.get("author");
        assertThat(authorMap.get("email")).isEqualTo("tester@example.com");
        assertThat(body.get("topicId")).isEqualTo(2L);
        assertThat(body.get("publishedAt")).isEqualTo(LocalDate.of(2026, 7, 14));
    }

    @Test
    void getPostReturnsNotFoundWhenPostMissing() {
        when(postService.getPostById(999L)).thenThrow(new ResourceNotFoundException("Post not found"));

        assertThatThrownBy(() -> postController.getPost(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Post not found");
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

        when(postService.getPostById(3L)).thenReturn(post);

        ResponseEntity<?> response = postController.getPost(3L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isInstanceOf(Map.class);

        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertThat(body.get("id")).isEqualTo(3L);
        assertThat(body.get("title")).isEqualTo("Found Post");
        assertThat(body.get("content")).isEqualTo("Found post content.");
        Map<?, ?> authorMap = (Map<?, ?>) body.get("author");
        assertThat(authorMap.get("pseudo")).isEqualTo("author");
        assertThat(body.get("topicId")).isEqualTo(5L);
    }

}
