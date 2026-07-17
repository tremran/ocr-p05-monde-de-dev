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
import com.tremran.mdd.service.UserService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/v1")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
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
        UserEntity user = userService.updateCurrentUser(
                userDetails.getUsername(),
                request.email(),
                request.pseudo(),
                request.password());
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
            @NotBlank @Size(min = 3, max = 100) String pseudo,
            @NotBlank @Size(min = 8) String password) {
    }
}