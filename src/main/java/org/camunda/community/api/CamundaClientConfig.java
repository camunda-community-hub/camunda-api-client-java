package org.camunda.community.api;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CamundaClientProperties.class)
public class CamundaClientConfig {

    @Bean
    TokenProvider camundaTokenProvider(
            RestClient.Builder builder,
            CamundaClientProperties properties) {
        return new ClientCredentialsTokenProvider(builder.build(), properties.auth());
    }

    @Bean
    RestClient camundaApiRestClient(
            RestClient.Builder builder,
            CamundaClientProperties properties,
            TokenProvider tokenProvider) {
        return builder
                .baseUrl(resolveBaseUrl(properties))
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .requestInterceptor((request, body, execution) -> {
                    if (!StringUtils.hasText(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))) {
                        String token = tokenProvider.getToken();
                        if (StringUtils.hasText(token)) {
                            request.getHeaders().setBearerAuth(token);
                        }
                    }

                    return execution.execute(request, body);
                })
                .build();
    }

    private static String resolveBaseUrl(CamundaClientProperties properties) {
        String baseUrl = trimTrailingSlash(properties.baseUrl());
        String apiPath = properties.apiPath();

        if (!StringUtils.hasText(apiPath)) {
            return baseUrl;
        }

        return baseUrl + ensureLeadingSlash(trimTrailingSlash(apiPath));
    }

    private static String trimTrailingSlash(String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }

        int endIndex = value.length();
        while (endIndex > 0 && value.charAt(endIndex - 1) == '/') {
            endIndex--;
        }
        return value.substring(0, endIndex);
    }

    private static String ensureLeadingSlash(String value) {
        return value.startsWith("/") ? value : "/" + value;
    }
}

