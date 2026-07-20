package com.tremran.mdd.controller.v1;

import java.util.HashMap;
import java.util.List;
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

import com.tremran.mdd.model.CommentEntity;
import com.tremran.mdd.service.CommentService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/v1/post/{postId}/comment")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        List<CommentEntity> comments = commentService.findCommentsByPostId(postId);
        return ResponseEntity.ok(comments.stream()
                .map(this::toCommentResponse)
                .toList());
    }

    @PostMapping
    public ResponseEntity<?> addComment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long postId,
            @Valid @RequestBody AddCommentRequest request) {
        CommentEntity comment = commentService.addComment(userDetails.getUsername(), postId, request.content());
        return ResponseEntity.ok(toCommentResponse(comment));
    }

    private Map<String, Object> toCommentResponse(CommentEntity comment) {
        Map<String, Object> authorMap = new HashMap<>();
        authorMap.put("email", comment.getUser().getEmail());
        authorMap.put("pseudo", comment.getUser().getPseudo());

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("id", comment.getId());
        responseBody.put("content", comment.getContent());
        responseBody.put("author", authorMap);
        responseBody.put("createdAt", comment.getCreatedAt());
        responseBody.put("updatedAt", comment.getUpdatedAt());
        return responseBody;
    }

    public record AddCommentRequest(@NotBlank String content) {
    }
}