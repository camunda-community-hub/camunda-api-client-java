package org.camunda.community.api;

@FunctionalInterface
public interface TokenProvider {

    String getToken();
}

