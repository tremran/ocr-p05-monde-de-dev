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

@RestController
@RequestMapping("/api/v1/post/{postId}/comment")
@Tag(name = "Comment", description = "Gestion des commentaires")
@SecurityRequirement(name = "bearerAuth")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping
    @Operation(summary = "Lister les commentaires d'un article")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Commentaires récupérés",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "getCommentsSuccess",
                    value = """
                    [
                        {
                            "id": 7,
                            "content": "Super article !",
                            "author": {
                                "email": "john.doe@example.com",
                                "pseudo": "johndoe"
                            },
                            "createdAt": "2026-07-21T10:20:00",
                            "updatedAt": "2026-07-21T10:20:00"
                        }
                    ]
                    """
                )
            )
        ),
        @ApiResponse(responseCode = "401", description = "JWT requis")
    })
    public ResponseEntity<?> getComments(@PathVariable Long postId) {
        List<CommentEntity> comments = commentService.findCommentsByPostId(postId);
        return ResponseEntity.ok(comments.stream()
                .map(this::toCommentResponse)
                .toList());
    }

    @PostMapping
    @Operation(
        summary = "Ajouter un commentaire à un article",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
        required = true,
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = AddCommentRequest.class),
            examples = @ExampleObject(
                name = "addCommentRequest",
                value = """
                {
                    "content": "Merci pour ce partage !"
                }
                """)
            )
        )
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Commentaire ajouté",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "addCommentSuccess",
                    value = """
                        {
                            "id": 8,
                            "content": "Merci pour ce partage !",
                            "author": {
                        "email": "john.doe@example.com",
                        "pseudo": "johndoe"
                            },
                            "createdAt": "2026-07-21T10:22:00",
                            "updatedAt": "2026-07-21T10:22:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "400", description = "Payload invalide"),
        @ApiResponse(responseCode = "401", description = "JWT requis")
    })
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

    public record AddCommentRequest(
        @Schema(description = "Contenu du commentaire", example = "Merci pour ce partage !")
        @NotBlank String content) {
    }
}