package com.reviewgenius.agent.util;

import com.reviewgenius.agent.model.Issue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Slf4j
public class CommonUtil {

  public static String getAnalyzeCodeMockResponse(String fileName) {
    try {
      log.info("Loading dummy response from file: {}", fileName);
      return new String(new ClassPathResource(fileName).getInputStream().readAllBytes());
    } catch (IOException ex) {
      log.error("Failed to load dummy response from file: {}", fileName, ex);
      throw new RuntimeException("Failed to load dummy LLM response", ex);
    }
  }

  public static String getSemanticReflectionMockResponse(String fileName) {
    try {
      log.info("Loading dummy response from file: {}", fileName);
      return new String(new ClassPathResource(fileName).getInputStream().readAllBytes());
    } catch (IOException ex) {
      log.error("Failed to load dummy response from file: {}", fileName, ex);
      throw new RuntimeException("Failed to load dummy LLM response", ex);
    }
  }

  public static String buildCommentBody(Issue issue) {
    return """
        Severity: %s

        %s

        Suggestion:
        %s
        """.formatted(
        issue.getSeverity(),
        issue.getDescription(),
        issue.getSuggestion());
  }
}
