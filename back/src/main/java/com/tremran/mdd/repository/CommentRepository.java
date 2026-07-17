package com.tremran.mdd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tremran.mdd.model.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPostIdOrderByCreatedAtAsc(Long postId);
}