package com.reviewgenius.agent.orchestrator;

import com.reviewgenius.agent.core.act.ActionExecutor;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.act.ActionResultStatus;
import com.reviewgenius.agent.core.think.ThinkEngine;
import com.reviewgenius.agent.enums.ActionType;
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

  public AgentOrchestrator(ThinkEngine thinkEngine, ActionExecutor actionExecutor) {
    this.thinkEngine = thinkEngine;
    this.actionExecutor = actionExecutor;
  }

  public AgentExecutionResult runAgent(ReviewRequestDto input, boolean debug) {
    long startTime = System.currentTimeMillis();
    AgentContext context = buildContext(input);
    executeSteps(context);
    long totalExecutionTimeMs = System.currentTimeMillis() - startTime;
    return buildExecutionResult(context, totalExecutionTimeMs, debug);
  }

  private void executeSteps(AgentContext context) {
    for (int step = 0; step < maxSteps; step++) {
      ActionType actionType = thinkEngine.think(context);
      ActionResult<?> result = actionExecutor.act(actionType, context);
      if (result.getStatus() == ActionResultStatus.FAILURE || context.isCompleted())
        break;
    }
  }

  private static AgentContext buildContext(ReviewRequestDto input) {
    return AgentContext.builder()
        .executionHistories(new ArrayList<>())
        .inputDto(input)
        .correlationId(TraceContext.getCorrelationId())
        .completed(false)
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
    boolean hasFailure = context.getExecutionHistories()
        .stream().anyMatch(history -> history.getStatus() == ActionResultStatus.FAILURE);
    return hasFailure ? ActionResultStatus.FAILURE : ActionResultStatus.SUCCESS;
  }
}
