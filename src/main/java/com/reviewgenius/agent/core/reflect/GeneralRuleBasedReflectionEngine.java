/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.core.reflect;

import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.enums.ReflectionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReflectionResult;
import org.springframework.stereotype.Component;

@Component
public class GeneralRuleBasedReflectionEngine extends AbstractReflectionEngine {
  @Override
  public ReflectionResult reflect(ActionType actionType, ActionResult<?> result, AgentContext context) {
    ReflectionResult reflectionResult = new ReflectionResult();
    reflectionResult.setReflectionType(ReflectionType.STRUCTURAL);
    ReflectionResult failedResult = hasActionFailure(actionType, result, reflectionResult);

    if (failedResult != null) {
      return failedResult;
    }
    if (reflectionResult.getDetectedProblems().isEmpty()) {
      return buildSuccessResult(reflectionResult);
    }
    return buildFailureResult(reflectionResult);
  }
}