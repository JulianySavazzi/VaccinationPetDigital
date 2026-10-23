package com.juliany.vaccinationpet.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI vaccinationPetOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vaccination Pet Digital API")
                        .version("v1")
                        .description("API para cadastro de pets e gerenciamento de suas vacinações."));
    }
}
