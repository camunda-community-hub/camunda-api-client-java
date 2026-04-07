package org.camunda.community.api;

import java.util.Objects;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class CamundaService {

    private static final String DECISION_DEFINITIONS_PATH = "/decision-definitions/{decisionDefinitionKey}";

    private final RestClient camundaApiRestClient;

    public CamundaService(RestClient camundaApiRestClient) {
        this.camundaApiRestClient = camundaApiRestClient;
    }

    public String topology() {
        return requireResponse(get("/topology"), "Camunda topology response must not be null");
    }

    public String getDecisionDefinition(long decisionDefinitionKey) {
        return requireResponse(
                get(DECISION_DEFINITIONS_PATH, decisionDefinitionKey),
                "Camunda decision-definitions/" + decisionDefinitionKey + " response must not be null");
    }

    public String getDecisionDefinitionXml(long decisionDefinitionKey) {
        return requireResponse(
                get(DECISION_DEFINITIONS_PATH + "/xml", decisionDefinitionKey),
                "Camunda decision-definitions/" + decisionDefinitionKey + "/xml response must not be null");
    }

    public String searchDecisionDefinitions(Object requestBody) {
        return requireResponse(
                post("/decision-definitions/search", requestBody),
                "Camunda decision-definitions/search response must not be null");
    }

    public String evaluateDecisionDefinition(Object requestBody) {
        return requireResponse(
                post("/decision-definitions/evaluation", requestBody),
                "Camunda decision-definitions/evaluation response must not be null");
    }

    private String get(String path, Object... uriVariables) {
        return this.camundaApiRestClient.get()
                .uri(path, uriVariables)
                .retrieve()
                .body(String.class);
    }

    private String post(String path, Object requestBody) {
        return this.camundaApiRestClient.post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);
    }

    private static String requireResponse(String response, String errorMessage) {
        return Objects.requireNonNull(response, errorMessage);
    }
}

