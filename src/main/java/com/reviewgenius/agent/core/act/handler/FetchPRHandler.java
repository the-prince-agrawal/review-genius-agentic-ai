package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.service.GitHubService;
import org.springframework.stereotype.Service;

import static com.reviewgenius.agent.core.act.ActionResultStatus.SUCCESS;

@Service
public class FetchPRHandler implements ActionHandler {

  private final GitHubService gitHubService;

  public FetchPRHandler(GitHubService gitHubService) {
    this.gitHubService = gitHubService;
  }

  @Override
  public ActionResult execute(AgentContext context) {
    String diff = gitHubService.fetchPullRequestDiff(
        context.getInputDto().getPrURL());
    context.setRawDiff(diff);

    return new ActionResult(SUCCESS, "PR fetched", diff);
  }
}
