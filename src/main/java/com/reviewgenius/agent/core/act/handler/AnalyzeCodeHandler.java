package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.act.ActionResultStatus;
import com.reviewgenius.agent.core.think.LLMClient;
import com.reviewgenius.agent.core.think.prompt.PromptBuilder;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.Issue;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.reviewgenius.agent.enums.ActionType.ANALYZE_CODE;
import static com.reviewgenius.agent.util.IssueParserUtil.parseIssues;

@Service
public class AnalyzeCodeHandler implements ActionHandler {

  private final LLMClient LLMClient;
  private final PromptBuilder promptBuilder;

  public AnalyzeCodeHandler(LLMClient LLMClient, PromptBuilder promptBuilder) {
    this.LLMClient = LLMClient;
    this.promptBuilder = promptBuilder;
  }

  @Override
  public ActionResult execute(AgentContext context) {
    String prompt = promptBuilder.buildCodeReviewPrompt(context.getParsedDiff(), context.getInputDto().getReviewType());
    String analysisResult = LLMClient.getResponse(prompt);
    List<Issue> issues = parseIssues(analysisResult);
    context.setAnalysis(analysisResult);
    context.setIssues(issues);
    return ActionResult.success("Code analyzed", analysisResult);
  }

  @Override
  public ActionType getType() {
    return ANALYZE_CODE;
  }
}
