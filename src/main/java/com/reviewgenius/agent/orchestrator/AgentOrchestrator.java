package com.reviewgenius.agent.orchestrator;

import com.reviewgenius.agent.core.act.ActionExecutor;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.reflect.ReflectionService;
import com.reviewgenius.agent.core.retry.RetryDecision;
import com.reviewgenius.agent.core.retry.RetryEngine;
import com.reviewgenius.agent.core.think.ThinkEngine;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.enums.ReflectionDecision;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReflectionResult;
import com.reviewgenius.agent.model.ReviewRequestDto;
import com.reviewgenius.agent.observability.execution.AgentExecutionResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class AgentOrchestrator {
  private final ThinkEngine thinkEngine;
  private final ActionExecutor actionExecutor;
  private final RetryEngine retryEngine;
  private final ReflectionService reflectionService;

  public AgentExecutionResult runAgent(ReviewRequestDto input, boolean debug) {
    long startTime = System.currentTimeMillis();
    AgentContext context = OrchestratorMapper.buildContext(input);
    executeWorkflow(context);
    long totalExecutionTimeMs = System.currentTimeMillis() - startTime;
    return OrchestratorMapper.buildExecutionResult(context, totalExecutionTimeMs, debug);
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
      ReflectionResult reflectionResult = reflect(actionType, actionResult, context);
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

      if (shouldStopExecution(context, reflectionResult)) {
        return;
      }
    } while (retry);
  }

  private ActionResult<?> executeAction(ActionType actionType, AgentContext context) {
    log.info("Executing action={}", actionType);
    return actionExecutor.act(actionType, context);
  }

  private ReflectionResult reflect(ActionType actionType, ActionResult<?> result, AgentContext context) {
    List<ReflectionResult> reflectionResults = reflectionService.executeReflection(actionType, result, context);
    attachReflectionResultToLatestExecution(context, reflectionResults);
    if (CollectionUtils.isEmpty(reflectionResults)) {
      return null;
    }
    return reflectionResults.getLast();
  }

  private RetryDecision evaluateRetry(ActionType actionType, ReflectionResult reflectionResult, AgentContext context) {
    return retryEngine.evaluateRetry(actionType, reflectionResult, context);
  }

  private boolean isRetryAllowed(RetryDecision retryDecision) {
    return retryDecision == RetryDecision.RETRY_ALLOWED;
  }

  private boolean shouldStopWorkflow(ReflectionResult reflectionResult, RetryDecision retryDecision) {
    // retry exhausted so stop the complete workflow and exit.
    // TODO fix this correctly
    return reflectionResult != null
        && retryDecision == RetryDecision.RETRY_DENIED
        && (reflectionResult.getDecision() == ReflectionDecision.RETRY
            ||
            reflectionResult.getDecision() == ReflectionDecision.FAIL);
  }

  private boolean shouldStopExecution(AgentContext context, ReflectionResult reflectionResult) {
    // TODO check this as we are not where setting the Reflection decision as FAIL.
    return context.isCompleted() ||
        (reflectionResult != null && reflectionResult.getDecision() == ReflectionDecision.FAIL);

  }

  private void attachReflectionResultToLatestExecution(AgentContext context, List<ReflectionResult> reflectionResults) {
    if (context.getExecutionHistories().isEmpty()) {
      return;
    }
    int lastIndex = context.getExecutionHistories().size() - 1;
    context.getExecutionHistories().get(lastIndex).setReflectionResult(reflectionResults);
  }
}
