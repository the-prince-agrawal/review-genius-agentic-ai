package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.CodeReviewResponseDto;
import com.reviewgenius.agent.model.Issue;
import com.reviewgenius.agent.model.ReviewIssueDto;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.reviewgenius.agent.Constants.REVIEW_GENERATED_SUCCESS_MESSAGE;
import static com.reviewgenius.agent.enums.ActionType.GENERATE_REVIEW;

@Service
public class GenerateReviewHandler implements ActionHandler {
  @Override
  public ActionResult<?> execute(AgentContext context) {
    List<Issue> issues = context.getIssues();
    CodeReviewResponseDto review = getFormattedReview(issues);
    context.setReview(review);
    context.setCompleted(true);
    return getSuccessResponse(review);
  }

  private static ActionResult<CodeReviewResponseDto> getSuccessResponse(CodeReviewResponseDto review) {
    return ActionResult.success(REVIEW_GENERATED_SUCCESS_MESSAGE, review);
  }

  private CodeReviewResponseDto getFormattedReview(List<Issue> issues) {
    if (CollectionUtils.isEmpty(issues)) {
      return CodeReviewResponseDto.builder().issues(List.of()).build();
    }
    List<ReviewIssueDto> reviewIssues = issues.stream()
        .map(issue -> ReviewIssueDto.builder()
            .fileName(issue.getFileName())
            .lineNumber(issue.getLineNumber())
            .severity(issue.getSeverity())
            .description(issue.getDescription())
            .suggestion(issue.getSuggestion())
            .build())
        .toList();
    return CodeReviewResponseDto.builder()
        .issues(reviewIssues)
        .build();
  }

  @Override
  public ActionType getType() {
    return GENERATE_REVIEW;
  }
}
