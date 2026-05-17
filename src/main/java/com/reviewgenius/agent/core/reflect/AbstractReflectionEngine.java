package com.reviewgenius.agent.core.reflect;

import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.act.ActionResultStatus;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.enums.ReflectionDecision;
import com.reviewgenius.agent.enums.ReflectionType;
import com.reviewgenius.agent.model.ReflectionAIResponse;
import com.reviewgenius.agent.model.ReflectionResult;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class AbstractReflectionEngine implements ReflectionEngine {
  protected ReflectionResult buildSuccessResult(ReflectionResult reflectionResult) {
    return ReflectionResult.builder()
        .passed(true)
        .retryRecommended(false)
        .confidenceScore(0.95)
        .reflectionSummary("Reflection passed successfully")
        .decision(ReflectionDecision.ACCEPT)
        .reflectionType(reflectionResult.getReflectionType())
        .detectedProblems(reflectionResult.getDetectedProblems())
        .build();
  }

  protected ReflectionResult buildFailureResult(ReflectionResult reflectionResult) {
    return ReflectionResult.builder()
        .passed(false)
        .retryRecommended(
            Objects.isNull(reflectionResult.getRetryRecommended()) || reflectionResult.getRetryRecommended())
        .confidenceScore(getConfidenceScore(reflectionResult))
        .reflectionSummary(getReflectionSummary(reflectionResult))
        .decision(ReflectionDecision.RETRY)
        .reflectionType(reflectionResult.getReflectionType())
        .detectedProblems(reflectionResult.getDetectedProblems())
        .build();
  }

  protected ReflectionResult hasActionFailure(ActionType actionType, ActionResult<?> result,
      ReflectionResult reflectionResult) {
    if (Objects.isNull(result) || ActionResultStatus.FAILURE.equals(result.getStatus())) {
      reflectionResult.addProblem(buildFailureMessage(actionType, result));
      return buildFailureResult(reflectionResult);
    }
    return null;
  }

  protected String buildFailureMessage(ActionType actionType, ActionResult<?> result) {
    String actionName = actionType.name();
    String resultMessage = result != null ? result.getMessage() : null;
    String resultData = result != null && result.getData() != null
        ? result.getData().toString()
        : null;
    return String.format("%s action failed with message='%s' and data='%s'", actionName, resultMessage, resultData);
  }

  protected ReflectionResult buildReflectionResult(ReflectionAIResponse aiResponse,
      ReflectionType reflectionType, String engineName) {

    return ReflectionResult.builder()
        .passed(CollectionUtils.isEmpty(aiResponse.getDetectedProblems()))
        .retryRecommended(aiResponse.isRetryRecommended())
        .confidenceScore(aiResponse.getConfidenceScore())
        .reflectionSummary(aiResponse.getReflectionSummary())
        .detectedProblems(aiResponse.getDetectedProblems())
        .decision(buildDecision(aiResponse.getDetectedProblems()))
        .reflectionType(reflectionType)
        .reflectionEngine(engineName)
        .build();
  }

  private static String getReflectionSummary(ReflectionResult reflectionResult) {
    return StringUtils.hasText(reflectionResult.getReflectionSummary())
        ? reflectionResult.getReflectionSummary()
        : "Reflection detected problems";
  }

  private static double getConfidenceScore(ReflectionResult reflectionResult) {
    return Objects.isNull(reflectionResult.getConfidenceScore()) ? 0.35 : reflectionResult.getConfidenceScore();
  }

  private static ReflectionDecision buildDecision(List<String> detectedProblems) {
    return CollectionUtils.isEmpty(detectedProblems)
        ? ReflectionDecision.ACCEPT
        : ReflectionDecision.FAIL;
  }

  protected ReflectionResult buildEngineFailureResult(
      String errorMessage, ReflectionType reflectionType, String engineName) {
    return ReflectionResult.builder()
        .passed(false)
        .retryRecommended(false)
        .confidenceScore(0.0)
        .reflectionSummary("Semantic Reflection agent execution failed")
        .decision(ReflectionDecision.FAIL)
        .reflectionType(reflectionType)
        .reflectionEngine(engineName)
        .detectedProblems(new ArrayList<>(List.of(errorMessage)))
        .build();
  }

}
