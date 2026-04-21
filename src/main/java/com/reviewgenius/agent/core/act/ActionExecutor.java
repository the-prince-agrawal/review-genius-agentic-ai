package com.reviewgenius.agent.core.act;

import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.service.GitHubService;
import com.reviewgenius.agent.util.DiffParserUtil;
import org.springframework.stereotype.Service;

@Service
public class ActionExecutor {

  private final GitHubService gitHubService;

  public ActionExecutor(GitHubService gitHubService) {
    this.gitHubService = gitHubService;
  }

  public String act(ActionType actionType, AgentContext context) {
    switch (actionType) {
      case FETCH_PR :
        String diff = gitHubService.fetchPullRequestDiff(context.getInputDto().getPrURL());
        context.setRawDiff(diff);
        return "PR fetched";

      case PARSE_DIFF :
        String parsed = DiffParserUtil.parse(context.getRawDiff());
        context.setParsedDiff(parsed);
        return "Diff parsed";

      case ANALYZE_CODE :
        return "LLM analysis (simulated)";

      case GENERATE_REVIEW :
        context.setReview("Review generated (simulated)");
        return "Review ready";

      default :
        return "No action";
    }
  }
}
