package com.reviewgenius.agent.observability.execution;

import com.reviewgenius.agent.core.act.ActionResultStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentExecutionResult {
  private String finalReview;
  private List<ActionExecutionHistory> executionHistories;
  private ActionResultStatus overallStatus;
  private long totalExecutionTimeMs;
}
