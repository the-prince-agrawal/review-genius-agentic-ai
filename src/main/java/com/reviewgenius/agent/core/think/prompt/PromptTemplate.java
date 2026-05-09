package com.reviewgenius.agent.core.think.prompt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PromptTemplate {

  REVIEW_PROMPT_V1(PromptVersion.REVIEW_V1,
      """
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
          """), REVIEW_PROMPT_V2(PromptVersion.REVIEW_V2, "TODO"), REVIEW_PROMPT_V3(PromptVersion.REVIEW_V3, "TODO");

  private final PromptVersion version;

  private final String template;
}
