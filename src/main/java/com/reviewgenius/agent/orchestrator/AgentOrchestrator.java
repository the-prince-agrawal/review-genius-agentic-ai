package com.reviewgenius.agent.orchestrator;

import com.reviewgenius.agent.core.act.ActionExecutor;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.act.ActionResultStatus;
import com.reviewgenius.agent.core.reflect.ReflectionEngine;
import com.reviewgenius.agent.core.retry.RetryDecision;
import com.reviewgenius.agent.core.retry.RetryEngine;
import com.reviewgenius.agent.core.think.ThinkEngine;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.enums.ReflectionDecision;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReflectionResult;
import com.reviewgenius.agent.model.ReviewRequestDto;
import com.reviewgenius.agent.observability.execution.AgentExecutionResult;
import com.reviewgenius.agent.observability.logging.ReflectionLogger;
import com.reviewgenius.agent.observability.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

@Component
@Slf4j
public class AgentOrchestrator {
  private final ThinkEngine thinkEngine;
  private final ActionExecutor actionExecutor;
  private final ReflectionEngine reflectionEngine;
  private final ReflectionLogger reflectionLogger;
  private final RetryEngine retryEngine;

  public AgentOrchestrator(ThinkEngine thinkEngine, ActionExecutor actionExecutor, ReflectionEngine reflectionEngine,
      ReflectionLogger reflectionLogger, RetryEngine retryEngine) {
    this.thinkEngine = thinkEngine;
    this.actionExecutor = actionExecutor;
    this.reflectionEngine = reflectionEngine;
    this.reflectionLogger = reflectionLogger;
    this.retryEngine = retryEngine;
  }

  public AgentExecutionResult runAgent(ReviewRequestDto input, boolean debug) {
    long startTime = System.currentTimeMillis();
    AgentContext context = buildContext(input);
    executeWorkflow(context);
    long totalExecutionTimeMs = System.currentTimeMillis() - startTime;
    return buildExecutionResult(context, totalExecutionTimeMs, debug);
  }

  private void executeWorkflow(AgentContext context) {
    while (!context.isCompleted()) {
      ActionType nextAction = thinkEngine.think(context);
      executeActionWithRetry(nextAction, context);
    }
  }

  private void executeActionWithRetry(ActionType actionType, AgentContext context) {
    boolean retry;
    do {
      retry = false;
      ActionResult<?> actionResult = executeAction(actionType, context);
      ReflectionResult reflectionResult = reflectAction(actionType, actionResult, context);
      RetryDecision retryDecision = evaluateRetry(actionType, reflectionResult, context);

      if (shouldStopWorkflow(reflectionResult, retryDecision)) {
        context.setWorkflowFailed(true);
        log.error("Workflow stopped. Retry denied after reflection failure. actionType={}", actionType);
        context.setCompleted(true);
        return;
      }

      if (isRetryAllowed(retryDecision)) {
        log.info("Retrying action={}", actionType);
        retry = true;
      }

      if (shouldStopExecution(context, actionResult, reflectionResult)) {
        return;
      }
    } while (retry);
  }

  private ActionResult<?> executeAction(ActionType actionType, AgentContext context) {
    log.info("Executing action={}", actionType);
    return actionExecutor.act(actionType, context);
  }

  private ReflectionResult reflectAction(ActionType actionType, ActionResult<?> result, AgentContext context) {
    ReflectionResult reflectionResult = reflectionEngine.reflect(actionType, result, context);
    reflectionLogger.logReflection(actionType, reflectionResult);
    attachReflectionResultToLatestExecution(context, reflectionResult);
    return reflectionResult;
  }

  private RetryDecision evaluateRetry(ActionType actionType, ReflectionResult reflectionResult, AgentContext context) {
    return retryEngine.evaluateRetry(actionType, reflectionResult, context);
  }

  private boolean shouldStopWorkflow(ReflectionResult reflectionResult, RetryDecision retryDecision) {
    // retry exhausted so stop the complete workflow and exit.
    return reflectionResult.getDecision() == ReflectionDecision.RETRY
        && retryDecision == RetryDecision.RETRY_DENIED;
  }

  private boolean isRetryAllowed(RetryDecision retryDecision) {
    return retryDecision == RetryDecision.RETRY_ALLOWED;
  }

  private boolean shouldStopExecution(AgentContext context, ActionResult<?> result,
      ReflectionResult reflectionResult) {
    return context.isCompleted() || reflectionResult.getDecision() == ReflectionDecision.FAIL;
  }

  private AgentContext buildContext(ReviewRequestDto input) {
    return AgentContext.builder()
        .executionHistories(new ArrayList<>())
        .retryCounts(new HashMap<>())
        .inputDto(input)
        .correlationId(TraceContext.getCorrelationId())
        .completed(false)
        .workflowFailed(false)
        .build();
  }

  private AgentExecutionResult buildExecutionResult(AgentContext context, long totalExecutionTimeMs, boolean debug) {
    return AgentExecutionResult.builder()
        .finalReview(context.getReview())
        .executionHistories(debug ? context.getExecutionHistories() : null)
        .overallStatus(getOverallStatus(context))
        .totalExecutionTimeMs(totalExecutionTimeMs)
        .build();
  }

  private ActionResultStatus getOverallStatus(AgentContext context) {
    return context.isWorkflowFailed() ? ActionResultStatus.FAILURE : ActionResultStatus.SUCCESS;
  }

  private void attachReflectionResultToLatestExecution(AgentContext context, ReflectionResult reflectionResult) {
    if (context.getExecutionHistories().isEmpty()) {
      return;
    }
    int lastIndex = context.getExecutionHistories().size() - 1;
    context.getExecutionHistories().get(lastIndex).setReflectionResult(reflectionResult);
  }
}
