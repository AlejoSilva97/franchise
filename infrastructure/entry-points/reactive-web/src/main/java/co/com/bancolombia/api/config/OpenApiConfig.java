package co.com.bancolombia.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI franchiseOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Franchise Management API")
                        .description("API for managing franchises, branches and products")
                        .version("1.0.0"));
    }
}
