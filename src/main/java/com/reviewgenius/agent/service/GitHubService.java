package com.reviewgenius.agent.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class GitHubService {

  @Value("${github.owner}")
  private String owner;

  @Value("${github.repo}")
  private String repo;

  @Value("${github.prNumber}")
  private Integer prNumber;

  @Value("${github.token}")
  private String githubToken;

  private final RestTemplate restTemplate = new RestTemplate();

  // TODO: Implement Caching here
  public String fetchPullRequestDiff() {
    // TODO: Implement proper url generation logic here
    String url = "https://api.github.com/repos/" + owner + "/" + repo + "/pulls/" + prNumber;
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(githubToken);
    headers.setAccept(List.of(MediaType.valueOf("application/vnd.github.v3.diff")));
    HttpEntity<Void> entity = new HttpEntity<>(headers);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
    return response.getBody();
  }
}
