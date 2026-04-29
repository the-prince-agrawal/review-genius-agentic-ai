package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.act.ActionResultStatus;
import com.reviewgenius.agent.core.think.LLMClient;
import com.reviewgenius.agent.core.think.prompt.PromptBuilder;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import org.springframework.stereotype.Service;

import static com.reviewgenius.agent.enums.ActionType.ANALYZE_CODE;

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
    context.setAnalysis(analysisResult);
    return new ActionResult(ActionResultStatus.SUCCESS, "Code analyzed", analysisResult, null);
  }

  @Override
  public ActionType getType() {
    return ANALYZE_CODE;
  }
}
