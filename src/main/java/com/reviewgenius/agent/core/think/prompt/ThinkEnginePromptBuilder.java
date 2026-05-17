package com.reviewgenius.agent.core.think.prompt;

import com.reviewgenius.agent.model.AgentContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;

import static com.reviewgenius.agent.enums.ActionType.ANALYZE_CODE;

@Service
@Slf4j
public class ThinkEnginePromptBuilder {
  public String buildCodeReviewPrompt(AgentContext context, String diffChunk) {
    if (!StringUtils.hasText(diffChunk)) {
      return "No code diff provided.";
    }
    Integer retryCount = context.getRetryCounts().getOrDefault(ANALYZE_CODE, 0);
    ThinkEnginePromptVersion version = ThinkEnginePromptVersion.fromRetryAttempt(retryCount);
    ThinkEnginePromptTemplate template = getPromptTemplate(version);
    context.setThinkEnginePromptVersion(version);
    log.info("Using prompt version: {}", version);
    return template.getTemplate().formatted(context.getInputDto().getReviewType(), diffChunk);
  }

  private ThinkEnginePromptTemplate getPromptTemplate(ThinkEnginePromptVersion version) {
    return Arrays.stream(ThinkEnginePromptTemplate.values())
        .filter(template -> template.getVersion() == version)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unsupported prompt version: " + version));
  }
}