/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.core.think;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class LLMClient {
  private final ChatClient chatClient;
  public LLMClient(ChatClient.Builder builder) {
    this.chatClient = builder.build();
  }

  public String getResponse(String prompt) {
    return chatClient.prompt()
        .user(prompt)
        .call()
        .content();
  }
}
