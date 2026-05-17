package com.reviewgenius.agent.core.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reviewgenius.agent.core.reflect.registry.prompt.ReflectEnginePromptBuilder;
import com.reviewgenius.agent.core.think.LLMClient;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReflectionAIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.reviewgenius.agent.util.CommonUtil.getSemanticReflectionMockResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class SemanticReflectionAgent {

  @Value("${agent.llm.client.semantic-reflection.disabled:false}")
  private boolean isSemanticReflectionLLMClientDisabled;

  private final LLMClient llmClient;
  private final ReflectEnginePromptBuilder promptBuilder;
  private final ObjectMapper objectMapper = new ObjectMapper();

  public ReflectionAIResponse getReflectionResult(ActionType actionType, AgentContext context)
      throws JsonProcessingException {
    String prompt = promptBuilder.buildSemanticReflectionPrompt(context);
    log.info("Starting semantic reflection for actionType={}", actionType);
    String response = callLLM(prompt);
    return objectMapper.readValue(response, ReflectionAIResponse.class);
  }

  private String callLLM(String prompt) {
    if (isSemanticReflectionLLMClientDisabled) {
      return getSemanticReflectionMockResponse("static/semantic-reflection-mock-response.json");
    }
    return llmClient.getResponse(prompt);
  }
}
