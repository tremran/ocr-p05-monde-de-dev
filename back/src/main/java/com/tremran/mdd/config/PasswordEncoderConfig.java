package com.tremran.mdd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Fournit l'encodeur de mots de passe utilisé par la sécurité Spring.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Fournit l'encodeur BCrypt utilisé pour stocker les mots de passe.
     *
     * @return encodeur de mots de passe compatible avec Spring Security
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
