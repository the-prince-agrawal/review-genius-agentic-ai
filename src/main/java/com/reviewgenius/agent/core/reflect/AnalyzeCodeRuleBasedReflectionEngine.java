/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.core.reflect;

import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.enums.ReflectionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.Issue;
import com.reviewgenius.agent.model.ReflectionResult;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class AnalyzeCodeRuleBasedReflectionEngine
    extends
      AbstractReflectionEngine {

  @Override
  public ReflectionResult reflect(ActionType actionType, ActionResult<?> result, AgentContext context) {
    ReflectionResult reflectionResult = new ReflectionResult();
    reflectionResult.setReflectionType(ReflectionType.STRUCTURAL);

    validateAnalyzeCode(context, reflectionResult);

    if (reflectionResult.getDetectedProblems().isEmpty()) {
      return buildSuccessResult(reflectionResult);
    }
    return buildFailureResult(reflectionResult);
  }

  private void validateAnalyzeCode(AgentContext context, ReflectionResult reflectionResult) {
    List<Issue> issues = context.getIssues();

    if (CollectionUtils.isEmpty(issues)) {
      reflectionResult.addProblem("Issues list is null or empty");
      return;
    }

    for (Issue issue : issues) {
      if (!StringUtils.hasText(issue.getFileName())) {
        reflectionResult.addProblem("Issue contains empty fileName");
      }

      if (issue.getSeverity() == null) {
        reflectionResult.addProblem("Severity is missing");
      }

      if (!StringUtils.hasText(issue.getDescription())) {
        reflectionResult.addProblem("Issue description is missing");
      }

      if (!StringUtils.hasText(issue.getSuggestion())) {
        reflectionResult.addProblem("Issue suggestion is missing");
      }

      if (issue.getLineNumber() == null || issue.getLineNumber() <= 0) {
        reflectionResult.addProblem("Issue lineNumber is invalid");
      }

      if (issue.getSide() == null) {
        reflectionResult.addProblem("Issue diff side is missing");
      }

      if (!StringUtils.hasText(issue.getFileName()) || issue.getFileName().contains("..")) {
        reflectionResult.addProblem("Issue contains invalid fileName");
      }
    }
  }
}
