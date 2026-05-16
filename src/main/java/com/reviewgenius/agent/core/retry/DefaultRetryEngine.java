package com.reviewgenius.agent.core.retry;

import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.enums.ReflectionDecision;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReflectionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
public class DefaultRetryEngine implements RetryEngine {
  @Value("${agent.retry.maxRetries:3}")
  private int maxRetries;
  @Override
  public RetryDecision evaluateRetry(ActionType actionType, ReflectionResult reflectionResult, AgentContext context) {
    if (Objects.isNull(reflectionResult)) {
      return RetryDecision.RETRY_DENIED;
    }

    if (reflectionResult.getDecision() != ReflectionDecision.RETRY) {
      return RetryDecision.RETRY_DENIED;
    }

    if (!actionType.isActionTypeRetryAble()) {
      log.warn("Retry denied. Action is not retry-able: {}", actionType);
      return RetryDecision.RETRY_DENIED;
    }

    int currentRetryCount = context.getRetryCounts().getOrDefault(actionType, 0);
    if (currentRetryCount >= maxRetries) {
      log.warn("Retry denied. Max retries exceeded for action={}", actionType);
      return RetryDecision.RETRY_DENIED;
    }

    context.getRetryCounts().put(actionType, currentRetryCount + 1);
    log.info("Retry allowed for action={} retryCount={}", actionType, currentRetryCount + 1);
    return RetryDecision.RETRY_ALLOWED;
  }
}
