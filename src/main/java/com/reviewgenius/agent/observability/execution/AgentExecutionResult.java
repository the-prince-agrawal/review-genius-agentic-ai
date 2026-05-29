/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.observability.execution;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.reviewgenius.agent.core.act.ActionResultStatus;
import com.reviewgenius.agent.model.CodeReviewResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AgentExecutionResult {
  private CodeReviewResponseDto finalReview;
  private List<ActionExecutionHistory> executionHistories;
  private ActionResultStatus overallStatus;
  private Long totalExecutionTimeMs;
}
