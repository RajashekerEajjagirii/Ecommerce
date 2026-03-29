package com.raj.ecommerce.config.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "raj-ecommerce")
public class RajeCommerceProperties {

    @NotBlank
    private String baseUri;

    @NotBlank
    private String accountVerifyUri;
    @NotBlank
    private String trackDetailsUri;
}
