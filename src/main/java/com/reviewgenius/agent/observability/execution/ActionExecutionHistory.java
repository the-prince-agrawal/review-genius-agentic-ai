package com.reviewgenius.agent.observability.execution;

import com.reviewgenius.agent.core.act.ActionResultStatus;
import com.reviewgenius.agent.enums.ActionType;
import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionExecutionHistory {
  private ActionType actionType;
  private ActionResultStatus status;
  private Instant startedAt;
  private Instant completedAt;
  private long executionTimeMs;
  private String summary;
  private String errorMessage;
}
