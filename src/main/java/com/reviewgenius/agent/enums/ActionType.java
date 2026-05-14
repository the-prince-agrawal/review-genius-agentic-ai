package com.reviewgenius.agent.enums;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ActionType {
  FETCH_PR(false), PARSE_DIFF(false), ANALYZE_CODE(true), GENERATE_REVIEW(false), DONE(false);

  private boolean isActionTypeRetryAble;
}
