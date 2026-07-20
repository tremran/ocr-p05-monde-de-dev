package com.tremran.mdd.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tremran.mdd.model.CommentEntity;
import com.tremran.mdd.model.PostEntity;
import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.repository.CommentRepository;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, PostService postService, UserService userService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.userService = userService;
    }

    public List<CommentEntity> findCommentsByPostId(Long postId) {
        postService.getPostById(postId);
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    public CommentEntity addComment(String userEmail, Long postId, String content) {
        UserEntity user = userService.getCurrentUser(userEmail);
        PostEntity post = postService.getPostById(postId);

        CommentEntity comment = new CommentEntity();
        comment.setUser(user);
        comment.setPost(post);
        comment.setContent(content);
        return commentRepository.save(comment);
    }
}