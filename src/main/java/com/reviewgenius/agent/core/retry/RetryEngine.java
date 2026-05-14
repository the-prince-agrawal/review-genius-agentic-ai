package com.reviewgenius.agent.core.retry;

import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReflectionResult;

public interface RetryEngine {
  RetryDecision evaluateRetry(ActionType actionType, ReflectionResult reflectionResult, AgentContext context);
}
