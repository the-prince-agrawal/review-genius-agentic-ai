/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.observability.logging;

import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AgentLogger {

  public void logStepStart(ActionType actionType) {
    log.info("Starting action: {}", actionType);
  }

  public void logStepCompletion(ActionType actionType, ActionResult<?> result, long executionTimeMs) {
    log.info("Completed action: {} | status={} | executionTime={} ms", actionType, result.getStatus(), executionTimeMs);
  }

  public void logStepFailure(ActionType actionType, Exception ex) {
    log.error("Action failed: {}", actionType, ex);
  }
}
