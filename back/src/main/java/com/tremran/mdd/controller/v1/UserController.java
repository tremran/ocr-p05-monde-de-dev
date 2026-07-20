package com.tremran.mdd.controller.v1;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tremran.mdd.model.UserEntity;
import com.tremran.mdd.service.JwtService;
import com.tremran.mdd.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private static final String OPTIONAL_PASSWORD_RULES =
            "^$|(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[=+_\\-$#!?]).{9,}$";

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(toUserResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateMeRequest request) {
        boolean emailChanged = !userDetails.getUsername().equals(request.email());

        UserEntity user = userService.updateCurrentUser(
                userDetails.getUsername(),
                request.email(),
                request.pseudo(),
                request.password());

        if (emailChanged) {
            return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "pseudo", user.getPseudo(),
                "token", jwtService.generateToken(org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles("USER")
                    .build())));
        }

        return ResponseEntity.ok(toUserResponse(user));
    }

    private Map<String, Object> toUserResponse(UserEntity user) {
        return Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "pseudo", user.getPseudo());
    }

    public record UpdateMeRequest(
            @NotBlank @Email String email,
            @NotBlank String pseudo,
            @Pattern(regexp = OPTIONAL_PASSWORD_RULES,
                message = "must be longer than 8 characters and include at least one digit, one lowercase letter, one uppercase letter and one special character (=+_-$#!?)")
            String password) {
    }
}