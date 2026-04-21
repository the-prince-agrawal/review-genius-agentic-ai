package com.reviewgenius.agent.core.observe;

import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import org.springframework.stereotype.Service;

import static com.reviewgenius.agent.enums.ActionType.*;

@Service
public class ObservationHandler {

  public String observe(ActionType action, AgentContext context) {
    return switch (action) {
      case FETCH_PR -> context.getRawDiff() != null ? "SUCCESS" : "FAILED";
      case PARSE_DIFF -> context.getParsedDiff() != null ? "SUCCESS" : "FAILED";
      case ANALYZE_CODE -> "PENDING_LLM";
      case GENERATE_REVIEW -> context.getReview() != null ? "DONE" : "FAILED";
      default -> "UNKNOWN";
    };
  }
}
