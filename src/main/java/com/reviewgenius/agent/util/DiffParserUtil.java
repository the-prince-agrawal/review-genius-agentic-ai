package com.reviewgenius.agent.util;

import com.reviewgenius.agent.model.DiffFile;
import com.reviewgenius.agent.model.DiffHunk;
import com.reviewgenius.agent.model.DiffLine;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DiffParserUtil {
  private static final Pattern HUNK_PATTERN = Pattern.compile("@@ -(\\d+)(?:,(\\d+))? \\+(\\d+)(?:,(\\d+))? @@");
  private DiffParserUtil() {
  }

  public static List<DiffFile> parse(String rawDiff) {
    List<DiffFile> files = new ArrayList<>();
    String[] lines = rawDiff.split("\\R");

    DiffFile currentFile = null;
    DiffHunk currentHunk = null;

    int oldLine = 0;
    int newLine = 0;
    int diffPosition = 0;

    for (String line : lines) {
      if (line.startsWith("diff --git")) {
        currentFile = new DiffFile();
        String[] parts = line.trim().split("\\s+");
        if (parts.length < 4) {
          continue;
        }
        String fileName = parts[2].replaceFirst("^a/", "");

        currentFile.setFileName(fileName);

        files.add(currentFile);

        currentHunk = null;

        continue;
      }

      if (line.startsWith("@@")) {

        currentHunk = new DiffHunk();

        currentHunk.setHeader(line);

        if (currentFile == null) {
          continue;
        }
        currentFile.getHunks().add(currentHunk);

        Matcher matcher = HUNK_PATTERN.matcher(line);

        if (!matcher.find()) {
          continue;
        }

        oldLine = Integer.parseInt(matcher.group(1));
        newLine = Integer.parseInt(matcher.group(3));

        diffPosition = 0;
        continue;
      }

      if (currentHunk == null) {
        continue;
      }

      if (line.startsWith("\\ No newline")) {
        continue;
      }

      diffPosition++;

      if (line.startsWith("+") && !line.startsWith("+++")) {

        currentHunk.getLines().add(
            DiffLine.builder()
                .type("ADDED")
                .content(line.substring(1))
                .oldLineNumber(null)
                .newLineNumber(newLine)
                .diffPosition(diffPosition)
                .build());

        newLine++;

        continue;
      }

      if (line.startsWith("-") && !line.startsWith("---")) {

        currentHunk.getLines().add(
            DiffLine.builder()
                .type("REMOVED")
                .content(line.substring(1))
                .oldLineNumber(oldLine)
                .newLineNumber(null)
                .diffPosition(diffPosition)
                .build());

        oldLine++;

        continue;
      }

      currentHunk.getLines().add(
          DiffLine.builder()
              .type("CONTEXT")
              .content(line.startsWith(" ")
                  ? line.substring(1)
                  : line)
              .oldLineNumber(oldLine)
              .newLineNumber(newLine)
              .diffPosition(diffPosition)
              .build());

      oldLine++;
      newLine++;
    }

    return files;
  }

  public static String format(List<DiffFile> files) {

    StringBuilder sb = new StringBuilder();

    for (DiffFile file : files) {

      sb.append("File: ")
          .append(file.getFileName())
          .append("\n\n");

      for (DiffHunk hunk : file.getHunks()) {

        sb.append("Hunk: ")
            .append(hunk.getHeader())
            .append("\n\n");

        for (DiffLine line : hunk.getLines()) {

          if ("CONTEXT".equals(line.getType())) {
            continue;
          }

          sb.append("[")
              .append(line.getType())
              .append("] ")
              .append("oldLine=")
              .append(line.getOldLineNumber())
              .append(", newLine=")
              .append(line.getNewLineNumber())
              .append(", position=")
              .append(line.getDiffPosition())
              .append("\n");

          sb.append(line.getContent())
              .append("\n\n");
        }
      }
    }

    return sb.toString();
  }
}