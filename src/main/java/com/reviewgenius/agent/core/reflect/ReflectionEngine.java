package com.reviewgenius.agent.core.reflect;

import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReflectionResult;

public interface ReflectionEngine {
  ReflectionResult reflect(ActionType actionType, ActionResult<?> result, AgentContext context);
}
