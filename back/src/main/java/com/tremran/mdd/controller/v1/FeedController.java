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

@RestController
@RequestMapping("/api/v1")
public class FeedController {

    private final PostService postService;

    public FeedController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/feed")
    public ResponseEntity<?> getFeed(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "DESC") String sort) {
        Iterable<PostEntity> feed = postService.findFeedForUser(userDetails.getUsername(), sort);
        return ResponseEntity.ok(feed);
    }
}
