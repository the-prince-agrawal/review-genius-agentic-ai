package com.reviewgenius.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PullRequestMetadata {
  private String prUrl;
  private String owner;
  private String repo;
  private Integer pullRequestNumber;
  private String latestCommitSha;
}
