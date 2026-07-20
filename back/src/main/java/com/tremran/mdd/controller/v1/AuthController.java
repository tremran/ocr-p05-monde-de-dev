package com.tremran.mdd.controller.v1;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
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
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Endpoints d'authentification")
public class AuthController {

    private static final String PASSWORD_RULES =
            "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[=+_\\-$#!?]).{9,}$";

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    @Operation(
        summary = "Créer un compte utilisateur",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RegisterRequest.class),
                examples = @ExampleObject(
                    name = "registerRequest",
                    value = """
                        {
                            "email": "john.doe@example.com",
                            "pseudo": "johndoe",
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
            description = "Compte créé avec succès",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "registerSuccess",
                    value = """
                        {
                            "token": "eyJhbGciOiJIUzI1NiJ9...",
                            "user": {
                        "email": "john.doe@example.com",
                        "pseudo": "johndoe"
                            }
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Données invalides",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "registerValidationError",
                    value = """
                        {
                            "error": "Bad Request",
                            "message": "password must be longer than 8 characters and include at least one digit, one lowercase letter, one uppercase letter and one special character (=+_-$#!?)"
                        }
                        """
                )
            )
        ),
        @ApiResponse(responseCode = "409", description = "Email ou pseudo déjà existant")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        UserEntity entity = userService.register(request.email(), request.pseudo(), request.password());
        String token = jwtService.generateToken(org.springframework.security.core.userdetails.User.withUsername(entity.getEmail())
                .password(entity.getPassword())
                .roles("USER")
                .build());
        return ResponseEntity.ok(Map.of("token", token, "user", Map.of("email", entity.getEmail(), "pseudo", entity.getPseudo())));
    }

    @PostMapping("/login")
    @Operation(
        summary = "Se connecter et obtenir un JWT",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequest.class),
                examples = @ExampleObject(
                    name = "loginRequest",
                    value = """
                        {
                            "email": "john.doe@example.com",
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
            description = "Connexion réussie",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "loginSuccess",
                    value = """
                        {
                            "token": "eyJhbGciOiJIUzI1NiJ9..."
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Identifiants invalides",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "loginUnauthorized",
                    value = """
                        {
                            "error": "Unauthorized",
                            "message": "Invalid credentials"
                        }
                        """
                )
            )
        )
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        String token = jwtService.generateToken((org.springframework.security.core.userdetails.UserDetails) authentication.getPrincipal());
        return ResponseEntity.ok(Map.of("token", token));
    }

    public record RegisterRequest(
        @Schema(description = "Adresse email", example = "john.doe@example.com")
        @NotBlank @Email String email,
        @Schema(description = "Pseudo utilisateur", example = "johndoe")
        @NotBlank @Size(min = 3, max = 100) String pseudo,
        @Schema(description = "Mot de passe fort", example = "StrongPass123!")
        @NotBlank
        @Pattern(regexp = PASSWORD_RULES,
            message = "must be longer than 8 characters and include at least one digit, one lowercase letter, one uppercase letter and one special character (=+_-$#!?)")
        String password) {
    }

    public record LoginRequest(
        @Schema(description = "Adresse email", example = "john.doe@example.com")
        @NotBlank @Email String email,
        @Schema(description = "Mot de passe", example = "StrongPass123!")
        @NotBlank String password) {
    }
}
