package com.tremran.mdd.controller.v1;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tremran.mdd.service.TopicService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Expose la liste des thèmes disponibles.
 */
@RestController
@RequestMapping("/api/v1/topic")
@Tag(name = "Topic", description = "Consultation des thèmes")
@SecurityRequirement(name = "bearerAuth")
public class TopicController {

    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    /**
     * Liste les thèmes disponibles avec l'état d'abonnement de l'utilisateur courant.
     *
     * @param userDetails principal Spring Security de l'utilisateur courant
     * @return réponse contenant les thèmes enrichis avec l'état registered
     */
    @GetMapping
    @Operation(summary = "Lister les thèmes")
    public ResponseEntity<?> getTopics(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(topicService.findAllTopicsForUser(userDetails.getUsername()));
    }
}