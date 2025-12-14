package ma.lsia.certis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI customOpenAPI() {
    final String securitySchemeName = "bearerAuth";
    
    return new OpenAPI()
      .info(new Info()
        .title("Certis API")
        .version("1.0.0")
        .description("Certificate Management System REST API with JWT Authentication")
        .contact(new Contact()
          .name("Certis Team")
          .email("contact@avycenna.com")
          .url("https://github.com/avycenna/certis"))
        .license(new License()
          .name("Private")
          .url("https://github.com/avycenna/certis")))
      .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
      .components(new Components()
        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
          .name(securitySchemeName)
          .type(SecurityScheme.Type.HTTP)
          .scheme("bearer")
          .bearerFormat("JWT")
          .description("Enter your JWT token received from the /auth/login endpoint")));
  }
}
