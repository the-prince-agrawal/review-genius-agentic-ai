/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.observability.logging;

import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.ReflectionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReflectionLogger {

  public void logReflection(ActionType actionType, ReflectionResult reflectionResult) {
    log.info(
        "Reflection completed: action={} decision={} passed={} confidenceScore={} problems={} ",
        actionType, reflectionResult.getDecision(),
        reflectionResult.isPassed(), reflectionResult.getConfidenceScore(),
        reflectionResult.getDetectedProblems());
  }
}