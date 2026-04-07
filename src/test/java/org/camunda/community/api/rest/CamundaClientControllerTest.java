package org.camunda.community.api.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.camunda.community.api.CamundaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import jakarta.servlet.ServletException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class CamundaClientControllerTest {

    private MockMvc mockMvc;
    private CamundaService camundaService;

    @BeforeEach
    void setUp() {
        camundaService = mock(CamundaService.class);
        CamundaClientController controller = new CamundaClientController(camundaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void topologyReturnsServiceResponse() throws Exception {
        when(camundaService.topology()).thenReturn("{\"clusterSize\":3}");

        mockMvc.perform(get("/api/camunda/topology"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"clusterSize\":3}"));

        verify(camundaService).topology();
    }

    @Test
    void getDecisionDefinitionReturnsServiceResponse() throws Exception {
        when(camundaService.getDecisionDefinition(12345L)).thenReturn("{\"decisionDefinitionKey\":12345}");

        mockMvc.perform(get("/api/camunda/decision-definitions/12345"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"decisionDefinitionKey\":12345}"));

        verify(camundaService).getDecisionDefinition(12345L);
    }

    @Test
    void getDecisionDefinitionXmlReturnsServiceResponse() throws Exception {
        when(camundaService.getDecisionDefinitionXml(12345L)).thenReturn("{\"xml\":\"<dmn/>\"}");

        mockMvc.perform(get("/api/camunda/decision-definitions/12345/xml"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"xml\":\"<dmn/>\"}"));

        verify(camundaService).getDecisionDefinitionXml(12345L);
    }

    @Test
    void searchDecisionDefinitionsForwardsBodyAndReturnsServiceResponse() throws Exception {
        String requestBody = """
                {
                  "page": {"from": 0, "limit": 10},
                  "filter": {"name": "loan-decision"}
                }
                """;
        when(camundaService.searchDecisionDefinitions(any())).thenReturn("{\"items\":[]}");

        mockMvc.perform(post("/api/camunda/decision-definitions/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"items\":[]}"));

        verify(camundaService).searchDecisionDefinitions(any());
    }

    @Test
    void evaluateDecisionDefinitionForwardsBodyAndReturnsServiceResponse() throws Exception {
        String requestBody = """
                {
                  "decisionDefinitionKey": "12345",
                  "variables": {"amount": 1000}
                }
                """;
        when(camundaService.evaluateDecisionDefinition(any())).thenReturn("{\"decisionOutput\":\"APPROVED\"}");

        mockMvc.perform(post("/api/camunda/decision-definitions/evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().json("{\"decisionOutput\":\"APPROVED\"}"));

        verify(camundaService).evaluateDecisionDefinition(any());
    }

    @Test
    void nonNumericDecisionKeyReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/camunda/decision-definitions/not-a-number"))
                .andExpect(status().isBadRequest());

        verify(camundaService, never()).getDecisionDefinition(anyLong());
    }

    @Test
    void malformedJsonReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/camunda/decision-definitions/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"page\":"))
                .andExpect(status().isBadRequest());

        verify(camundaService, never()).searchDecisionDefinitions(any());
    }

    @Test
    void serviceExceptionBubblesAsServletException() {
        doThrow(new RuntimeException("boom")).when(camundaService).topology();

        assertThrows(ServletException.class,
                () -> mockMvc.perform(get("/api/camunda/topology")));

        verify(camundaService).topology();
    }
}





