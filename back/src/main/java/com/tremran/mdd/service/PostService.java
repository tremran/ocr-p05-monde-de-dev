package com.tremran.mdd.service;

import org.springframework.stereotype.Service;

import com.tremran.mdd.exception.ResourceNotFoundException;
import com.tremran.mdd.model.PostEntity;
import com.tremran.mdd.model.TopicEntity;
import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.repository.PostRepository;
import com.tremran.mdd.repository.TopicRepository;
import com.tremran.mdd.repository.UserRepository;

/**
 * Gère la création des posts et la récupération du fil d'actualité.
 */
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

    /**
     * Crée un post pour un auteur et un thème existants.
     *
     * @param authorEmail email de l'auteur authentifié
     * @param topicId identifiant du thème associé au post
     * @param title titre du post
     * @param content contenu du post
     * @param publishedAt date de publication au format ISO local date
     * @return post persisté avec ses relations résolues
     */
    public PostEntity createPost(String authorEmail, Long topicId, String title, String content, String publishedAt) {
        UserEntity author = userRepository.findByEmail(authorEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Authenticated user not found"));

        TopicEntity topic = topicRepository.findById(topicId)
            .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        PostEntity post = new PostEntity();
        post.setAuthor(author);
        post.setTopic(topic);
        post.setTitle(title);
        post.setContent(content);
        post.setPublishedAt(java.time.LocalDate.parse(publishedAt));
        return postRepository.save(post);
    }

    /**
     * Retourne un post par son identifiant ou lève une erreur métier.
     *
     * @param postId identifiant du post recherché
     * @return post correspondant à l'identifiant fourni
     */
    public PostEntity getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }
    
    /**
     * Charge le fil d'actualité de l'utilisateur selon l'ordre demandé.
     *
     * @param email email de l'utilisateur dont on veut le fil
     * @param sort ordre demandé, ascendant ou descendant
     * @return ensemble ordonné des posts visibles dans le fil
     */
    public Iterable<PostEntity> findFeedForUser(String email, String sort) {
        if ("ASC".equalsIgnoreCase(sort)) {
            return postRepository.findFeedForUserOrderByPublishedAtAsc(email);
        }

        return postRepository.findFeedForUserOrderByPublishedAtDesc(email);
    }
}
