package org.example.mybooks.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
@Bean
public OpenAPI openAPI(){
    String jwtSchemeName="jwtAuth";
    SecurityRequirement securityRequirement=new SecurityRequirement().addList(jwtSchemeName);

    SecurityScheme securityScheme=new SecurityScheme()
            .name(jwtSchemeName)
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");
    return new OpenAPI()
            .info(new Info()
                    .title("MyBook API 명세서")
                    .description(("JWT 인증 설정이 적용된 Swagger 화면입니다."))
                    .version("v1.0.0"))
            .addSecurityItem(securityRequirement)
            .components(new Components().addSecuritySchemes(jwtSchemeName,securityScheme));
}
}
