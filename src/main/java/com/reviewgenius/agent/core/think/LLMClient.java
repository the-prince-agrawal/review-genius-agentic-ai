package com.reviewgenius.agent.core.think;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.reviewgenius.agent.util.CommonUtil.getMockResponse;

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
      return getMockResponse("static/mock-code-review-response.json");
    }
    return chatClient.prompt()
        .user(prompt)
        .call()
        .content();
  }
}
