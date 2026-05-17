package com.reviewgenius.agent.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewgenius.agent.model.Issue;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.reviewgenius.agent.Constants.LINE_SEPARATOR;
import static com.reviewgenius.agent.Constants.NEW_LINE;

public class IssueParserUtil {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  public static List<Issue> parseIssues(String json) {
    try {
      return OBJECT_MAPPER.readValue(cleanJson(json), new TypeReference<List<Issue>>() {
      });
    } catch (Exception ex) {
      throw new RuntimeException("Failed to parse LLM response", ex);
    }
  }

  private static String cleanJson(String response) {
    if (response == null) {
      return "";
    }
    return response
        .replace("```json", "")
        .replace("```", "")
        .trim();
  }

  public static List<String> getParsedDifferenceChunks(String parsedDiff, int chunkSize) {
    final String FILE_SEPARATOR = NEW_LINE + LINE_SEPARATOR;
    List<String> chunks = new ArrayList<>();
    String[] fileSections = parsedDiff.split(FILE_SEPARATOR);
    StringBuilder currentChunk = new StringBuilder();
    for (String section : fileSections) {
      if (!StringUtils.hasText(section)) {
        continue;
      }

      // Add separator back because split removes it
      String formattedSection = section.strip() + NEW_LINE + LINE_SEPARATOR + NEW_LINE;

      /*
       * If adding this section exceeds chunk size, finalize current chunk first.
       */
      if (currentChunk.length() + formattedSection.length() > chunkSize) {
        if (!currentChunk.isEmpty()) {
          chunks.add(currentChunk.toString().trim());
          currentChunk = new StringBuilder();
        }
      }

      currentChunk.append(formattedSection);
    }
    // Add remaining chunk
    if (!currentChunk.isEmpty()) {
      chunks.add(currentChunk.toString().trim());
    }
    return chunks;
  }
}