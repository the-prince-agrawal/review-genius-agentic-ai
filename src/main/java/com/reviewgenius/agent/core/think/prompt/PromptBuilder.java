package com.reviewgenius.agent.core.think.prompt;

import com.reviewgenius.agent.model.AgentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;

import static com.reviewgenius.agent.enums.ActionType.ANALYZE_CODE;

@Service
@Slf4j
public class PromptBuilder {
  public String buildCodeReviewPrompt(AgentContext context, String diffChunk) {
    if (!StringUtils.hasText(diffChunk)) {
      return "No code diff provided.";
    }
    Integer retryCount = context.getRetryCounts().getOrDefault(ANALYZE_CODE, 0);
    PromptVersion version = PromptVersion.fromRetryAttempt(retryCount);
    PromptTemplate template = getPromptTemplate(version);
    context.setPromptVersion(version);
    log.info("Using prompt version: {}", version);
    return template.getTemplate().formatted(context.getInputDto().getReviewType(), diffChunk);
  }

  private PromptTemplate getPromptTemplate(PromptVersion version) {
    return Arrays.stream(PromptTemplate.values())
        .filter(template -> template.getVersion() == version)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unsupported prompt version: " + version));
  }
}