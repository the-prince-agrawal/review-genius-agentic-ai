package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.Issue;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

import static com.reviewgenius.agent.Constants.NEW_LINE;
import static com.reviewgenius.agent.Constants.REVIEW_GENERATED_SUCCESS_MESSAGE;
import static com.reviewgenius.agent.enums.ActionType.GENERATE_REVIEW;

@Service
public class GenerateReviewHandler implements ActionHandler {
  private static final String DEFAULT_REVIEW_MESSAGE = """
      ✅ Code Review Completed

      No major issues were identified in the code changes.
      """;
  @Override
  public ActionResult execute(AgentContext context) {
    List<Issue> issues = context.getIssues();
    if (CollectionUtils.isEmpty(issues)) {
      context.setReview(DEFAULT_REVIEW_MESSAGE);
      context.setCompleted(true);
      return getSuccessResponse(DEFAULT_REVIEW_MESSAGE, 0);
    }

    String review = getFormattedReview(issues);
    context.setReview(review);
    context.setCompleted(true);
    return getSuccessResponse(review, issues.size());
  }

  private static ActionResult getSuccessResponse(String reviewMessage, int issuesCount) {
    return ActionResult.success(
        REVIEW_GENERATED_SUCCESS_MESSAGE,
        Map.of(
            "review", reviewMessage,
            "issueCount", issuesCount));
  }

  private String getFormattedReview(List<Issue> issues) {
    StringBuilder reviewBuilder = new StringBuilder();
    reviewBuilder.append("🔍 CODE REVIEW SUMMARY\n\n");
    for (Issue issue : issues) {
      reviewBuilder.append("File       : ")
          .append(issue.getFileName())
          .append(NEW_LINE);
      reviewBuilder.append("Line       : ")
          .append(issue.getLineNumber())
          .append(NEW_LINE);
      reviewBuilder.append("Severity   : ")
          .append(issue.getSeverity())
          .append(NEW_LINE);
      reviewBuilder.append("Issue      : ")
          .append(issue.getDescription())
          .append(NEW_LINE);
      reviewBuilder.append("Suggestion : ")
          .append(issue.getSuggestion())
          .append(NEW_LINE);
      reviewBuilder.append(
          "\n----------------------------------------\n\n");
    }
    return reviewBuilder.toString();
  }

  @Override
  public ActionType getType() {
    return GENERATE_REVIEW;
  }
}
