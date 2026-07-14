package com.tremran.mdd.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tremran.mdd.model.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
}
