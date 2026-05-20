package com.reviewgenius.agent.util;

import com.reviewgenius.agent.model.DiffFile;

import java.util.ArrayList;
import java.util.List;

public class DiffParserUtil {

  public static List<DiffFile> parse(String diff) {
    List<DiffFile> files = new ArrayList<>();
    String[] lines = diff.split("\n");

    DiffFile currentFile = null;

    for (String originalLine : lines) {

      String line = originalLine;

      // New file starts
      if (line.startsWith("diff --git")) {

        String fileName = extractFileName(line);

        currentFile = new DiffFile(fileName);

        files.add(currentFile);

        continue;
      }

      if (currentFile == null) {
        continue;
      }

      // Preserve hunk metadata
      if (line.startsWith("@@")) {
        currentFile.getHunks().add(line.trim());
        continue;
      }

      // Skip metadata
      if (line.startsWith("index")
          || line.startsWith("---")
          || line.startsWith("+++")) {
        continue;
      }

      // Preserve raw diff line
      if ((line.startsWith("+") && !line.startsWith("+++"))
          || (line.startsWith("-") && !line.startsWith("---"))
          || line.startsWith(" ")) {

        currentFile.getRawDiffLines().add(line);
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
    return files;
  }

  private static String extractFileName(String line) {

    String[] parts = line.split(" ");
    String fullPath = parts[2]; // a/src/...

    // Fix path
    return fullPath.replaceFirst("^a/", "");
  }

  public static String format(List<DiffFile> files) {
    StringBuilder sb = new StringBuilder();

    for (DiffFile file : files) {

      sb.append("File: ")
          .append(file.getFileName())
          .append("\n\n");

      if (!file.getHunks().isEmpty()) {

        sb.append("Hunks:\n");

        for (String hunk : file.getHunks()) {
          sb.append(hunk).append("\n");
        }

        sb.append("\n");
      }

      if (!file.getAddedLines().isEmpty()) {

        sb.append("Added:\n");

        for (String line : file.getAddedLines()) {
          sb.append("+ ").append(line).append("\n");
        }

        sb.append("\n");
      }

      if (!file.getRemovedLines().isEmpty()) {

        sb.append("Removed:\n");

        for (String line : file.getRemovedLines()) {
          sb.append("- ").append(line).append("\n");
        }

        sb.append("\n");
      }

      if (!file.getRawDiffLines().isEmpty()) {

        sb.append("RawDiff:\n");

        for (String raw : file.getRawDiffLines()) {
          sb.append(raw).append("\n");
        }

        sb.append("\n");
      }

      sb.append("--------------------------------------\n\n");
    }

    return sb.toString();
  }
}
