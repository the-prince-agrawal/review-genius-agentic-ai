package com.reviewgenius.agent.orchestrator;

import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.service.GitHubService;
import com.reviewgenius.agent.util.DiffParserUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class AgentOrchestrator {

  @Value("${agent.maxSteps:5}")
  private int maxSteps;

  private final GitHubService gitHubService;

  public AgentOrchestrator(GitHubService gitHubService) {
    this.gitHubService = gitHubService;
  }

  public String runAgent(String input) {

    AgentContext context = AgentContext.builder().steps(new ArrayList<>()).input(input).completed(false).build();

    for (int i = 0; i < maxSteps; i++) {

      String thought = think(context);
      String action = act(thought);
      String observation = observe(action);

      context.getSteps().add("Thought: " + thought + " | Action: " + action + " | Observation: " + observation);

      if (isDone(context)) {
        break;
      }
    }

    return formatResponse(context);
  }

  private String think(AgentContext context) {

    if (context.getSteps().isEmpty()) {
      String diff = gitHubService.fetchPullRequestDiff();
      return DiffParserUtil.parse(diff);
    } else if (context.getSteps().size() == 1) {
      return "Analyze code";
    } else {
      return "Generate review";
    }
  }

  private String act(String thought) {

    if (thought.contains("fetch")) {
      return "Calling GitHub API (simulated)";
    } else if (thought.contains("Analyze")) {
      return "Calling LLM (simulated)";
    } else {
      return "Formatting review";
    }
  }

  private String observe(String action) {

    if (action.contains("GitHub")) {
      return "Code fetched successfully";
    } else if (action.contains("LLM")) {
      return "Analysis complete";
    } else {
      return "Review ready";
    }
  }

  private boolean isDone(AgentContext context) {
    return context.getSteps().size() >= 3;
  }

  private String formatResponse(AgentContext context) {
    return String.join("\n", context.getSteps());
  }
}
