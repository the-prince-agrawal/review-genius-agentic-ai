package com.reviewgenius.agent.core.think.prompt;

import org.springframework.stereotype.Service;

@Service
public class PromptBuilder {

  public String buildCodeReviewPrompt(String diff, String reviewType) {
    if (diff == null || diff.isBlank()) {
      return "No code diff provided.";
    }
    return """
        You are a senior software engineer doing a strict code review.

        Analyze the following code diff and identify issues.

        Focus Area:
        %s

        Rules:
        - Focus on bugs, performance, security, and bad practices
        - Ignore formatting and minor style issues
        - Be precise and concise
        - Return ONLY raw JSON
        - Do NOT wrap response in markdown
        - Do NOT use ```json
        - Do NOT add explanation text

        Return ONLY valid JSON array:
        [
          {
            "fileName": "",
            "lineNumber": 0,
            "severity": "LOW|MEDIUM|HIGH",
            "description": "",
            "suggestion": ""
          }
        ]

        Code Diff:
        %s
        """.formatted(reviewType, diff);
  }
}