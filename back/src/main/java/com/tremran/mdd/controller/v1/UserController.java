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

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "User", description = "Gestion du profil utilisateur")
@SecurityRequirement(name = "bearerAuth")
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
    @Operation(summary = "Récupérer le profil de l'utilisateur connecté")
    @ApiResponse(
        responseCode = "200",
        description = "Profil utilisateur",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                name = "getMeSuccess",
                value = """
                    {
                        "id": 1,
                        "email": "john.doe@example.com",
                        "pseudo": "johndoe"
                    }
                    """
            )
        )
    )
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        UserEntity user = userService.getCurrentUser(userDetails.getUsername());
        return ResponseEntity.ok(toUserResponse(user));
    }

    @PutMapping("/me")
    @Operation(
        summary = "Mettre à jour le profil de l'utilisateur connecté",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdateMeRequest.class),
                examples = @ExampleObject(
                    name = "updateMeRequest",
                    value = """
                        {
                            "email": "john.updated@example.com",
                            "pseudo": "johnupdated",
                            "password": "StrongPass123!"
                        }
                        """
                )
            )
        )
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Profil mis à jour",
            content = @Content(
                mediaType = "application/json",
                examples = {
                    @ExampleObject(
                        name = "updateMeWithoutEmailChange",
                        value = """
                            {
                                "id": 1,
                                "email": "john.doe@example.com",
                                "pseudo": "johnupdated"
                            }
                            """),
                    @ExampleObject(
                        name = "updateMeWithEmailChange",
                        value = """
                            {
                                "id": 1,
                                "email": "john.updated@example.com",
                                "pseudo": "johnupdated",
                                "token": "eyJhbGciOiJIUzI1NiJ9..."
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(responseCode = "400", description = "Payload invalide"),
        @ApiResponse(responseCode = "401", description = "JWT requis")
    })
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
        @Schema(description = "Adresse email", example = "john.updated@example.com")
        @NotBlank @Email String email,
        @Schema(description = "Pseudo utilisateur", example = "johnupdated")
        @NotBlank String pseudo,
        @Schema(description = "Mot de passe (laisser vide pour ne pas le modifier)", example = "StrongPass123!")
        @Pattern(regexp = OPTIONAL_PASSWORD_RULES,
            message = "must be longer than 8 characters and include at least one digit, one lowercase letter, one uppercase letter and one special character (=+_-$#!?)")
        String password) {
    }
}