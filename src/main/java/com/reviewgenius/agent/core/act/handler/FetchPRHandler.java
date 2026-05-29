/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.service.GitHubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.reviewgenius.agent.enums.ActionType.FETCH_PR_DIFF;

@Service
@Slf4j
public class FetchPRHandler implements ActionHandler {

  private final GitHubService gitHubService;

  public FetchPRHandler(GitHubService gitHubService) {
    this.gitHubService = gitHubService;
  }

  @Override
  public ActionResult<String> execute(AgentContext context) {
    String prUrl = context.getInputDto().getPrURL();

    try {
      String diff = gitHubService.fetchPullRequestDiff(prUrl);
      if (!StringUtils.hasText(diff)) {
        log.error("Received empty diff from GitHub");
        return ActionResult.failure("Failed to fetch PR", "GitHub returned empty diff");
      }

      context.setRawDiff(diff);
      return ActionResult.success("PR fetched successfully", diff);

    } catch (Exception ex) {
      log.error("Error while fetching PR diff", ex);
      return ActionResult.failure("Failed to fetch PR", ex.getMessage());
    }
  }

  @Override
  public ActionType getType() {
    return FETCH_PR_DIFF;
  }
}
