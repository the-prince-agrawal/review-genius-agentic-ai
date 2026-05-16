package com.reviewgenius.agent.enums;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ActionType {
  FETCH_PR(true), PARSE_DIFF(true), ANALYZE_CODE(true), GENERATE_REVIEW(false);

  private boolean isActionTypeRetryAble;
}
