package org.camunda.community.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

class CamundaServiceTest {

    @Test
    void topologyReturnsResponseBody() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        when(restClient.get().uri("/topology").retrieve().body(String.class)).thenReturn("{\"clusterSize\":3}");

        CamundaService service = new CamundaService(restClient);

        String response = service.topology();

        assertEquals("{\"clusterSize\":3}", response);
    }

    @Test
    void topologyThrowsWhenResponseBodyIsNull() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        when(restClient.get().uri("/topology").retrieve().body(String.class)).thenReturn(null);

        CamundaService service = new CamundaService(restClient);

        NullPointerException error = assertThrows(NullPointerException.class, service::topology);
        assertEquals("Camunda topology response must not be null", error.getMessage());
    }

    @Test
    void getDecisionDefinitionReturnsResponseBody() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        when(restClient.get().uri("/decision-definitions/{decisionDefinitionKey}", 12345L).retrieve().body(String.class))
                .thenReturn("{\"decisionDefinitionKey\":12345}");

        CamundaService service = new CamundaService(restClient);

        String response = service.getDecisionDefinition(12345L);

        assertEquals("{\"decisionDefinitionKey\":12345}", response);
    }

    @Test
    void getDecisionDefinitionThrowsWhenResponseBodyIsNull() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        when(restClient.get().uri("/decision-definitions/{decisionDefinitionKey}", 12345L).retrieve().body(String.class))
                .thenReturn(null);

        CamundaService service = new CamundaService(restClient);

        NullPointerException error = assertThrows(NullPointerException.class,
                () -> service.getDecisionDefinition(12345L));

        assertEquals("Camunda decision-definitions/12345 response must not be null", error.getMessage());
    }

    @Test
    void getDecisionDefinitionXmlReturnsResponseBody() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        when(restClient.get().uri("/decision-definitions/{decisionDefinitionKey}/xml", 12345L).retrieve().body(String.class))
                .thenReturn("{\"xml\":\"<dmn/>\"}");

        CamundaService service = new CamundaService(restClient);

        String response = service.getDecisionDefinitionXml(12345L);

        assertEquals("{\"xml\":\"<dmn/>\"}", response);
    }

    @Test
    void getDecisionDefinitionXmlThrowsWhenResponseBodyIsNull() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        when(restClient.get().uri("/decision-definitions/{decisionDefinitionKey}/xml", 12345L).retrieve().body(String.class))
                .thenReturn(null);

        CamundaService service = new CamundaService(restClient);

        NullPointerException error = assertThrows(NullPointerException.class,
                () -> service.getDecisionDefinitionXml(12345L));

        assertEquals("Camunda decision-definitions/12345/xml response must not be null", error.getMessage());
    }

    @Test
    void searchDecisionDefinitionsReturnsResponseBody() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        Map<String, Object> requestBody = Map.of("name", "loan-decision");
        when(restClient.post()
                .uri("/decision-definitions/search")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class))
                .thenReturn("{\"items\":[]}");

        CamundaService service = new CamundaService(restClient);

        String response = service.searchDecisionDefinitions(requestBody);

        assertEquals("{\"items\":[]}", response);
    }

    @Test
    void searchDecisionDefinitionsThrowsWhenResponseBodyIsNull() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        Map<String, Object> requestBody = Map.of("name", "loan-decision");
        when(restClient.post()
                .uri("/decision-definitions/search")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class))
                .thenReturn(null);

        CamundaService service = new CamundaService(restClient);

        NullPointerException error = assertThrows(NullPointerException.class,
                () -> service.searchDecisionDefinitions(requestBody));

        assertEquals("Camunda decision-definitions/search response must not be null", error.getMessage());
    }

    @Test
    void evaluateDecisionDefinitionReturnsResponseBody() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        Map<String, Object> requestBody = Map.of("decisionDefinitionKey", "12345");
        when(restClient.post()
                .uri("/decision-definitions/evaluation")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class))
                .thenReturn("{\"decisionOutput\":\"APPROVED\"}");

        CamundaService service = new CamundaService(restClient);

        String response = service.evaluateDecisionDefinition(requestBody);

        assertEquals("{\"decisionOutput\":\"APPROVED\"}", response);
    }

    @Test
    void evaluateDecisionDefinitionThrowsWhenResponseBodyIsNull() {
        RestClient restClient = mock(RestClient.class, RETURNS_DEEP_STUBS);
        Map<String, Object> requestBody = Map.of("decisionDefinitionKey", "12345");
        when(restClient.post()
                .uri("/decision-definitions/evaluation")
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class))
                .thenReturn(null);

        CamundaService service = new CamundaService(restClient);

        NullPointerException error = assertThrows(NullPointerException.class,
                () -> service.evaluateDecisionDefinition(requestBody));

        assertEquals("Camunda decision-definitions/evaluation response must not be null", error.getMessage());
    }
}

