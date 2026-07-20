package com.tremran.mdd.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tremran.mdd.model.SubscriptionEntity;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
    boolean existsByUserIdAndTopicId(Long userId, Long topicId);

    Optional<SubscriptionEntity> findByUserIdAndTopicId(Long userId, Long topicId);

    @Query("select s.topic.id from SubscriptionEntity s where s.user.id = ?1")
    List<Long> findTopicIdsByUserId(Long userId);
}