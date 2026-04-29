package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import org.springframework.stereotype.Service;

import static com.reviewgenius.agent.core.act.ActionResultStatus.SUCCESS;
import static com.reviewgenius.agent.enums.ActionType.GENERATE_REVIEW;

@Service
public class GenerateReviewHandler implements ActionHandler {
  @Override
  public ActionResult execute(AgentContext context) {
    String review = "Generated review based on analysis and parsed diff.";
    context.setReview(review);
    return new ActionResult(SUCCESS, "Review generated", review, null);
  }

  @Override
  public ActionType getType() {
    return GENERATE_REVIEW;
  }
}
