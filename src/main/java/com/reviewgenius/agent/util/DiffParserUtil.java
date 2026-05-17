package com.reviewgenius.agent.util;

import com.reviewgenius.agent.model.DiffFile;

import java.util.ArrayList;
import java.util.List;

import static com.reviewgenius.agent.Constants.LINE_SEPARATOR;
import static com.reviewgenius.agent.Constants.NEW_LINE;

public class DiffParserUtil {

  public static String parse(String diff) {

    List<DiffFile> files = new ArrayList<>();
    String[] lines = diff.split("\n");

    DiffFile currentFile = null;

    for (String line : lines) {

      line = line.trim();

      // New file starts
      if (line.startsWith("diff --git")) {

        String fileName = extractFileName(line);
        currentFile = new DiffFile(fileName);
        files.add(currentFile);
        continue;
      }

      if (currentFile == null)
        continue;

      // Skip metadata
      if (line.startsWith("index") || line.startsWith("---") || line.startsWith("+++")) {
        continue;
      }

      // Added line
      if (line.startsWith("+") && !line.startsWith("+++")) {

        String clean = line.substring(1).trim();
        if (!clean.isEmpty()) {
          currentFile.getAddedLines().add(clean);
        }
      }

      // Removed line
      else if (line.startsWith("-") && !line.startsWith("---")) {

        String clean = line.substring(1).trim();
        if (!clean.isEmpty()) {
          currentFile.getRemovedLines().add(clean);
        }
      }
    }

    return format(files);
  }

  private static String extractFileName(String line) {

    String[] parts = line.split(" ");
    String fullPath = parts[2]; // a/src/...

    // Fix path
    return fullPath.replaceFirst("^a/", "");
  }

  private static String format(List<DiffFile> files) {

    StringBuilder sb = new StringBuilder();

    for (DiffFile file : files) {

      sb.append("File: ").append(file.getFileName()).append("\n");

      if (!file.getAddedLines().isEmpty()) {
        sb.append("Added:\n");
        for (String line : file.getAddedLines()) {
          sb.append("- ").append(line).append("\n");
        }
      }

      if (!file.getRemovedLines().isEmpty()) {
        sb.append("Removed:\n");
        for (String line : file.getRemovedLines()) {
          sb.append("- ").append(line).append("\n");
        }
      }

      sb.append(NEW_LINE + LINE_SEPARATOR + NEW_LINE);
    }

    return sb.toString();
  }
}
