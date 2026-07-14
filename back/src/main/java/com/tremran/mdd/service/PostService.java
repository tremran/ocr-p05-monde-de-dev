package com.tremran.mdd.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tremran.mdd.model.PostEntity;
import com.tremran.mdd.model.TopicEntity;
import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.repository.PostRepository;
import com.tremran.mdd.repository.TopicRepository;
import com.tremran.mdd.repository.UserRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, TopicRepository topicRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
    }

    public PostEntity createPost(String authorEmail, Long topicId, String title, String content, String publishedAt) {
        UserEntity author = userRepository.findByEmail(authorEmail)
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        TopicEntity topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));

        PostEntity post = new PostEntity();
        post.setAuthor(author);
        post.setTopic(topic);
        post.setTitle(title);
        post.setContent(content);
        post.setPublishedAt(java.time.LocalDate.parse(publishedAt));
        return postRepository.save(post);
    }

    public Optional<PostEntity> findPostById(Long postId) {
        return postRepository.findById(postId);
    }
}
