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

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/v1/post")
@Tag(name = "Post", description = "Gestion des articles")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    @Operation(
        summary = "Créer un article",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreatePostRequest.class),
                examples = @ExampleObject(
                    name = "createPostRequest",
                    value = """
                        {
                            "topicId": 1,
                            "title": "Mon premier article",
                            "content": "Contenu de l'article",
                            "publishedAt": "2026-07-21"
                        }
                        """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Article créé",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "createPostSuccess",
                    value = """
                        {
                            "id": 42,
                            "title": "Mon premier article",
                            "content": "Contenu de l'article",
                            "author": {
                        "email": "john.doe@example.com",
                        "pseudo": "johndoe"
                            },
                            "publishedAt": "2026-07-21",
                            "createdAt": "2026-07-21T10:15:30",
                            "updatedAt": "2026-07-21T10:15:30"
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Payload invalide"),
        @ApiResponse(responseCode = "401", description = "JWT requis")
    })
    @SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "Récupérer le détail d'un article")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Détail article",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "getPostSuccess",
                    value = """
                        {
                            "id": 42,
                            "title": "Mon premier article",
                            "content": "Contenu de l'article",
                            "author": {
                                "email": "john.doe@example.com",
                                "pseudo": "johndoe"
                            },
                            "topic": {
                                "id": 1,
                                "name": "Java",
                                "description": "Discussions Java"
                            },
                            "publishedAt": "2026-07-21",
                            "createdAt": "2026-07-21T10:15:30",
                            "updatedAt": "2026-07-21T10:15:30"
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "JWT requis"),
        @ApiResponse(responseCode = "404", description = "Article introuvable")
    })
    @SecurityRequirement(name = "bearerAuth")
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
        @Schema(description = "Identifiant du thème", example = "1")
        @NotNull Long topicId,
        @Schema(description = "Titre de l'article", example = "Mon premier article")
        @NotBlank @Size(min = 3, max = 255) String title,
        @Schema(description = "Contenu de l'article", example = "Contenu de l'article")
        @NotBlank String content,
        @Schema(description = "Date de publication au format ISO", example = "2026-07-21")
        @NotBlank String publishedAt) {
    }
}
