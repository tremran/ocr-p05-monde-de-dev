package com.tremran.mdd.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tremran.mdd.model.PostEntity;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    @Query("""
            select p from PostEntity p
            where p.topic in (
                select s.topic from SubscriptionEntity s
                where s.user.email = ?1
            )
            order by p.publishedAt desc, p.createdAt desc
            """)
    Iterable<PostEntity> findFeedForUser(String email);
}
