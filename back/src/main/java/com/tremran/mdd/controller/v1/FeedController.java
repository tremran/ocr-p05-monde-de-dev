package com.tremran.mdd.controller.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tremran.mdd.model.PostEntity;
import com.tremran.mdd.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Feed", description = "Fil d'actualité")
@SecurityRequirement(name = "bearerAuth")
public class FeedController {

    private final PostService postService;

    public FeedController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/feed")
        @Operation(summary = "Récupérer le fil d'actualité")
    public ResponseEntity<?> getFeed(
            @AuthenticationPrincipal UserDetails userDetails,
            @Parameter(description = "Ordre de tri par date: DESC (défaut) ou ASC")
            @RequestParam(defaultValue = "DESC") String sort) {
        Iterable<PostEntity> feed = postService.findFeedForUser(userDetails.getUsername(), sort);
        return ResponseEntity.ok(feed);
    }
}
