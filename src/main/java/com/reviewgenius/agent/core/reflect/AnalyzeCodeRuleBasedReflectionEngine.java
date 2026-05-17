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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    Set<String> duplicateCheckSet = new HashSet<>();
    for (Issue issue : issues) {
      if (!StringUtils.hasText(issue.getFileName())) {
        reflectionResult.addProblem("Issue contains empty fileName");
      }

      if (issue.getLineNumber() <= 0) {
        reflectionResult.addProblem("Invalid line number detected");
      }

      if (issue.getSeverity() == null) {
        reflectionResult.addProblem("Severity is missing");
      }

      String issueKey = issue.getFileName() + "-" + issue.getLineNumber() + "-" + issue.getDescription();

      if (!duplicateCheckSet.add(issueKey)) {
        reflectionResult.addProblem("Duplicate issue detected");
      }
    }
  }
}
