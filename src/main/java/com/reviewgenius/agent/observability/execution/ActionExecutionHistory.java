package com.reviewgenius.agent.observability.execution;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.reviewgenius.agent.core.act.ActionResultStatus;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.ReflectionResult;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionExecutionHistory {
  private ActionType actionType;
  private ActionResultStatus status;
  private Instant startedAt;
  private Instant completedAt;
  private long executionTimeMs;
  private String summary;
  private String errorMessage;
  List<ReflectionResult> reflectionResult;
}
