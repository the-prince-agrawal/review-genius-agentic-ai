/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.CodeReviewResponseDto;
import com.reviewgenius.agent.model.Issue;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.reviewgenius.agent.Constants.REVIEW_GENERATED_SUCCESS_MESSAGE;
import static com.reviewgenius.agent.enums.ActionType.GENERATE_REVIEW;

@Service
// TODO: this is for future use
// If not used, please consider removing it to reduce code complexity and maintenance overhead.
public class GenerateReviewHandler implements ActionHandler {
  @Override
  public ActionResult<?> execute(AgentContext context) {
    List<Issue> issues = context.getIssues();
    context.setCompleted(true);
    context.setReview(getCodeReviewResponseDto(issues));
    return getSuccessResponse(issues);
  }

  private CodeReviewResponseDto getCodeReviewResponseDto(List<Issue> issues) {
    return CodeReviewResponseDto.builder().issues(issues).build();
  }

  private ActionResult<List<Issue>> getSuccessResponse(List<Issue> review) {
    return ActionResult.success(REVIEW_GENERATED_SUCCESS_MESSAGE, review);
  }

  @Override
  public ActionType getType() {
    return GENERATE_REVIEW;
  }
}
