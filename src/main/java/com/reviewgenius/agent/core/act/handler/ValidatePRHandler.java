package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.service.GitHubPullRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidatePRHandler implements ActionHandler {

  private final GitHubPullRequestService gitHubPullRequestService;
  @Override
  public ActionResult<?> execute(AgentContext context) {
    boolean isPROpen = gitHubPullRequestService.isPullRequestOpen(context.getInputDto().getPrURL());
    context.setPRStateValidated(true);
    if (isPROpen) {
      return ActionResult.success("Pull request is open", null);
    }
    context.setWorkflowFailed(true);
    return ActionResult.failure("Pull request is not open",
        "The pull request associated with this review request is not open. Please open the pull request and try again.");
  }

  @Override
  public ActionType getType() {
    return ActionType.VALIDATE_PR_STATE;
  }
}
