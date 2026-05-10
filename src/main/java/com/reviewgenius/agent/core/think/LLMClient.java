package com.reviewgenius.agent.core.think;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class LLMClient {
  private final ChatClient chatClient;

  @Value("${agent.llm.client.disabled:false}")
  private boolean isLLMClientDisabled;
  public LLMClient(ChatClient.Builder builder) {
    this.chatClient = builder.build();
  }

  public String getResponse(String prompt) {
    if (isLLMClientDisabled) {
      return getDummyResponse();
    }
    return chatClient.prompt()
        .user(prompt)
        .call()
        .content();
  }

  private String getDummyResponse() {
    try {
      return new String(new ClassPathResource("static/DummyLLMResponse.json").getInputStream().readAllBytes());
    } catch (IOException ex) {
      throw new RuntimeException("Failed to load dummy LLM response", ex);
    }
  }
}
