package com.reviewgenius.agent.service;

import com.reviewgenius.agent.enums.IssueDiffSide;
import com.reviewgenius.agent.model.DiffFile;
import com.reviewgenius.agent.model.DiffHunk;
import com.reviewgenius.agent.model.DiffLine;
import com.reviewgenius.agent.model.Issue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class IssuePositionResolver {

  public void resolveDiffPositions(List<Issue> issues, List<DiffFile> diffFiles) {
    if (issues == null || issues.isEmpty()) {
      return;
    }
    if (diffFiles == null || diffFiles.isEmpty()) {
      return;
    }

    for (Issue issue : issues) {

      if (issue == null) {
        continue;
      }

      for (DiffFile file : diffFiles) {

        if (!Objects.equals(
            file.getFileName(),
            issue.getFileName())) {
          continue;
        }

        resolveIssuePosition(issue, file);
        break;
      }
    }
  }

  private void resolveIssuePosition(Issue issue, DiffFile file) {

    for (DiffHunk hunk : file.getHunks()) {

      for (DiffLine line : hunk.getLines()) {

        if (!isMatchingLine(issue, line)) {
          continue;
        }

        if (issue.getSide() == IssueDiffSide.RIGHT
            && !"ADDED".equals(line.getType())) {
          continue;
        }

        if (issue.getSide() == IssueDiffSide.LEFT
            && !"REMOVED".equals(line.getType())) {
          continue;
        }

        issue.setDiffPosition(
            line.getDiffPosition());

        log.debug(
            "Resolved diff position. file={}, lineNumber={}, diffPosition={}",
            issue.getFileName(),
            issue.getLineNumber(),
            line.getDiffPosition());

        return;
      }
    }

    log.warn(
        "Unable to resolve diff position. file={}, lineNumber={}, side={}",
        issue.getFileName(),
        issue.getLineNumber(),
        issue.getSide());
  }

  private boolean isMatchingLine(Issue issue, DiffLine line) {
    if (issue.getSide() == IssueDiffSide.RIGHT) {
      return Objects.equals(line.getNewLineNumber(), issue.getLineNumber());
    }
    return Objects.equals(line.getOldLineNumber(), issue.getLineNumber());
  }
}