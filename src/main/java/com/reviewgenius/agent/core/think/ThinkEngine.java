package com.reviewgenius.agent.core.think;

import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import static com.reviewgenius.agent.enums.ActionType.*;

@Service
public class ThinkEngine {
  public ActionType think(AgentContext context) {

    if (context.getRawDiff() == null) {
      return FETCH_PR;
    }

    if (context.getParsedDiff() == null) {
      return PARSE_DIFF;
    }

    if (context.getAnalysis() == null) {
      return ANALYZE_CODE;
    }

    if (!CollectionUtils.isEmpty(context.getIssues())) {
      return GENERATE_REVIEW;
    }

    context.setCompleted(true);
    return DONE;
  }
}
