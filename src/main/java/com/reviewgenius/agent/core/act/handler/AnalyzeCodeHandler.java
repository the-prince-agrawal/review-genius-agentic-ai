package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.think.LLMClient;
import com.reviewgenius.agent.core.think.prompt.PromptBuilder;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.Issue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.reviewgenius.agent.enums.ActionType.ANALYZE_CODE;
import static com.reviewgenius.agent.util.IssueParserUtil.parseIssues;

@Service
@Slf4j
public class AnalyzeCodeHandler implements ActionHandler {

  private final LLMClient LLMClient;
  private final PromptBuilder promptBuilder;

  public AnalyzeCodeHandler(LLMClient LLMClient, PromptBuilder promptBuilder) {
    this.LLMClient = LLMClient;
    this.promptBuilder = promptBuilder;
  }

  @Override
  public ActionResult<?> execute(AgentContext context) {
    String prompt = promptBuilder.buildCodeReviewPrompt(context);
    log.debug("Using prompt version: {}", context.getPromptVersion());
    try {
      String analysisResult = LLMClient.getResponse(prompt);
      List<Issue> issues = parseIssues(analysisResult);
      context.setAnalysis(analysisResult);
      context.setIssues(issues);
      return ActionResult.success("Code analyzed",
          Map.of("analysisResult", analysisResult, "issueCount", issues.size()));
    } catch (Exception ex) {
      log.error("Error during code analysis: {}", ex.getMessage(), ex);
      return ActionResult.failure("Action execution failed", ex.getMessage());
    }
  }

  @Override
  public ActionType getType() {
    return ANALYZE_CODE;
  }
}
