/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.core.reflect.registry.prompt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewgenius.agent.model.AgentContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReflectEnginePromptBuilder {

  private final ObjectMapper objectMapper;

  public String buildSemanticReflectionPrompt(AgentContext context) {

    try {
      String issuesJson = objectMapper.writerWithDefaultPrettyPrinter()
          .writeValueAsString(context.getIssues());

      return ReflectEnginePromptTemplate.SEMANTIC_REFLECTION_PROMPT
          .getTemplate()
          .formatted(issuesJson);

    } catch (JsonProcessingException ex) {
      throw new RuntimeException("Failed to build semantic reflection prompt", ex);
    }
  }
}