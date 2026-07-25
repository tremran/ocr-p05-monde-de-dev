package com.tremran.mdd.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.repository.SubscriptionRepository;
import com.tremran.mdd.repository.TopicRepository;

/**
 * Prépare les thèmes avec l'état d'abonnement de l'utilisateur courant.
 */
@Service
public class TopicService {

    private final TopicRepository topicRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    public TopicService(
            TopicRepository topicRepository,
            SubscriptionRepository subscriptionRepository,
            UserService userService) {
        this.topicRepository = topicRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userService = userService;
    }

    /**
     * Retourne tous les thèmes enrichis avec l'état d'abonnement de l'utilisateur.
     *
     * @param userEmail email de l'utilisateur courant
     * @return liste des thèmes exposés à l'API avec le booléen registered
     */
    public List<TopicResponse> findAllTopicsForUser(String userEmail) {
        UserEntity user = userService.getCurrentUser(userEmail);
        Set<Long> subscribedTopicIds = subscriptionRepository.findTopicIdsByUserId(user.getId()).stream()
                .collect(Collectors.toSet());

        return topicRepository.findAll().stream()
                .map(topic -> new TopicResponse(
                        topic.getId(),
                        topic.getName(),
                        topic.getDescription(),
                        subscribedTopicIds.contains(topic.getId())))
                .toList();
    }

    public record TopicResponse(Long id, String name, String description, boolean registered) {
    }
}