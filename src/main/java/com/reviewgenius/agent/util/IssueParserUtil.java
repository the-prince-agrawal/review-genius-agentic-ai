package com.reviewgenius.agent.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewgenius.agent.model.Issue;

import java.util.List;

public class IssueParserUtil {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  public static List<Issue> parseIssues(String json) {
    try {
      String cleanedJson = cleanJson(json);
      return OBJECT_MAPPER.readValue(cleanedJson, new TypeReference<List<Issue>>() {
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
}