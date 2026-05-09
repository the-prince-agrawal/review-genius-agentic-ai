package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.util.DiffParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import static com.reviewgenius.agent.core.act.ActionResultStatus.SUCCESS;
import static com.reviewgenius.agent.enums.ActionType.PARSE_DIFF;

@Service
@Slf4j
public class ParseDiffHandler implements ActionHandler {
  @Override
  public ActionResult<String> execute(AgentContext context) {
    try {
      log.info("Parsing GitHub diff");
      String parsedDiff = DiffParserUtil.parse(context.getRawDiff());
      validateParsedDiff(parsedDiff);
      context.setParsedDiff(parsedDiff);
      log.info("Diff parsed successfully");
      return ActionResult.success("Diff parsed successfully", parsedDiff);
    } catch (Exception ex) {
      log.error("Error while parsing diff", ex);
      return ActionResult.failure("Failed to parse diff", ex.getMessage());
    }
  }

  private void validateParsedDiff(String parsedDiff) {
    if (!StringUtils.hasText(parsedDiff)) {
      throw new IllegalStateException(
          "Parsed diff is empty");
    }
  }

  @Override
  public ActionType getType() {
    return PARSE_DIFF;
  }
}
