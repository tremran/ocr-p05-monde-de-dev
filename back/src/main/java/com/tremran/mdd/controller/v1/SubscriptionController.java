package com.tremran.mdd.controller.v1;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tremran.mdd.service.SubscriptionService;

@RestController
@RequestMapping("/api/v1/subscription")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping({"/{topicId}", "/{topicId}/"})
    public ResponseEntity<?> subscribe(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long topicId) {
        subscriptionService.subscribe(userDetails.getUsername(), topicId);
        return ResponseEntity.ok(Map.of(
                "topicId", topicId,
                "subscribed", true));
    }

    @DeleteMapping({"/{topicId}", "/{topicId}/"})
    public ResponseEntity<?> unsubscribe(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long topicId) {
        subscriptionService.unsubscribe(userDetails.getUsername(), topicId);
        return ResponseEntity.ok(Map.of(
                "topicId", topicId,
                "subscribed", false));
    }
}