package com.reviewgenius.agent.core.retry;

import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.enums.ReflectionDecision;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReflectionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.reviewgenius.agent.core.retry.RetryDecision.RETRY_DENIED;
import static com.reviewgenius.agent.enums.ReflectionDecision.RETRY;

@Component
@Slf4j
public class DefaultRetryEngine implements RetryEngine {
  @Value("${agent.retry.maxRetries:2}")
  private int maxRetries;

  @Override
  public RetryDecision evaluateRetry(ActionType actionType, ReflectionResult reflectionResult, AgentContext context) {
    int currentRetryCount = context.getRetryCounts().getOrDefault(actionType, 0);
    if (Objects.isNull(reflectionResult)
        || reflectionResult.getDecision() != RETRY
        || !actionType.isActionTypeRetryAble()
        || currentRetryCount >= maxRetries) {
      log.warn("Retry denied for action={} retryCount={}", actionType, currentRetryCount);
      return RETRY_DENIED;
    }
    context.getRetryCounts().put(actionType, currentRetryCount + 1);
    log.info("Retry allowed for action={} retryCount={}", actionType, currentRetryCount + 1);
    return RetryDecision.RETRY_ALLOWED;
  }
}
