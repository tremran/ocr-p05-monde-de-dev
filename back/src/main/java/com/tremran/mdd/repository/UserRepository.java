package com.tremran.mdd.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tremran.mdd.model.UserEntity;

/**
 * Accès aux utilisateurs persistés.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByPseudo(String pseudo);
}
