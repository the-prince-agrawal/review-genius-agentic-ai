package com.reviewgenius.agent.core.think.prompt;

import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class PromptBuilder {

  public String buildCodeReviewPrompt(String diff, String reviewType, PromptVersion version) {
    if (diff == null || diff.isBlank()) {
      return "No code diff provided.";
    }
    PromptTemplate template = getPromptTemplate(version);
    return template.getTemplate()
        .formatted(reviewType, diff);
  }

  private PromptTemplate getPromptTemplate(PromptVersion version) {
    return Arrays.stream(PromptTemplate.values())
        .filter(template -> template.getVersion() == version)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unsupported prompt version: " + version));
  }
}