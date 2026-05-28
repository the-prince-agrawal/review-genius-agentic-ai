package com.reviewgenius.agent.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewgenius.agent.model.Issue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.reviewgenius.agent.Constants.LINE_SEPARATOR;
import static com.reviewgenius.agent.Constants.NEW_LINE;

@Component
@RequiredArgsConstructor
public class IssueParserUtil {

  private final ObjectMapper objectMapper;
  public List<Issue> parseIssues(String json) {
    try {
      return objectMapper.readValue(cleanJson(json), new TypeReference<List<Issue>>() {
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

  public static List<String> splitParsedDiffIntoChunks(String parsedDiff, int chunkSize) {
    List<String> chunks = new ArrayList<>();
    if (!StringUtils.hasText(parsedDiff)) {
      return chunks;
    }
    if (chunkSize <= 0) {
      throw new IllegalArgumentException("Chunk size must be greater than zero");
    }

    String[] sections = parsedDiff.split("(?=File: )");
    StringBuilder currentChunk = new StringBuilder();
    for (String section : sections) {
      if (!StringUtils.hasText(section)) {
        continue;
      }

      String formattedSection = section.trim() + NEW_LINE + LINE_SEPARATOR + NEW_LINE;
      if (formattedSection.length() > chunkSize) {
        if (currentChunk.length() > 0) {
          chunks.add(currentChunk.toString().trim());
          currentChunk = new StringBuilder();
        }
        chunks.add(formattedSection.trim());
        continue;
      }
      if (currentChunk.length() + formattedSection.length() > chunkSize) {
        chunks.add(currentChunk.toString().trim());
        currentChunk = new StringBuilder();
      }
      currentChunk.append(formattedSection);
    }
    if (currentChunk.length() > 0) {
      chunks.add(currentChunk.toString().trim());
    }
    return chunks;
  }
}