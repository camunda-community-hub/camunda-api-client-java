package org.camunda.community.api.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.camunda.community.api.CamundaService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/camunda")
@Tag(name = "Camunda", description = "Camunda API proxy endpoints")
public class CamundaClientController {

    private final CamundaService camundaService;

    public CamundaClientController(CamundaService camundaService) {
        this.camundaService = camundaService;
    }

    @GetMapping(path = "/topology", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Camunda Cluster topology", description = "Returns Camunda Cluster topology payload from the configured Camunda base URL")
    public String topology() {
        return this.camundaService.topology();
    }

    @GetMapping(path = "/decision-definitions/{decisionDefinitionKey}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get a decision definition", description = "Returns a Camunda decision definition by its key")
    public String getDecisionDefinition(@PathVariable long decisionDefinitionKey) {
        return this.camundaService.getDecisionDefinition(decisionDefinitionKey);
    }

    @GetMapping(path = "/decision-definitions/{decisionDefinitionKey}/xml", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get decision definition XML", description = "Returns the XML representation of a Camunda decision definition by its key")
    public String getDecisionDefinitionXml(@PathVariable long decisionDefinitionKey) {
        return this.camundaService.getDecisionDefinitionXml(decisionDefinitionKey);
    }

    @PostMapping(path = "/decision-definitions/search",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search decision definitions", description = "Searches Camunda decision definitions using the provided filter criteria")
    public String searchDecisionDefinitions(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object"),
                            examples = @ExampleObject(
                                    name = "DecisionDefinitionSearchRequest",
                                    value = """
                                            {
                                              \"page\": {
                                                \"from\": 0,
                                                \"limit\": 100
                                              },
                                              \"sort\": [
                                                {
                                                  \"field\": \"decisionDefinitionKey\",
                                                  \"order\": \"ASC\"
                                                }
                                              ],
                                              \"filter\": {
                                                \"decisionDefinitionId\": \"new-hire-onboarding-workflow\",
                                                \"name\": \"string\",
                                                \"version\": 0,
                                                \"decisionRequirementsId\": \"string\",
                                                \"tenantId\": \"customer-service\",
                                                \"decisionDefinitionKey\": \"2251799813326547\",
                                                \"decisionRequirementsKey\": \"2251799813683346\"
                                              }
                                            }
                                            """
                            )
                    )
            )
            @RequestBody Object requestBody) {
        return this.camundaService.searchDecisionDefinitions(requestBody);
    }

    @PostMapping(path = "/decision-definitions/evaluation",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Evaluate a decision definition", description = "Evaluates a Camunda decision definition using the provided input variables")
    public String evaluateDecisionDefinition(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(type = "object"),
                            examples = {
                                    @ExampleObject(
                                            name = "Evaluate using decisionDefinitionId",
                                            value = """
                                                    {
                                                      "decisionDefinitionId": "1234-5678",
                                                      "variables": {}
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "Evaluate using decisionDefinitionKey",
                                            value = """
                                                    {
                                                      "decisionDefinitionKey": "12345",
                                                      "variables": {}
                                                    }
                                                    """
                                    )
                            }
                    )
            )
            @RequestBody Object requestBody) {
        return this.camundaService.evaluateDecisionDefinition(requestBody);
    }
}

