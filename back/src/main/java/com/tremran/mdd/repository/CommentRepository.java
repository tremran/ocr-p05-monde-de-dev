package com.tremran.mdd.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tremran.mdd.model.CommentEntity;

/**
 * Accès aux commentaires d'un post.
 */
public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    List<CommentEntity> findByPostIdOrderByCreatedAtAsc(Long postId);
}