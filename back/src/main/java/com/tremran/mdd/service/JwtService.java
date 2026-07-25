package com.tremran.mdd.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Service utilitaire pour générer et valider les JWT.
 */
@Service
public class JwtService {

    @Value("${security.jwt.secret-key:3cfa76ef14937c1c0ea519f8fc079a80fcd04a7420f8e8bcd0a7567c272e007b}")
    private String secretKey;

    @Value("${security.jwt.expiration-time:36000000}")
    private long jwtExpiration;

    /**
     * Génère un JWT signé pour l'utilisateur authentifié.
     *
     * @param userDetails identité Spring Security de l'utilisateur
     * @return token JWT contenant le nom d'utilisateur comme sujet
     */
    public String generateToken(UserDetails userDetails) {
        return buildToken(userDetails.getUsername(), jwtExpiration);
    }

    /**
     * Extrait le nom d'utilisateur porté par un JWT.
     *
     * @param token jeton JWT à analyser
     * @return identifiant utilisateur stocké dans le sujet du token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Vérifie qu'un JWT correspond bien à l'utilisateur fourni et qu'il n'est pas expiré.
     *
     * @param token jeton JWT à valider
     * @param userDetails identité attendue de l'utilisateur
     * @return true si le token est encore valide pour cet utilisateur
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private String buildToken(String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        return Jwts.builder()
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
