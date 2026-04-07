# Spring HTTP Camunda Client (Gradle)

**Last Updated:** April 2026

A Spring Boot client application that calls Camunda 8 SaaS REST endpoints using OAuth client credentials.

---

## Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
  - [Environment Variables](#environment-variables)
  - [Get Required Values from Camunda 8 SaaS](#get-required-values-from-camunda-8-saas)
  - [Network Access Requirements](#network-access-requirements)
  - [Setting Environment Variables](#setting-environment-variables)
    - [macOS and Linux](#macos-and-linux)
    - [Windows PowerShell](#windows-powershell)
    - [Windows Command Prompt](#windows-command-prompt)
  - [Using direnv (Alternative)](#using-direnv-alternative)
- [Request Flow](#request-flow)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [API Documentation](#api-documentation)
- [Running Tests](#running-tests)
- [Building a JAR](#building-a-jar)
- [Troubleshooting](#troubleshooting)
- [License](#license)

---

## Overview

This project provides a minimal, production-style example of:

- configuring a Spring `RestClient` for Camunda API calls
- loading Camunda settings from `camunda.*` properties
- obtaining and caching OAuth access tokens with a `TokenProvider`
- exposing the following local proxy endpoints:
  - `GET  /api/camunda/topology`
  - `GET  /api/camunda/decision-definitions/{decisionDefinitionKey}`
  - `GET  /api/camunda/decision-definitions/{decisionDefinitionKey}/xml`
  - `POST /api/camunda/decision-definitions/search`
  - `POST /api/camunda/decision-definitions/evaluation`
- forwarding each request to the equivalent Camunda 8 SaaS REST endpoint

---

## Tech Stack

| Technology | Version |
| --- | --- |
| Java | 21 |
| Spring Boot | 4.0.5 |
| Spring Web MVC | via `spring-boot-starter-webmvc` |
| Spring RestClient | via `spring-boot-starter-restclient` |
| Bean Validation | via `spring-boot-starter-validation` |
| OpenAPI + Swagger UI | `springdoc-openapi-starter-webmvc-ui:3.0.2` |
| Build Tool | Gradle Wrapper (`./gradlew`) |

---

## Project Structure

```text
src/
├── main/
│   ├── java/org/camunda/community/api/
│   │   ├── CamundaClientApplication.java        # Spring Boot entry point
│   │   ├── CamundaClientConfig.java             # RestClient and token provider wiring
│   │   ├── CamundaClientProperties.java         # Configuration properties + validation
│   │   ├── CamundaService.java                  # Camunda API service using RestClient
│   │   ├── OpenApiConfig.java                   # OpenAPI metadata configuration
│   │   ├── TokenProvider.java                   # Token abstraction
│   │   ├── ClientCredentialsTokenProvider.java  # OAuth client credentials token flow
│   │   └── rest/CamundaClientController.java    # HTTP endpoints delegating to CamundaService
│   └── resources/
│       └── application.yaml                     # Env-backed configuration defaults
└── test/
    └── java/org/camunda/community/api/
        ├── CamundaClientApplicationTests.java
        ├── CamundaServiceTest.java
        └── rest/CamundaClientControllerTest.java
```

---

## Prerequisites

- Java 21 (or compatible with this project)
- Gradle (or use the included wrapper `./gradlew`)
- Camunda 8 SaaS account with client credentials

---

## Configuration

Configuration is defined in `src/main/resources/application.yaml` and is environment-variable driven.

### Environment Variables

| Property | Environment Variable | Required | Default | Notes |
| --- | --- | --- | --- | --- |
| `camunda.base-url` | `CAMUNDA_BASE_URL` | Yes | None | Camunda REST endpoint from `ZEEBE_REST_ADDRESS` |
| `camunda.api-path` | `CAMUNDA_API_PATH` | No | `/v2` | API prefix |
| `camunda.auth.token-url` | `CAMUNDA_TOKEN_URL` | No | `https://login.cloud.camunda.io/oauth/token` | OAuth token endpoint |
| `camunda.auth.client-id` | `CAMUNDA_CLIENT_ID` | Yes | None | Must not be blank |
| `camunda.auth.client-secret` | `CAMUNDA_CLIENT_SECRET` | Yes | None | Must not be blank |
| `camunda.auth.audience` | `CAMUNDA_AUDIENCE` | No | `zeebe.camunda.io` | OAuth audience |
| `camunda.auth.scope` | `CAMUNDA_SCOPE` | No | `Zeebe` | Must be one of: `Zeebe`, `Tasklist`, `Operate` |
| `camunda.auth.refresh-skew` | `CAMUNDA_TOKEN_REFRESH_SKEW` | No | `PT30S` | ISO-8601 duration |

### Get Required Values from Camunda 8 SaaS

1. Sign in to Camunda Console.
2. Open your cluster and copy the REST endpoint from `ZEEBE_REST_ADDRESS` for `CAMUNDA_BASE_URL`.
3. Open **API / Client Credentials**.
4. Create or open an M2M credential.
5. Copy:
   - `CAMUNDA_BASE_URL` from `ZEEBE_REST_ADDRESS`
   - `CAMUNDA_CLIENT_ID`
   - `CAMUNDA_CLIENT_SECRET`

### Network Access Requirements

Ensure outbound access from your environment to:

- your `ZEEBE_REST_ADDRESS` endpoint (for example `https://<region>.zeebe.camunda.io/<clusterId>`)
- `https://login.cloud.camunda.io/oauth/token`

### Setting Environment Variables

#### macOS and Linux

```zsh
export CAMUNDA_BASE_URL="https://your-camunda-endpoint"
export CAMUNDA_API_PATH="/v2"
export CAMUNDA_TOKEN_URL="https://login.cloud.camunda.io/oauth/token"
export CAMUNDA_CLIENT_ID="your-client-id"
export CAMUNDA_CLIENT_SECRET="your-client-secret"
export CAMUNDA_AUDIENCE="zeebe.camunda.io"
export CAMUNDA_SCOPE="Zeebe"
export CAMUNDA_TOKEN_REFRESH_SKEW="PT30S"
```

#### Windows PowerShell

```powershell
$env:CAMUNDA_BASE_URL="https://your-camunda-endpoint"
$env:CAMUNDA_API_PATH="/v2"
$env:CAMUNDA_TOKEN_URL="https://login.cloud.camunda.io/oauth/token"
$env:CAMUNDA_CLIENT_ID="your-client-id"
$env:CAMUNDA_CLIENT_SECRET="your-client-secret"
$env:CAMUNDA_AUDIENCE="zeebe.camunda.io"
$env:CAMUNDA_SCOPE="Zeebe"
$env:CAMUNDA_TOKEN_REFRESH_SKEW="PT30S"
```

#### Windows Command Prompt

```bat
set CAMUNDA_BASE_URL=https://your-camunda-endpoint
set CAMUNDA_API_PATH=/v2
set CAMUNDA_TOKEN_URL=https://login.cloud.camunda.io/oauth/token
set CAMUNDA_CLIENT_ID=your-client-id
set CAMUNDA_CLIENT_SECRET=your-client-secret
set CAMUNDA_AUDIENCE=zeebe.camunda.io
set CAMUNDA_SCOPE=Zeebe
set CAMUNDA_TOKEN_REFRESH_SKEW=PT30S
```

### Using direnv (Alternative)

If you use `direnv`, define `CAMUNDA_*` values in `.envrc` at the project root.

```zsh
direnv allow
```

Example `.envrc`:

```sh
export CAMUNDA_BASE_URL="https://your-camunda-endpoint"
export CAMUNDA_API_PATH="/v2"
export CAMUNDA_TOKEN_URL="https://login.cloud.camunda.io/oauth/token"
export CAMUNDA_CLIENT_ID="your-client-id"
export CAMUNDA_CLIENT_SECRET="your-client-secret"
export CAMUNDA_AUDIENCE="zeebe.camunda.io"
export CAMUNDA_SCOPE="Zeebe"
export CAMUNDA_TOKEN_REFRESH_SKEW="PT30S"
```

---

## Request Flow

1. Call one of the local proxy endpoints (for example `GET /api/camunda/topology`).
2. `CamundaClientController` delegates to `CamundaService`.
3. `CamundaService` invokes the configured Camunda `RestClient` with the appropriate method/path.
4. The request interceptor asks `TokenProvider` for a token.
5. `ClientCredentialsTokenProvider` fetches or reuses a cached token.
6. Request is sent with `Authorization: Bearer <token>`.

---

## Running the Application

```zsh
./gradlew bootRun
```

---

## API Endpoints

| Method | Local URL | Camunda SaaS path |
| --- | --- | --- |
| `GET` | `http://localhost:8080/api/camunda/topology` | `/v2/topology` |
| `GET` | `http://localhost:8080/api/camunda/decision-definitions/{decisionDefinitionKey}` | `/v2/decision-definitions/{decisionDefinitionKey}` |
| `GET` | `http://localhost:8080/api/camunda/decision-definitions/{decisionDefinitionKey}/xml` | `/v2/decision-definitions/{decisionDefinitionKey}/xml` |
| `POST` | `http://localhost:8080/api/camunda/decision-definitions/search` | `/v2/decision-definitions/search` |
| `POST` | `http://localhost:8080/api/camunda/decision-definitions/evaluation` | `/v2/decision-definitions/evaluation` |

Quick check:

```zsh
curl http://localhost:8080/api/camunda/topology
curl http://localhost:8080/api/camunda/decision-definitions/2251799813685249
curl http://localhost:8080/api/camunda/decision-definitions/2251799813685249/xml
curl -X POST http://localhost:8080/api/camunda/decision-definitions/search -H "Content-Type: application/json" -d '{"page":{"from":0,"limit":100},"sort":[{"field":"decisionDefinitionKey","order":"ASC"}]}'
curl -X POST http://localhost:8080/api/camunda/decision-definitions/evaluation -H "Content-Type: application/json" -d '{"decisionDefinitionKey":"12345","variables":{}}'
```

---

## API Documentation

After starting the app, OpenAPI/Swagger is available at:

- `Swagger UI`: `http://localhost:8080/swagger-ui/index.html`
- `OpenAPI JSON`: `http://localhost:8080/v3/api-docs`

---

## Running Tests

```zsh
./gradlew test
```

---

## Building a JAR

```zsh
./gradlew clean build
```

Built artifacts are placed under `build/libs/`.

---

## Troubleshooting

- `camunda.base-url` is missing or blank
  - Set `CAMUNDA_BASE_URL` to your Camunda REST endpoint (`ZEEBE_REST_ADDRESS`).
- `camunda.auth.client-id` or `camunda.auth.client-secret` is missing
  - Set non-empty values for `CAMUNDA_CLIENT_ID` and `CAMUNDA_CLIENT_SECRET`.
- `camunda.auth.scope` validation fails
  - Use one of `Zeebe`, `Tasklist`, or `Operate`.
- OAuth token request fails
  - Verify `CAMUNDA_TOKEN_URL`, `CAMUNDA_AUDIENCE`, credentials, and network connectivity.

---

## License

This project is licensed under the Apache License 2.0. See `LICENSE` for the full license text.

