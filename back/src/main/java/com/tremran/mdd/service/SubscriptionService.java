package com.tremran.mdd.service;

import org.springframework.stereotype.Service;

import com.tremran.mdd.exception.ConflictException;
import com.tremran.mdd.exception.ResourceNotFoundException;
import com.tremran.mdd.model.SubscriptionEntity;
import com.tremran.mdd.model.TopicEntity;
import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.repository.SubscriptionRepository;
import com.tremran.mdd.repository.TopicRepository;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final TopicRepository topicRepository;
    private final UserService userService;

    public SubscriptionService(
            SubscriptionRepository subscriptionRepository,
            TopicRepository topicRepository,
            UserService userService) {
        this.subscriptionRepository = subscriptionRepository;
        this.topicRepository = topicRepository;
        this.userService = userService;
    }

    public SubscriptionEntity subscribe(String userEmail, Long topicId) {
        UserEntity user = userService.getCurrentUser(userEmail);
        TopicEntity topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        if (subscriptionRepository.existsByUserIdAndTopicId(user.getId(), topicId)) {
            throw new ConflictException("Subscription already exists");
        }

        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setUser(user);
        subscription.setTopic(topic);
        return subscriptionRepository.save(subscription);
    }

    public void unsubscribe(String userEmail, Long topicId) {
        UserEntity user = userService.getCurrentUser(userEmail);
        topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        SubscriptionEntity subscription = subscriptionRepository.findByUserIdAndTopicId(user.getId(), topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found"));

        subscriptionRepository.delete(subscription);
    }
}