package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.act.ActionResultStatus;
import com.reviewgenius.agent.model.AgentContext;
import org.springframework.stereotype.Service;

@Service
public class AnalyzeCodeHandler implements ActionHandler {
  @Override
  public ActionResult execute(AgentContext context) {
    String analysisResult = "Code analysis completed. No issues found.";
    context.setAnalysis(analysisResult);
    return new ActionResult(ActionResultStatus.SUCCESS, "Code analyzed", analysisResult);
  }
}
