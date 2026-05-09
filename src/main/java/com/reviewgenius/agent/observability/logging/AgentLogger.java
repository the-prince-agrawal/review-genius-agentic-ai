package com.reviewgenius.agent.observability.logging;

import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AgentLogger {

  public void logStepStart(ActionType actionType, AgentContext context) {
    log.info("[correlationId={}] Starting action: {}", context.getCorrelationId(), actionType);
  }

  public void logStepCompletion(ActionType actionType, ActionResult<?> result, long executionTimeMs,
      AgentContext context) {
    log.info(
        "[correlationId={}] Completed action: {} | status={} | executionTime={} ms", context.getCorrelationId(),
        actionType, result.getStatus(), executionTimeMs);
  }

  public void logStepFailure(ActionType actionType, Exception ex, AgentContext context) {
    log.error("[correlationId={}] Action failed: {}", context.getCorrelationId(), actionType, ex);
  }
}
