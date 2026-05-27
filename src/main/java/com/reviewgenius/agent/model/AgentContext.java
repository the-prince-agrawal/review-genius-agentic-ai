package com.reviewgenius.agent.model;

import com.reviewgenius.agent.core.think.prompt.ThinkEnginePromptVersion;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.observability.execution.ActionExecutionHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
//TODO create small small public static inner classes inside this class for better organization of related field
public class AgentContext {
  private ReviewRequestDto inputDto;
  private boolean isPRStateValidated;
  private List<Issue> issues;
  private String rawDiff;
  private String parsedDiff;
  private List<String> analysis;
  private CodeReviewResponseDto review;
  private boolean completed;
  private String correlationId;
  private ThinkEnginePromptVersion thinkEnginePromptVersion;
  private List<ActionExecutionHistory> executionHistories;
  private Map<ActionType, Integer> retryCounts;
  private boolean workflowFailed;
  private boolean prCommentsAdded;
  private PullRequestMetadata pullRequestMetadata;
}