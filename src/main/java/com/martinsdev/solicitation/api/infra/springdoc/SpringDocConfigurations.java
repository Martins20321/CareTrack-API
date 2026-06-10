package com.martinsdev.solicitation.api.infra.springdoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfigurations {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                .info(new Info()
                        .title("Solicitation API SEA")
                        .description("API REST para gerenciamento de solicitações de atendimento com controle de acesso por perfil (CLIENT, ANALYST, ADMIN)")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("José Gabriel")
                                .email("jgmsilva11@gmail.com")));
    }


}
