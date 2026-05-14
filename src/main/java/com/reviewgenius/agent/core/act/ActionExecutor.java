package com.reviewgenius.agent.core.act;

import com.reviewgenius.agent.core.act.registry.ActionHandlerRegistry;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.observability.execution.ActionExecutionHistory;
import com.reviewgenius.agent.observability.logging.AgentLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@Slf4j
@RequiredArgsConstructor
public class ActionExecutor {

  private final ActionHandlerRegistry registry;
  private final AgentLogger agentLogger;

  public ActionResult<?> act(ActionType actionType, AgentContext context) {

    ActionHandler handler = registry.getActionHandler(actionType);

    if (handler == null) {
      log.error("No handler registered for action: {}", actionType);
      ActionResult<?> failureResult = ActionResult.failure("No handler found", "No handler mapped for " + actionType);
      addExecutionHistory(context, actionType, failureResult, null, null);
      return failureResult;
    }

    Instant startedAt = Instant.now();
    agentLogger.logStepStart(actionType);
    ActionResult<?> result;

    try {

      result = handler.execute(context);

    } catch (Exception ex) {
      agentLogger.logStepFailure(actionType, ex);
      result = ActionResult.failure("Unexpected action execution failure", ex.getMessage());
    }

    Instant completedAt = Instant.now();
    addExecutionHistory(context, actionType, result, startedAt, completedAt);
    agentLogger.logStepCompletion(actionType, result, Duration.between(startedAt, completedAt).toMillis());
    return result;

  }

  private static void addExecutionHistory(AgentContext context, ActionType actionType,
      ActionResult<?> result, Instant startedAt, Instant completedAt) {
    long executionTimeMs = 0;
    if (startedAt != null && completedAt != null) {
      executionTimeMs = Duration.between(startedAt, completedAt).toMillis();
      executionTimeMs = Math.max(executionTimeMs, 1);
    }

    ActionExecutionHistory history = ActionExecutionHistory.builder()
        .actionType(actionType)
        .status(result.getStatus())
        .startedAt(startedAt)
        .completedAt(completedAt)
        .executionTimeMs(executionTimeMs)
        .summary(result.getMessage())
        .errorMessage(result.getErrorMessage())
        .build();
    context.getExecutionHistories().add(history);
  }
}
