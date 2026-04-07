package org.camunda.community.api;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI camundaClientOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Camunda Client API")
                .description("REST endpoints for invoking Camunda APIs through Spring RestClient")
                .version("v1"));
    }
}

