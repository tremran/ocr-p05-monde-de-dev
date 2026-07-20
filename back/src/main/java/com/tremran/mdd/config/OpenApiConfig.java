package com.tremran.mdd.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info(
                title = "MDD API",
                version = "v1",
                description = "API REST du projet Monde de Dev.",
                contact = @Contact(name = "MDD Team"),
                license = @License(name = "Internal Use")),
        tags = {
                @Tag(name = "Auth", description = "Endpoints d'authentification"),
                @Tag(name = "User", description = "Gestion du profil utilisateur"),
                @Tag(name = "Topic", description = "Consultation des thèmes"),
                @Tag(name = "Subscription", description = "Abonnements aux thèmes"),
                @Tag(name = "Feed", description = "Fil d'actualité"),
                @Tag(name = "Post", description = "Gestion des articles"),
                @Tag(name = "Comment", description = "Gestion des commentaires")
        })
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
}
