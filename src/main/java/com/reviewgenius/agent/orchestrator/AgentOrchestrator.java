package com.reviewgenius.agent.orchestrator;

import com.reviewgenius.agent.core.act.ActionExecutor;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.observe.ObservationHandler;
import com.reviewgenius.agent.core.think.ThinkEngine;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReviewRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
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
    AgentContext context = AgentContext
        .builder()
        .steps(new ArrayList<>())
        .inputDto(input)
        .completed(false)
        .build();

    for (int i = 0; i < maxSteps; i++) {
      ActionType actionType = thinkEngine.think(context);
      ActionResult action = actionExecutor.act(actionType, context);
      String observation = observationHandler.observe(actionType, context);
      context.getSteps()
          .add("Thought: " + actionType + " | Action: " + action.getMessage() + " | Observation: " + observation);
      if (context.isCompleted()) {
        break;
      }
    }
    return formatResponse(context);
  }

  private String formatResponse(AgentContext context) {
    return String.join("\n", context.getSteps());
  }
}
