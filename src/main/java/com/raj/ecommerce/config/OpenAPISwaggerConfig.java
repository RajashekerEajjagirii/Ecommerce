package com.raj.ecommerce.config;

import com.raj.ecommerce.constants.Constants;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenAPISwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI(){
         return new OpenAPI()
                 .info(new Info()
                         .title("Ecommerce Application Services")
                         .description("By Raja")
                         .version("v1")
                 ).servers(
                         Arrays.asList(new Server().url("http://localhost:8080").description("dev"),
                                 new Server().url("https://rajEcommerce.com").description("live"))
                 ).tags(
                         Arrays.asList(new Tag().name("public-module").description("Operations include users signUp and signIn"),
                                 new Tag().name("product-module").description("Here you can perform product related operations"),
                                 new Tag().name("user-module").description("Here you can perform user related Operations"))
                 ).components(
                         new Components()
                                 .addSecuritySchemes(Constants.SECURITY_SCHEME_NAME,new SecurityScheme()
                                         .name(Constants.SECURITY_SCHEME_NAME)
                                         .type(SecurityScheme.Type.HTTP)
                                         .scheme("bearer")
                                         .bearerFormat("JWT")
                                 )
                 ).addSecurityItem(new SecurityRequirement().addList(Constants.SECURITY_SCHEME_NAME));
    }
}
