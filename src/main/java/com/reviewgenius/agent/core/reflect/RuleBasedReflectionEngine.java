package com.reviewgenius.agent.core.reflect;

import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.enums.ReflectionDecision;
import com.reviewgenius.agent.enums.ReflectionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.Issue;
import com.reviewgenius.agent.model.ReflectionResult;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.reviewgenius.agent.core.act.ActionResultStatus.FAILURE;

@Component
public class RuleBasedReflectionEngine implements ReflectionEngine {
  @Override
  public ReflectionResult reflect(ActionType actionType, ActionResult<?> result, AgentContext context) {
    ReflectionResult reflectionResult = new ReflectionResult();
    reflectionResult.setReflectionType(ReflectionType.STRUCTURAL);

    if (Objects.isNull(result) || FAILURE.equals(result.getStatus())) {
      reflectionResult.addProblem(buildFailureMessage(actionType, result));
      return getFailedResult(reflectionResult);
    }

    if (actionType == ActionType.ANALYZE_CODE) {
      validateAnalyzeCode(context, reflectionResult);
    }

    if (reflectionResult.getDetectedProblems().isEmpty()) {
      return getSuccessResult(reflectionResult);
    }
    return getFailedResult(reflectionResult);
  }

  private static ReflectionResult getSuccessResult(ReflectionResult reflectionResult) {
    reflectionResult.setPassed(true);
    reflectionResult.setDecision(ReflectionDecision.ACCEPT);
    reflectionResult.setConfidenceScore(0.95);
    reflectionResult.setReflectionSummary("Reflection passed successfully");
    return reflectionResult;
  }

  private void validateAnalyzeCode(AgentContext context, ReflectionResult reflectionResult) {
    List<Issue> issues = context.getIssues();

    if (issues == null) {
      reflectionResult.addProblem("Issues list is null");
      return;
    }

    if (issues.isEmpty()) {
      reflectionResult.addProblem("Issues list is empty");
    }

    Set<String> duplicateCheckSet = new HashSet<>();
    for (Issue issue : issues) {
      if (issue.getFileName() == null || issue.getFileName().isBlank()) {
        reflectionResult.addProblem("Issue contains empty fileName");
      }

      if (issue.getLineNumber() <= 0) {
        reflectionResult.addProblem("Invalid line number detected");
      }

      if (issue.getSeverity() == null) {
        reflectionResult.addProblem("Severity is missing");
      }

      String duplicateKey = issue.getFileName()
          + "-" + issue.getLineNumber() + "-" + issue.getDescription();

      if (!duplicateCheckSet.add(duplicateKey)) {
        reflectionResult.addProblem("Duplicate issue detected");
      }
    }
  }

  private ReflectionResult getFailedResult(ReflectionResult reflectionResult) {
    reflectionResult.setPassed(false);
    reflectionResult.setRetryRecommended(true);
    reflectionResult.setDecision(ReflectionDecision.RETRY);
    reflectionResult.setConfidenceScore(0.35);
    reflectionResult.setReflectionSummary("Reflection detected problems");
    return reflectionResult;
  }

  private String buildFailureMessage(ActionType actionType, ActionResult<?> result) {
    String actionName = actionType.name();
    String resultMessage = result != null ? result.getMessage() : null;
    String resultData = result != null && result.getData() != null ? result.getData().toString() : null;
    return String.format("%s action failed with message='%s' and data='%s'", actionName, resultMessage, resultData);
  }
}
