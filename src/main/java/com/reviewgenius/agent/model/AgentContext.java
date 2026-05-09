package com.reviewgenius.agent.model;

import com.reviewgenius.agent.observability.execution.ActionExecutionHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentContext {
  private ReviewRequestDto inputDto;
  List<Issue> issues;
  Map<String, Object> metadata;
  private String rawDiff;
  private String parsedDiff;
  private String analysis;
  private String review;
  private boolean completed;
  private String correlationId;
  private List<ActionExecutionHistory> executionHistories;
}