package com.tremran.mdd.controller.v1;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.tremran.mdd.model.CommentEntity;
import com.tremran.mdd.model.PostEntity;
import com.tremran.mdd.model.SubscriptionEntity;
import com.tremran.mdd.model.TopicEntity;
import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.repository.CommentRepository;
import com.tremran.mdd.repository.PostRepository;
import com.tremran.mdd.repository.SubscriptionRepository;
import com.tremran.mdd.repository.TopicRepository;
import com.tremran.mdd.repository.UserRepository;
import com.tremran.mdd.service.JwtService;

abstract class ControllerIntegrationTestSupport {

    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TopicRepository topicRepository;

    @Autowired
    protected SubscriptionRepository subscriptionRepository;

    @Autowired
    protected CommentRepository commentRepository;

    @Autowired
    protected PostRepository postRepository;

    @Autowired
    protected JwtService jwtService;

    protected MockMvc createMockMvc() {
        return MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    protected UserEntity createUser(String email, String pseudo) {
        return createUser(email, pseudo, "password");
    }

    protected UserEntity createUser(String email, String pseudo, String password) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPseudo(pseudo);
        user.setPassword(password);
        return userRepository.save(user);
    }

    protected TopicEntity createTopic(String name, String description) {
        TopicEntity topic = new TopicEntity();
        topic.setName(name);
        topic.setDescription(description);
        return topicRepository.save(topic);
    }

    protected SubscriptionEntity createSubscription(UserEntity user, TopicEntity topic) {
        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setUser(user);
        subscription.setTopic(topic);
        return subscriptionRepository.save(subscription);
    }

    protected PostEntity createPost(UserEntity author, TopicEntity topic, String title, String content, LocalDate publishedAt) {
        PostEntity post = new PostEntity();
        post.setAuthor(author);
        post.setTopic(topic);
        post.setTitle(title);
        post.setContent(content);
        post.setPublishedAt(publishedAt);
        return postRepository.save(post);
    }

    protected CommentEntity createComment(PostEntity post, UserEntity user, String content) {
        CommentEntity comment = new CommentEntity();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    protected String createToken(UserEntity user) {
        UserDetails userDetails = User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
        return jwtService.generateToken(userDetails);
    }
}