package com.reviewgenius.agent.model;

import com.reviewgenius.agent.core.think.prompt.PromptVersion;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.observability.execution.ActionExecutionHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
// TODO create small small public static inner classes inside this class for better organization of related fields, e.g.
// DiffInfo, AnalysisInfo, etc.
public class AgentContext {
  private ReviewRequestDto inputDto;
  List<Issue> issues;
  private String rawDiff;
  private String parsedDiff;
  private String analysis;
  private CodeReviewResponseDto review;
  private boolean completed;
  private String correlationId;
  private PromptVersion promptVersion;
  private List<ActionExecutionHistory> executionHistories;
  private Map<ActionType, Integer> retryCounts;
  private boolean workflowFailed;
}