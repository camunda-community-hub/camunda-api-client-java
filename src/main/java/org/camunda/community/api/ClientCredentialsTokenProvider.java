package org.camunda.community.api;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

public final class ClientCredentialsTokenProvider implements TokenProvider {

    private final RestClient restClient;
    private final CamundaClientProperties.AuthProperties auth;
    private final Clock clock;
    private volatile CachedToken cachedToken;

    public ClientCredentialsTokenProvider(
            RestClient restClient,
            CamundaClientProperties.AuthProperties auth) {
        this(restClient, auth, Clock.systemUTC());
    }

    public ClientCredentialsTokenProvider(
            RestClient restClient,
            CamundaClientProperties.AuthProperties auth,
            Clock clock) {
        this.restClient = restClient;
        this.auth = auth;
        this.clock = clock;
    }

    @Override
    public String getToken() {
        CachedToken currentToken = this.cachedToken;
        Instant now = this.clock.instant();

        if (isUsable(currentToken, now)) {
            return currentToken.accessToken();
        }

        synchronized (this) {
            currentToken = this.cachedToken;
            now = this.clock.instant();

            if (isUsable(currentToken, now)) {
                return currentToken.accessToken();
            }

            CachedToken refreshedToken = requestToken(now);
            this.cachedToken = refreshedToken;
            return refreshedToken.accessToken();
        }
    }

    private boolean isUsable(CachedToken token, Instant now) {
        return token != null && token.expiresAt().isAfter(now.plus(this.auth.refreshSkew()));
    }

    private CachedToken requestToken(Instant now) {
        validateAuthConfiguration();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", this.auth.clientId());
        formData.add("client_secret", this.auth.clientSecret());
        addIfPresent(formData, "audience", this.auth.audience());
        addIfPresent(formData, "scope", this.auth.scope());

        TokenResponse response = this.restClient.post()
                .uri(this.auth.tokenUrl())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(formData)
                .retrieve()
                .body(TokenResponse.class);

        TokenResponse tokenResponse = Objects.requireNonNull(response, "Camunda token response must not be null");
        String accessToken = tokenResponse.accessToken();
        if (!StringUtils.hasText(accessToken)) {
            throw new IllegalStateException("Camunda token response does not contain access_token");
        }

        long expiresIn = tokenResponse.expiresIn() != null ? tokenResponse.expiresIn() : 300L;
        return new CachedToken(accessToken, now.plusSeconds(Math.max(1L, expiresIn)));
    }

    private void validateAuthConfiguration() {
        if (!StringUtils.hasText(this.auth.tokenUrl())) {
            throw new IllegalStateException("camunda.auth.token-url must be configured");
        }
        if (!StringUtils.hasText(this.auth.clientId())) {
            throw new IllegalStateException("camunda.auth.client-id must be configured");
        }
        if (!StringUtils.hasText(this.auth.clientSecret())) {
            throw new IllegalStateException("camunda.auth.client-secret must be configured");
        }
    }

    private static void addIfPresent(MultiValueMap<String, String> formData, String name, String value) {
        if (StringUtils.hasText(value)) {
            formData.add(name, value);
        }
    }

    private record CachedToken(String accessToken, Instant expiresAt) {
    }

    private record TokenResponse(String access_token, Long expires_in) {
        String accessToken() {
            return this.access_token;
        }

        Long expiresIn() {
            return this.expires_in;
        }
    }
}

