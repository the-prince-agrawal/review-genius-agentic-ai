/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.agent.AnalyzeCodeAgent;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.Issue;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.reviewgenius.agent.enums.ActionType.ANALYZE_CODE;

@Service
@Slf4j
@AllArgsConstructor
public class AnalyzeCodeHandler implements ActionHandler {

  private AnalyzeCodeAgent analyzeCodeAgent;

  @Override
  public ActionResult<?> execute(AgentContext context) {
    try {
      List<Issue> issues = analyzeCodeAgent.analyze(context, context.getParsedDiff());
      context.setIssues(issues);
      log.info("Parallel code analysis completed. totalIssues={}", issues.size());
      return ActionResult.success("Code analyzed", Map.of("issueCount", issues.size()));
    } catch (Exception ex) {
      log.error("Error during parallel code analysis: {}", ex.getMessage(), ex);
      return ActionResult.failure("Action execution failed", ex.getMessage());
    }
  }

  @Override
  public ActionType getType() {
    return ANALYZE_CODE;
  }
}
