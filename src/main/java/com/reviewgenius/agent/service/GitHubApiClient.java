/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubApiClient {
  @Value("${github.token}")
  private String githubToken;
  private static final String SHA = "sha";
  private static final String HEAD = "head";

  private final RestTemplate restTemplate;

  public void addReviewComments(String prUrl, Map<String, Object> payload, String url) {
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(payload, buildHeaders());
    try {
      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
      log.info("GitHub review comments added successfully. status={}", response.getStatusCode());
    } catch (RestClientException ex) {
      log.error("Failed to add GitHub review comments. prUrl={}", prUrl, ex);
      throw new RuntimeException("Failed to add GitHub review comments", ex);
    }
  }

  public String fetchLatestCommitSha(String prUrl, String url) {
    try {
      HttpEntity<Void> requestEntity = new HttpEntity<>(buildHeaders());
      ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
          new ParameterizedTypeReference<>() {
          });
      Map<String, Object> responseBody = response.getBody();
      validatePullRequestResponse(responseBody);
      Map<String, Object> head = (Map<String, Object>) responseBody.get(HEAD);
      return (String) head.get(SHA);
    } catch (RestClientException ex) {
      log.error("Failed to fetch latest commit SHA. prUrl={}", prUrl, ex);
      throw new RuntimeException("Failed to fetch latest commit SHA from GitHub", ex);
    }
  }

  public List<Map<String, Object>> getExistingReviewComments(String url) {
    HttpEntity<Void> requestEntity = new HttpEntity<>(buildHeaders());
    try {
      ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
          new ParameterizedTypeReference<>() {
          });
      return response.getBody();

    } catch (RestClientException ex) {
      log.error("Failed to fetch existing review comments", ex);
      return List.of();
    }
  }

  public Map<String, Object> isPullRequestOpen(String url) {
    try {
      HttpEntity<Void> requestEntity = new HttpEntity<>(buildHeaders());
      ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
          new ParameterizedTypeReference<>() {
          });
      return response.getBody();
    } catch (RestClientException ex) {
      log.error("Failed to check PR status. url={}", url, ex);
      return Map.of();
    }
  }

  private HttpHeaders buildHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(githubToken);
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    return headers;
  }

  private void validatePullRequestResponse(Map<String, Object> responseBody) {
    if (responseBody == null || !responseBody.containsKey(HEAD)) {
      throw new IllegalStateException("Invalid GitHub PR response received");
    }
  }
}
