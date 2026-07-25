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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Gère les abonnements et désabonnements aux thèmes.
 */
@RestController
@RequestMapping("/api/v1/subscription")
@Tag(name = "Subscription", description = "Abonnements aux thèmes")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    /**
     * Abonne l'utilisateur authentifié au thème demandé.
     *
     * @param userDetails principal Spring Security de l'utilisateur courant
     * @param topicId identifiant du thème à suivre
     * @return réponse confirmant l'abonnement créé
     */
    @PostMapping({"/{topicId}", "/{topicId}/"})
    @Operation(summary = "S'abonner à un thème")
    public ResponseEntity<?> subscribe(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long topicId) {
        subscriptionService.subscribe(userDetails.getUsername(), topicId);
        return ResponseEntity.ok(Map.of(
                "topicId", topicId,
                "subscribed", true));
    }

    /**
     * Désabonne l'utilisateur authentifié du thème demandé.
     *
     * @param userDetails principal Spring Security de l'utilisateur courant
     * @param topicId identifiant du thème à quitter
     * @return réponse confirmant la suppression de l'abonnement
     */
    @DeleteMapping({"/{topicId}", "/{topicId}/"})
    @Operation(summary = "Se désabonner d'un thème")
    public ResponseEntity<?> unsubscribe(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long topicId) {
        subscriptionService.unsubscribe(userDetails.getUsername(), topicId);
        return ResponseEntity.ok(Map.of(
                "topicId", topicId,
                "subscribed", false));
    }
}