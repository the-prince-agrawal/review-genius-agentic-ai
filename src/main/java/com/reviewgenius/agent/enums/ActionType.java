package com.reviewgenius.agent.enums;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ActionType {
  VALIDATE_PR_STATE(false), FETCH_PR_DIFF(true), PARSE_DIFF(true), ANALYZE_CODE(true), ADD_REVIEW_COMMENTS(
      false), GENERATE_REVIEW(false);

  private boolean isActionTypeRetryAble;
}
