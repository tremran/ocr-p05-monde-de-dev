package com.tremran.mdd.service;

import java.util.Objects;
import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tremran.mdd.exception.ConflictException;
import com.tremran.mdd.exception.ResourceNotFoundException;
import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.repository.UserRepository;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity register(String email, String pseudo, String password) {
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByPseudo(pseudo).isPresent()) {
            throw new ConflictException("Email or pseudo already exists");
        }

        UserEntity entity = new UserEntity();
        entity.setEmail(email);
        entity.setPseudo(pseudo);
        entity.setPassword(passwordEncoder.encode(password));
        return userRepository.save(entity);
    }

    public UserEntity getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserEntity updateCurrentUser(String currentEmail, String email, String pseudo, String password) {
        UserEntity user = getCurrentUser(currentEmail);

        userRepository.findByEmail(email)
                .filter(existingUser -> !Objects.equals(existingUser.getId(), user.getId()))
                .ifPresent(existingUser -> {
                    throw new ConflictException("Email already exists");
                });

        userRepository.findByPseudo(pseudo)
                .filter(existingUser -> !Objects.equals(existingUser.getId(), user.getId()))
                .ifPresent(existingUser -> {
                    throw new ConflictException("Pseudo already exists");
                });

        if (password != null && !password.isBlank() && password.length() < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        }

        user.setEmail(email);
        user.setPseudo(pseudo);
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userOpt = userRepository.findByEmail(username);
        if (userOpt.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        UserEntity user = userOpt.get();
        return User.withUsername(user.getEmail())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
