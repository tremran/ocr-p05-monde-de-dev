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

/**
 * Gère l'inscription, la lecture et la mise à jour des utilisateurs.
 */
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inscrit un nouvel utilisateur après contrôle d'unicité de l'email et du pseudo.
     *
     * @param email adresse email du nouvel utilisateur
     * @param pseudo pseudo public du nouvel utilisateur
     * @param password mot de passe en clair à encoder avant persistance
     * @return utilisateur créé et persisté
     */
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

    /**
     * Charge l'utilisateur courant à partir de son email.
     *
     * @param email email de l'utilisateur recherché
     * @return utilisateur correspondant à l'email fourni
     */
    public UserEntity getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    /**
     * Met à jour le profil de l'utilisateur courant en conservant les contraintes d'unicité.
     *
     * @param currentEmail email actuellement authentifié
     * @param email nouvel email demandé
     * @param pseudo nouveau pseudo demandé
     * @param password nouveau mot de passe éventuel, vide ou null pour ne pas le changer
     * @return utilisateur mis à jour et persisté
     */
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

    /**
     * Adapte un utilisateur métier en UserDetails pour Spring Security.
     *
     * @param username email utilisé comme identifiant de connexion
     * @return représentation Spring Security de l'utilisateur
     * @throws UsernameNotFoundException si aucun utilisateur ne correspond à cet email
     */
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
