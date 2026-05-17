package com.reviewgenius.agent.core.reflect.registry.prompt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReflectEnginePromptTemplate {

  SEMANTIC_REFLECTION_PROMPT(
      """
          You are a principal engineer performing semantic validation on AI generated code review findings.

          Your job:
          - Detect hallucinated issues
          - Detect duplicate findings
          - Detect weak/generic recommendations
          - Detect invalid severity assignments
          - Detect meaningless or low confidence findings

          REVIEW RULES:
          - Be strict and highly practical
          - Prefer precision over quantity
          - Ignore formatting concerns
          - Only detect real semantic quality issues

          RESPONSE RULES:
          - Return ONLY VALID RAW JSON
          - No markdown
          - No explanation text
          - No comments
          - Must be parsable by Jackson ObjectMapper

          RESPONSE FORMAT:
          {
            "retryRecommended": false,
            "confidenceScore": 0.95,
            "reflectionSummary": "Reflection summary here",
            "detectedProblems": [
              "Duplicate issue detected",
              "Weak recommendation found"
            ]
          }

          If everything looks good:
          {
            "retryRecommended": false,
            "confidenceScore": 0.98,
            "reflectionSummary": "All findings look valid and meaningful",
            "detectedProblems": []
          }

          Input Findings:
          %s
          """);
  private final String template;
}
