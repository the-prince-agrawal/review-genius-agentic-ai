package com.reviewgenius.agent.core.observe;

import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.enums.ObservationStatus;
import com.reviewgenius.agent.model.AgentContext;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static com.reviewgenius.agent.enums.ObservationStatus.*;

@Service
public class ObservationHandler {

  public ObservationStatus observe(ActionType action, AgentContext context) {
    return switch (action) {
      case FETCH_PR -> Objects.nonNull(context.getRawDiff())
          ? SUCCESS
          : FAILED;

      case PARSE_DIFF -> StringUtils.hasText(context.getParsedDiff())
          ? SUCCESS
          : FAILED;

      case ANALYZE_CODE -> StringUtils.hasText(context.getAnalysis())
          ? SUCCESS
          : FAILED;

      case GENERATE_REVIEW -> Objects.nonNull(context.getReview())
          ? DONE
          : FAILED;

      default -> UNKNOWN;
    };
  }
}
