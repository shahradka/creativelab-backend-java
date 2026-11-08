package top.creativelab.user.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for the user module.
 *
 * <p>Exposes the generated specification at {@code /v3/api-docs} and the interactive
 * Swagger UI at {@code /swagger-ui.html}.</p>
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI userModuleOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CreativeLab User API")
                        .description("REST API for managing users and their role assignments.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CreativeLab")
                                .email("support@creativelab.top"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Local development server")))
                .components(new Components()
                        .addSecuritySchemes("X-User-Id", new SecurityScheme()
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .name("X-User-Id")
                                .description("Username of the authenticated user, required by password change.")));
    }
}
