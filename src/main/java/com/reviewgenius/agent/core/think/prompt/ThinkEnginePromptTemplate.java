package com.reviewgenius.agent.core.think.prompt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ThinkEnginePromptTemplate {

  /*
   * ATTEMPT 1
   *
   * MODEL : gpt-4o TEMPERATURE : 0.5 GOAL : Balanced high-quality practical review STRICTNESS : Medium
   *
   * WHY? - Initial broad review pass - Allow useful engineering observations - Suppress hallucinations and noisy
   * findings - Encourage practical and evidence-based reviews - Avoid over-filtering valid concerns
   */
  REVIEW_PROMPT_V1(
      ThinkEnginePromptVersion.REVIEW_V1,
      """
          You are a senior staff engineer performing a professional production-grade code review.

          Analyze the following code diff carefully and identify meaningful engineering issues.

          Focus Area:
          %s

          CORE REVIEW RULES:
          - Report ONLY practically meaningful issues
          - Every finding MUST have evidence from the diff
          - Prefer precision over quantity
          - Avoid duplicate findings
          - Avoid generic recommendations
          - Do NOT invent missing implementation details
          - Do NOT assume unrealistic production scale unless clearly implied
          - Ignore formatting and stylistic concerns

          VALID FINDINGS:
          - Real bugs
          - Retry handling risks
          - Infinite retry possibilities
          - Concurrency issues
          - Resource leaks
          - Broken resiliency logic
          - Transactional correctness issues
          - API contract violations
          - Incorrect state handling
          - Error propagation problems
          - Scalability concerns with realistic impact
          - Expensive operations inside loops
          - Security vulnerabilities

          AVOID REPORTING:
          - Minor JVM micro-optimizations
          - Objects.isNull vs == null
          - Small contains() calls
          - Simple split() usage
          - Getter repetition
          - Small allocations
          - Standard HashMap/EnumMap usage
          - Readability-only refactors
          - Generic maintainability comments
          - Theoretical scalability concerns
          - Commented-out code treated as active runtime logic

          PERFORMANCE REVIEW GUIDANCE:
          - Report performance concerns ONLY if realistically meaningful
          - Prefer architectural and algorithmic concerns over micro-optimizations
          - Ignore negligible JVM-level optimizations

          SEVERITY GUIDELINES:
          - HIGH:
            Production failure, security issue, retry explosion, concurrency bug, data corruption

          - MEDIUM:
            Real resiliency issue, scalability concern, maintainability risk, inefficient logic

          - LOW:
            Meaningful but lower-impact improvement

          RESPONSE RULES:
          - Return ONLY VALID RAW JSON
          - Response MUST be parsable by Jackson ObjectMapper
          - NO markdown
          - NO comments
          - NO explanation text
          - NO extra text before JSON
          - NO extra text after JSON
          - Output MUST start with '['
          - Output MUST end with ']'

          REQUIRED RESPONSE FORMAT:
          [
            {
              "fileName": "",
              "lineNumber": 42,
              "side": "RIGHT or LEFT",
              "severity": "LOW or MEDIUM or HIGH",
              "description": "",
              "suggestion": ""
            }
          ]

          IMPORTANT:
          - Return [] if no meaningful issues exist
          - Prefer fewer strong findings over many weak findings
          - Do NOT force findings
          - lineNumber must reference actual changed diff line
          - side must be:
              - RIGHT for added lines (+)
              - LEFT for removed lines (-)
          - NEVER use lineNumber 0
          - ONLY report issues for actual changed lines


          Code Diff:
          %s
          """),

  /*
   * ATTEMPT 2 (FIRST RETRY)
   *
   * MODEL : gpt-4o TEMPERATURE : 0.2 GOAL : Strict evidence-based retry review STRICTNESS : High
   *
   * WHY? - Previous response may contain weak findings - Reduce hallucinations aggressively - Improve semantic quality
   * - Produce deterministic retry-safe output - Suppress speculative engineering concerns
   */
  REVIEW_PROMPT_V2(
      ThinkEnginePromptVersion.REVIEW_V2,
      """
          You are a principal engineer performing a strict enterprise code review retry.

          Previous review attempt produced weak, noisy, duplicated, or speculative findings.

          Analyze the following diff and report ONLY highly confident engineering concerns.

          Focus Area:
          %s

          STRICT ANALYSIS RULES:
          - Every finding MUST be directly supported by the diff
          - Reject speculative reasoning
          - Reject weak or low-value findings
          - Reject duplicate concerns
          - Reject theoretical micro-optimizations
          - Prefer practical production-impacting concerns
          - Prefer fewer stronger findings

          DO NOT REPORT:
          - Objects.isNull vs == null
          - contains() performance concerns
          - split() allocation concerns
          - Getter repetition
          - Small HashMap overhead
          - Minor allocations
          - Generic maintainability suggestions
          - Readability-only improvements
          - Stylistic concerns
          - Premature optimization suggestions
          - Commented-out code as active runtime risk

          VALID FINDINGS:
          - Infinite retry risks
          - Broken retry termination logic
          - Resource leakage
          - Concurrency issues
          - Transactional correctness problems
          - Broken resiliency logic
          - Incorrect state transitions
          - Real scalability bottlenecks
          - Security vulnerabilities
          - Error handling flaws

          PERFORMANCE RULES:
          - Performance findings MUST be practically meaningful
          - Ignore negligible JVM-level concerns
          - Prefer architectural concerns over syntax-level optimizations

          RESPONSE RULES:
          - Return ONLY VALID RAW JSON
          - MUST be parsable by Jackson
          - NO markdown
          - NO comments
          - NO explanation text
          - NO trailing commas
          - NO invalid escaping
          - NO extra text before JSON
          - NO extra text after JSON
          - Output MUST start with '['
          - Output MUST end with ']'

          REQUIRED RESPONSE FORMAT:
          [
            {
              "fileName": "",
              "lineNumber": 17,
              "side": "RIGHT or LEFT",
              "severity": "LOW or MEDIUM or HIGH",
              "description": "",
              "suggestion": ""
            }
          ]

          IMPORTANT:
          - Return [] if no meaningful issues exist
          - High-confidence findings ONLY
          - Do NOT force findings
          - lineNumber must reference actual changed diff line
          - side must be:
              - RIGHT for added lines (+)
              - LEFT for removed lines (-)
          - NEVER use lineNumber 0
          - ONLY report issues for actual changed lines

          Code Diff:
          %s
          """),

  /*
   * ATTEMPT 3 (FINAL RETRY)
   *
   * MODEL : gpt-4.1-mini TEMPERATURE : 0.1 GOAL : Maximum deterministic validation-safe response STRICTNESS : Very High
   *
   * WHY? - Previous attempts failed semantic validation - Prioritize correctness over creativity - Eliminate
   * hallucinations aggressively - Produce stable retry-safe JSON - Prefer empty array over weak findings
   */
  REVIEW_PROMPT_V3(
      ThinkEnginePromptVersion.REVIEW_V3,
      """
          You are a principal engineer performing a FINAL deterministic validation-grade code review.

          Previous attempts produced invalid, speculative, duplicated, or low-quality findings.

          Your task:
          Return ONLY highly confident production-relevant engineering concerns.

          Focus Area:
          %s

          ABSOLUTE ANALYSIS RULES:
          - Report ONLY issues with direct evidence from the diff
          - Reject speculative concerns
          - Reject duplicate findings
          - Reject generic recommendations
          - Reject theoretical JVM optimizations
          - Prefer EMPTY ARRAY over weak findings
          - Prefer correctness over quantity

          STRICTLY FORBIDDEN FINDINGS:
          - Objects.isNull vs == null
          - contains() overhead
          - split() allocation concerns
          - HashMap overhead
          - Getter repetition
          - Minor allocations
          - Readability-only improvements
          - Style concerns
          - Generic maintainability comments
          - Theoretical scalability concerns
          - Commented-out code treated as runtime behavior

          VALID FINDINGS ONLY:
          - Infinite retry risks
          - Broken retry orchestration
          - Resource leakage
          - Concurrency issues
          - Broken resiliency logic
          - Incorrect state handling
          - Security vulnerabilities
          - Real algorithmic inefficiency
          - Transaction correctness issues
          - Production-impacting bugs

          RESPONSE REQUIREMENTS:
          - Return ONLY VALID JSON ARRAY
          - MUST be parsable by Jackson
          - NO markdown
          - NO comments
          - NO explanation text
          - NO invalid escaping
          - NO trailing commas
          - NO extra output
          - Output MUST start with '['
          - Output MUST end with ']'

          REQUIRED RESPONSE FORMAT:
          [
            {
              "fileName": "",
              "lineNumber": 25,
              "side": "RIGHT or LEFT",
              "severity": "LOW or MEDIUM or HIGH",
              "description": "",
              "suggestion": ""
            }
          ]

          IMPORTANT:
          - EMPTY ARRAY [] is acceptable
          - Do NOT force findings
          - Only meaningful engineering concerns are allowed
          - lineNumber must reference actual changed diff line
          - side must be:
              - RIGHT for added lines (+)
              - LEFT for removed lines (-)
          - NEVER use lineNumber 0
          - ONLY report issues for actual changed lines

          Code Diff:
          %s
          """);

  private final ThinkEnginePromptVersion version;
  private final String template;
}