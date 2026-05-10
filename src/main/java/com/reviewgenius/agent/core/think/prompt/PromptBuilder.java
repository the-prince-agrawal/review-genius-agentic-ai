package com.reviewgenius.agent.core.think.prompt;

import com.reviewgenius.agent.model.AgentContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Service
public class PromptBuilder {

  @Value("${agent.prompt.version:REVIEW_V1}")
  private String reviewPromptVersion;

  public String buildCodeReviewPrompt(AgentContext context) {
    if (!StringUtils.hasText(context.getParsedDiff())) {
      return "No code diff provided.";
    }
    PromptVersion version = PromptVersion.valueOf(reviewPromptVersion);
    PromptTemplate template = getPromptTemplate(version);
    context.setPromptVersion(version);
    return template.getTemplate().formatted(context.getInputDto().getReviewType(), context.getParsedDiff());
  }

  private PromptTemplate getPromptTemplate(PromptVersion version) {
    return Arrays.stream(PromptTemplate.values())
        .filter(template -> template.getVersion() == version)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unsupported prompt version: " + version));
  }
}