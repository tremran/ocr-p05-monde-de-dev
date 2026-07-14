package com.tremran.mdd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tremran.mdd.model.TopicEntity;

public interface TopicRepository extends JpaRepository<TopicEntity, Long> {
}
