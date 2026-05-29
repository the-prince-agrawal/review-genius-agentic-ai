/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.reviewgenius.agent.config.CacheConfig.LOGS_CACHE;
import static com.reviewgenius.agent.util.CommonUtil.getAnalyzeCodeMockResponse;

@Service
@RequiredArgsConstructor
public class GitHubService {
  @Value("${github.token}")
  private String githubToken;

  @Value("${github.remoteFetchDisabled:false}")
  private boolean isGithubRemoteFetchDisabled;

  private final RestTemplate restTemplate;

  @Cacheable(value = LOGS_CACHE, key = "#input")
  public String fetchPullRequestDiff(String input) {
    if (isGithubRemoteFetchDisabled) {
      return getAnalyzeCodeMockResponse("static/mock-github-pr.patch");
    }
    HttpEntity<Void> entity = getHeader();
    String diffUrl = getDiffUrl(input);
    ResponseEntity<String> response = restTemplate.exchange(diffUrl, HttpMethod.GET, entity, String.class);
    return response.getBody();
  }

  private HttpEntity<Void> getHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(githubToken);
    return new HttpEntity<>(headers);
  }

  private static String getDiffUrl(String input) {
    return input.endsWith("/") ? input.substring(0, input.length() - 1) + ".diff" : input + ".diff";
  }
}
