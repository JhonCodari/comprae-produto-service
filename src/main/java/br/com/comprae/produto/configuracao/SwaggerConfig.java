package br.com.comprae.produto.configuracao;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração do Swagger/OpenAPI para documentação da API
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Compraê Produto Service API")
                        .description("API do Microserviço de Produtos do Ecosistema Compraê")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Compraê")
                                .email("dev@comprae.com.br")
                                .url("https://comprae.com.br"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
