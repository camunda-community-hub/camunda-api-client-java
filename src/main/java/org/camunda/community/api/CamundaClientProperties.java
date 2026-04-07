package org.camunda.community.api;

import java.time.Duration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "camunda")
public record CamundaClientProperties(
        @NotBlank String baseUrl,
        @DefaultValue("/v2") String apiPath,
        @Valid @DefaultValue AuthProperties auth) {

    public record AuthProperties(
            @NotBlank String tokenUrl,
            @NotBlank String clientId,
            @NotBlank String clientSecret,
            @NotBlank String audience,
            @Pattern(regexp = "^(Zeebe|Tasklist|Operate)$", message = "scope must be one of: Zeebe, Tasklist, Operate")
            @NotBlank String scope,
            @DefaultValue("PT30S") Duration refreshSkew) {
    }
}

