package com.reviewgenius.agent.orchestrator;

import com.reviewgenius.agent.core.act.ActionResultStatus;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.PullRequestMetadata;
import com.reviewgenius.agent.model.ReviewRequestDto;
import com.reviewgenius.agent.observability.execution.AgentExecutionResult;
import com.reviewgenius.agent.observability.trace.TraceContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class OrchestratorMapper {
  public static AgentContext buildContext(ReviewRequestDto input) {
    return AgentContext.builder()
        .executionHistories(new ArrayList<>())
        .retryCounts(new HashMap<>())
        .inputDto(input)
        .analysis(new CopyOnWriteArrayList<>())
        .correlationId(TraceContext.getCorrelationId())
        .completed(false)
        .workflowFailed(false)
        .pullRequestMetadata(PullRequestMetadata.builder().prUrl(input.getPrURL()).build())
        .prCommentsAdded(false)
        .build();
  }

  public static AgentExecutionResult buildExecutionResult(AgentContext context, long totalExecutionTimeMs,
      boolean debug) {
    return AgentExecutionResult.builder()
        .finalReview(context.getReview())
        .executionHistories(debug ? context.getExecutionHistories() : null)
        .overallStatus(getOverallStatus(context))
        .totalExecutionTimeMs(totalExecutionTimeMs)
        .build();
  }

  private static ActionResultStatus getOverallStatus(AgentContext context) {
    return context.isWorkflowFailed() ? ActionResultStatus.FAILURE : ActionResultStatus.SUCCESS;
  }
}
