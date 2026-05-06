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

  public String runAgent(ReviewRequestDto input) {
    AgentContext context = buildContext(input);
    executeSteps(context);
    return context.getReview();
  }

  private void executeSteps(AgentContext context) {
    for (int step = 0; step < maxSteps; step++) {
      ActionType actionType = thinkEngine.think(context);
      ActionResult result = actionExecutor.act(actionType, context);
      ObservationStatus observation = observationHandler.observe(actionType, context);
      logStep(context, step, actionType, result, observation);
      if (result.getStatus() == ActionResultStatus.FAILURE) {
        context.getSteps().add("Execution stopped due to failure at step " + (step + 1));
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
    context.getSteps().add(stepData);
    log.debug(stepData);
  }

  private static AgentContext buildContext(ReviewRequestDto input) {
    return AgentContext
        .builder()
        .steps(new ArrayList<>())
        .inputDto(input)
        .completed(false)
        .build();
  }
}
