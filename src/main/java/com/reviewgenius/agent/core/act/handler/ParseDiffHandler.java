package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.DiffFile;
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
      String parsedDiff = DiffParserUtil.format(diffFiles);
      validateParsedDiff(parsedDiff);
      context.setParsedDiff(parsedDiff);
      return ActionResult.success("Diff parsed successfully", parsedDiff);
    } catch (Exception ex) {
      log.error("Error while parsing diff", ex);
      return ActionResult.failure("Failed to parse diff", ex.getMessage());
    }
  }

  private void validateParsedDiff(String parsedDiff) {
    if (!StringUtils.hasText(parsedDiff)) {
      throw new IllegalStateException("Parsed diff is empty");
    }

    String normalized = parsedDiff.trim();
    // Detect placeholder / useless formatted output
    if ("----------------------".equals(normalized)) {
      throw new IllegalStateException("Parsed diff does not contain meaningful content");
    }

    boolean hasFile = normalized.contains("File:");
    boolean hasAdded = normalized.contains("Added:");
    boolean hasRemoved = normalized.contains("Removed:");

    // Must contain at least one file
    if (!hasFile) {
      throw new IllegalStateException(
          "No files detected in parsed diff");
    }

    // Must contain actual code changes
    if (!hasAdded && !hasRemoved) {
      throw new IllegalStateException(
          "No code changes detected in parsed diff");
    }

    String[] lines = normalized.split("\n");
    boolean hasActualContent = false;

    for (String line : lines) {
      String trimmed = line.trim();
      if (trimmed.startsWith("- ") && trimmed.length() > 2) {
        hasActualContent = true;
        break;
      }
    }

    if (!hasActualContent) {
      throw new IllegalStateException("Parsed diff contains no meaningful code lines");
    }
  }

  @Override
  public ActionType getType() {
    return PARSE_DIFF;
  }
}
