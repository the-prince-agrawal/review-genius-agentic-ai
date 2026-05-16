package com.reviewgenius.agent.core.think.prompt;

public enum PromptVersion {
  REVIEW_V1, REVIEW_V2, REVIEW_V3;

  public static PromptVersion fromRetryAttempt(int retryAttempt) {
    return switch (retryAttempt) {
      case 0 -> REVIEW_V1;
      case 1 -> REVIEW_V2;
      default -> REVIEW_V3;
    };
  }
}
