package com.reviewgenius.agent.core.act;

import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;

public interface ActionHandler {
  ActionResult execute(AgentContext context);

  ActionType getType();
}
