package com.tremran.mdd.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tremran.mdd.model.SubscriptionEntity;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    boolean existsByUserIdAndTopicId(Long userId, Long topicId);

    Optional<SubscriptionEntity> findByUserIdAndTopicId(Long userId, Long topicId);
}