package com.reviewgenius.agent.core.act;

import com.reviewgenius.agent.core.act.registry.ActionHandlerRegistry;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class ActionExecutor {
  private final ActionHandlerRegistry registry;
  public ActionResult act(ActionType actionType, AgentContext context) {
    ActionHandler handler = registry.getActionHandler(actionType);
    if (handler == null) {
      return ActionResult.failure("No handler found", "No handler mapped for " + actionType);
    }
    return handler.execute(context);
  }
}
