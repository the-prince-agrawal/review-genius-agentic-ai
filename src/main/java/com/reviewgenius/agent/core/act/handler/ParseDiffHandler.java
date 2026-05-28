package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.DiffFile;
import com.reviewgenius.agent.model.DiffHunk;
import com.reviewgenius.agent.model.DiffLine;
import com.reviewgenius.agent.util.DiffParserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.reviewgenius.agent.enums.ActionType.PARSE_DIFF;

@Service
@Slf4j
public class ParseDiffHandler implements ActionHandler {

  @Override
  public ActionResult<String> execute(AgentContext context) {

    try {
      List<DiffFile> diffFiles = DiffParserUtil.parse(context.getRawDiff());
      validateParsedDiff(diffFiles);
      String parsedDiff = DiffParserUtil.format(diffFiles);
      context.setParsedDiff(parsedDiff);
      return ActionResult.success("Diff parsed successfully", parsedDiff);
    } catch (Exception ex) {
      log.error("Error while parsing diff", ex);
      return ActionResult.failure("Failed to parse diff", ex.getMessage());
    }
  }

  private void validateParsedDiff(List<DiffFile> diffFiles) {
    if (diffFiles == null || diffFiles.isEmpty()) {
      throw new IllegalStateException("No diff files found");
    }

    boolean hasActualChanges = false;
    for (DiffFile file : diffFiles) {
      if (!StringUtils.hasText(file.getFileName())) {
        continue;
      }

      for (DiffHunk hunk : file.getHunks()) {
        for (DiffLine line : hunk.getLines()) {
          if ("ADDED".equals(line.getType()) || "REMOVED".equals(line.getType())) {
            hasActualChanges = true;
            if (line.getDiffPosition() == null) {
              throw new IllegalStateException("Diff position missing");
            }
          }
        }
      }
    }

    if (!hasActualChanges) {
      throw new IllegalStateException("No actual code changes found");
    }
  }

  @Override
  public ActionType getType() {
    return PARSE_DIFF;
  }
}