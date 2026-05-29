/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.Issue;
import com.reviewgenius.agent.service.GitHubPullRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddReviewCommentsHandler implements ActionHandler {
  private final RestTemplate restTemplate;
  private final GitHubPullRequestService gitHubPullRequestService;

  @Override
  public ActionResult<?> execute(AgentContext context) {
    try {
      String prURL = context.getInputDto().getPrURL();
      String commitSha = gitHubPullRequestService.fetchLatestCommitSha(prURL);
      List<Issue> issues = context.getIssues();
      gitHubPullRequestService.addReviewComments(prURL, commitSha, issues);
      context.setPrCommentsAdded(true);
      return ActionResult.success("Review comments added successfully", null);
    } catch (Exception e) {
      log.error("Error adding review comments to GitHub", e);
      return ActionResult.failure("Failed to add review comments: ", e.getMessage());
    }
  }

  @Override
  public ActionType getType() {
    return ActionType.ADD_REVIEW_COMMENTS;
  }
}
