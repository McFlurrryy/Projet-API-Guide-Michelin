package M2fst.demo.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    /**
     * Méthode permettant de définir le Groupe (les controllers) à documenter pour le swagger.
     */
    @Bean
    public GroupedOpenApi api() {
        return org.springdoc.core.models.GroupedOpenApi.builder()
                .group("M2fst")
                .pathsToMatch("/**")
                .packagesToScan("M2fst.demo.controller")
                .build();
    }

    /**
     * Méthode permettant de configurer la documentation et de permettre d'activer le bouton "Authorize" pour donner un JWT afin de tester les routes en étant connecté.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Guide Michelin").description("Documentation API Guide Michelin"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
