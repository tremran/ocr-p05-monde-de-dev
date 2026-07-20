package com.tremran.mdd.controller.v1;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tremran.mdd.model.PostEntity;
import com.tremran.mdd.service.PostService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/v1/post")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<?> createPost(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreatePostRequest request) {
        PostEntity post = postService.createPost(
                userDetails.getUsername(),
                request.topicId(),
                request.title(),
                request.content(),
                request.publishedAt());

        Map<String, Object> authorMap = new HashMap<>();
        authorMap.put("email", post.getAuthor().getEmail());
        authorMap.put("pseudo", post.getAuthor().getPseudo());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", post.getId());
        responseBody.put("title", post.getTitle());
        responseBody.put("content", post.getContent());
        responseBody.put("author", authorMap);
        responseBody.put("publishedAt", post.getPublishedAt());
        responseBody.put("createdAt", post.getCreatedAt());
        responseBody.put("updatedAt", post.getUpdatedAt());

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<?> getPost(@PathVariable Long postId) {
        PostEntity post = postService.getPostById(postId);

        Map<String, Object> authorMap = new HashMap<>();
        authorMap.put("email", post.getAuthor().getEmail());
        authorMap.put("pseudo", post.getAuthor().getPseudo());

        Map<String, Object> topicMap = new HashMap<>();
        topicMap.put("id", post.getTopic().getId());
        topicMap.put("name", post.getTopic().getName());
        topicMap.put("description", post.getTopic().getDescription());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", post.getId());
        responseBody.put("title", post.getTitle());
        responseBody.put("content", post.getContent());
        responseBody.put("author", authorMap);
        responseBody.put("topic", topicMap);
        responseBody.put("publishedAt", post.getPublishedAt());
        responseBody.put("createdAt", post.getCreatedAt());
        responseBody.put("updatedAt", post.getUpdatedAt());

        return ResponseEntity.ok(responseBody);
    }

    public record CreatePostRequest(
            @NotNull Long topicId,
            @NotBlank @Size(min = 3, max = 255) String title,
            @NotBlank String content,
            @NotBlank String publishedAt) {
    }
}
