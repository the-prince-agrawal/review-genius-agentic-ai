package com.reviewgenius.agent.orchestrator;

import com.reviewgenius.agent.core.act.ActionExecutor;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.act.ActionResultStatus;
import com.reviewgenius.agent.core.observe.ObservationHandler;
import com.reviewgenius.agent.core.think.ThinkEngine;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.enums.ObservationStatus;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReviewRequestDto;
import com.reviewgenius.agent.observability.execution.AgentExecutionResult;
import com.reviewgenius.agent.observability.trace.TraceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@Slf4j
public class AgentOrchestrator {

  @Value("${agent.maxSteps:5}")
  private int maxSteps;
  private final ThinkEngine thinkEngine;
  private final ActionExecutor actionExecutor;
  private final ObservationHandler observationHandler;

  public AgentOrchestrator(ThinkEngine thinkEngine, ActionExecutor actionExecutor,
      ObservationHandler observationHandler) {
    this.thinkEngine = thinkEngine;
    this.actionExecutor = actionExecutor;
    this.observationHandler = observationHandler;
  }

  public AgentExecutionResult runAgent(ReviewRequestDto input) {
    long startTime = System.currentTimeMillis();
    AgentContext context = buildContext(input);
    executeSteps(context);
    long totalExecutionTimeMs = System.currentTimeMillis() - startTime;
    return buildExecutionResult(context, totalExecutionTimeMs);
  }

  private void executeSteps(AgentContext context) {
    for (int step = 0; step < maxSteps; step++) {
      ActionType actionType = thinkEngine.think(context);
      ActionResult<?> result = actionExecutor.act(actionType, context);
      ObservationStatus observation = observationHandler.observe(actionType, context);
      logStep(context, step, actionType, result, observation);
      if (result.getStatus() == ActionResultStatus.FAILURE) {
        // context.getSteps().add("Execution stopped due to failure at step " + (step + 1));
        break;
      }
      if (context.isCompleted()) {
        break;
      }
    }
  }

  private static void logStep(AgentContext context, int step, ActionType actionType, ActionResult result,
      ObservationStatus observation) {
    String stepData = String.format(
        "Step %d | Thought=%s | Action=%s | Status=%s",
        step + 1,
        actionType,
        result.getMessage(),
        observation);
    log.debug(stepData);
  }

  private static AgentContext buildContext(ReviewRequestDto input) {
    return AgentContext.builder()
        .executionHistories(new ArrayList<>())
        .inputDto(input)
        .correlationId(TraceContext.getCorrelationId())
        .completed(false)
        .build();
  }

  private AgentExecutionResult buildExecutionResult(AgentContext context, long totalExecutionTimeMs) {
    return AgentExecutionResult.builder()
        .finalReview(context.getReview())
        .executionHistories(context.getExecutionHistories())
        .overallStatus(getOverallStatus(context))
        .totalExecutionTimeMs(totalExecutionTimeMs)
        .build();
  }

  private ActionResultStatus getOverallStatus(AgentContext context) {
    boolean hasFailure = context.getExecutionHistories()
        .stream().anyMatch(history -> history.getStatus() == ActionResultStatus.FAILURE);
    return hasFailure
        ? ActionResultStatus.FAILURE
        : ActionResultStatus.SUCCESS;
  }
}
