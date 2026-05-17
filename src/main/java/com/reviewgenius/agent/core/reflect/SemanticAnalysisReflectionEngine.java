package com.reviewgenius.agent.core.reflect;

import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.agent.SemanticReflectionAgent;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReflectionAIResponse;
import com.reviewgenius.agent.model.ReflectionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static com.reviewgenius.agent.enums.ReflectionType.SEMANTIC;

@Slf4j
@Component
@RequiredArgsConstructor
public class SemanticAnalysisReflectionEngine extends AbstractReflectionEngine {

  private final SemanticReflectionAgent reflectionAgent;
  @Override
  public ReflectionResult reflect(ActionType actionType, ActionResult<?> result, AgentContext context) {
    ReflectionAIResponse agentResponse = null;
    try {
      agentResponse = reflectionAgent.getReflectionResult(actionType, context);
    } catch (Exception ex) {
      log.error("Semantic reflection failed : {}", ex.getMessage(), ex);
      return buildEngineFailureResult(ex.getMessage(), SEMANTIC, getClass().getSimpleName());
    }

    ReflectionResult reflectionResult = buildReflectionResult(agentResponse, SEMANTIC,
        getClass().getSimpleName());

    if (CollectionUtils.isEmpty(reflectionResult.getDetectedProblems())) {
      return buildSuccessResult(reflectionResult);
    } else {
      return buildFailureResult(reflectionResult);
    }
  }
}