# 🚀 Review Genius Agent

## Overview

Review Genius Agent is an AI-powered autonomous Pull Request Review platform built using Java 21, Spring Boot, Spring AI, GitHub APIs, and an Agentic Think → Act → Reflect → Retry architecture.

The platform automatically:

- Validates Pull Requests
- Fetches GitHub PR diffs
- Parses code changes
- Sends code to LLMs for analysis
- Applies structural and semantic reflection
- Retries low-quality AI analysis automatically
- Posts GitHub inline review comments
- Generates final review output
- Produces complete execution observability

---

# Architecture
```
┌─────────────────────┐
│  REST API Request   │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ AgentController     │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ AgentOrchestrator   │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Think Engine        │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ ActionExecutor      │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Action Handlers     │
├─────────────────────┤
│ Validate PR         │
│ Fetch Diff          │
│ Parse Diff          │
│ Analyze Code        │
│ Add Comments        │
│ Generate Review     │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ GitHub + LLM APIs   │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│ Reflection Engines  │
└──────────┬──────────┘
           │
     PASS? │
      ┌────┴────┐
      │         │
     YES       NO
      │         │
      ▼         ▼
 Continue   RetryEngine
      │         │
      └────┬────┘
           ▼
┌─────────────────────┐
│ Final Review        │
└─────────────────────┘

```
---

# Key Capabilities
```
✅ Autonomous PR Review

✅ GitHub Inline Comments

✅ AI Reflection Layer

✅ Automatic Retry Mechanism

✅ Semantic Quality Validation

✅ Execution Observability

✅ Prompt Versioning Support

✅ Agentic Think → Act → Reflect Workflow
```
---

# Current Workflow

```
VALIDATE_PR_STATE
        ↓
FETCH_PR_DIFF
        ↓
PARSE_DIFF
        ↓
ANALYZE_CODE
        ↓
REFLECT_ANALYSIS
        ↓
RETRY_IF_REQUIRED
        ↓
ADD_REVIEW_COMMENTS
        ↓
GENERATE_REVIEW
        ↓
RETURN_RESPONSE
```
---
# Supported Actions

```
VALIDATE_PR_STATE
│
└── ValidatePRHandler

FETCH_PR_DIFF
│
└── FetchPRHandler

PARSE_DIFF
│
└── ParseDiffHandler

ANALYZE_CODE
│
└── AnalyzeCodeHandler

ADD_REVIEW_COMMENTS
│
└── AddReviewCommentsHandler

GENERATE_REVIEW
│
└── GenerateReviewHandler
```
---
# Reflection Types
```
STRUCTURAL
│
├── GeneralRuleBasedReflectionEngine
│
└── AnalyzeCodeRuleBasedReflectionEngine

SEMANTIC
│
└── SemanticAnalysisReflectionEngine
```
---
# API
```
POST /agent/review?debug=true

Request:

{
"prURL":"https://github.com/owner/repo/pull/6",
"reviewType":"do generic review"
}
```
---

# Configuration
```
OpenAI Model:
gpt-4o-mini

Temperature:
0.2

Retry Count:
2

Chunk Size:
1500

Chunk Analysis Timeout:
160 seconds

GitHub Remote Fetch:
Enabled
```
---

# Supported LLM Providers

OpenAI
- GPT-4o-mini

Future
- Ollama
- DeepSeek
- Local Models

---

# Core Components

AgentController
- REST entrypoint

AgentOrchestrator
- Controls complete workflow
- Think
- Execute
- Reflect
- Retry

ThinkEngine
- Decides next action dynamically

ActionExecutor
- Executes handlers
- Tracks execution history

ReflectionService
- Executes reflection engines

RetryEngine
- Determines retry behavior

LLMClient
- Spring AI wrapper

---

# Action Handlers

ValidatePRHandler
Purpose:
- Verify PR state
- Ensure PR is open

FetchPRHandler
Purpose:
- Download PR metadata
- Download PR diff

ParseDiffHandler
Purpose:
- Convert raw git diff into structured model

AnalyzeCodeHandler
Purpose:
- Chunk code
- Send to LLM
- Collect findings

AddReviewCommentsHandler
Purpose:
- Create GitHub inline comments

GenerateReviewHandler
Purpose:
- Build final review response

---

# Reflection Layer

GeneralRuleBasedReflectionEngine

Validates:
- Action execution success
- Workflow correctness

AnalyzeCodeRuleBasedReflectionEngine

Validates:
- Issue quality
- Mandatory fields
- Structural consistency

SemanticAnalysisReflectionEngine

Uses AI to verify:

- Duplicate findings
- Weak findings
- Hallucinations
- Low-value comments
- Review quality

Possible Decisions:

ACCEPT
RETRY
REJECT

---

# Retry Architecture

Automatic retry occurs when:

- Reflection requests retry
- Retry count not exhausted

Current max retries:
2

Example:

Analyze Code
↓
Semantic Reflection
↓
Duplicate Findings Found
↓
Retry Analysis
↓
Reflection Passed
↓
Continue Workflow

---

# Observability

Every action stores:

- Action Type
- Status
- Start Time
- End Time
- Execution Duration
- Summary
- Reflection Results

Returned in API response when debug=true.

---

# Sample Execution

VALIDATE_PR_STATE
SUCCESS

FETCH_PR_DIFF
SUCCESS

PARSE_DIFF
SUCCESS

ANALYZE_CODE
SUCCESS

SEMANTIC REFLECTION
RETRY REQUESTED

ANALYZE_CODE
RETRY

SEMANTIC REFLECTION
PASSED

ADD_REVIEW_COMMENTS
SUCCESS

GENERATE_REVIEW
SUCCESS

---
# Response Structure
```
{
  "finalReview": {
    "issues": [...]
  },
  "executionHistories":  [...],
  "overallStatus": "SUCCESS",
  "totalExecutionTimeMs": 218053
}
```
------------------------------------------------------------

# High Level Structure
```
Response
│
├── finalReview
│   └── issues[]
│
├── executionHistories[]
│
├── overallStatus
│
└── totalExecutionTimeMs
```
------------------------------------------------------------

# finalReview Structure
```
{
  "finalReview": {
    "issues": [
      {
        "fileName": "...",
        "lineNumber": 28,
        "side": "RIGHT",
        "severity": "MEDIUM",
        "description": "...",
        "suggestion": "...",
        "diffPosition": 5
      }
    ]
  }
}
```
------------------------------------------------------------

# Issue Structure

Issue
│
├── fileName
├── lineNumber
├── side
├── severity
├── description
├── suggestion
└── diffPosition

Example:
```
{
"fileName": "src/main/java/.../ResponseMapper.java",
"lineNumber": 20,
"side": "RIGHT",
"severity": "MEDIUM",
"description": "Logging parsing failures at INFO level",
"suggestion": "Use ERROR level logging",
"diffPosition": 9
}
```
------------------------------------------------------------

# Execution History Structure
```
{
"actionType": "ANALYZE_CODE",
"status": "SUCCESS",
"startedAt": 1779988724,
"completedAt": 1779988734,
"executionTimeMs": 9995,
"summary": "Code analyzed",
"reflectionResult": [...]
}
```
------------------------------------------------------------

# Execution History Fields
```
ExecutionHistory
│
├── actionType
├── status
├── startedAt
├── completedAt
├── executionTimeMs
├── summary
└── reflectionResult[]
```
------------------------------------------------------------

# Reflection Result Structure
```
{
"passed": false,
"retryRecommended": true,
"confidenceScore": 0.72,
"reflectionSummary": "Duplicate findings detected",
"detectedProblems": [
"DUPLICATE_FINDING"
],
"decision": "RETRY",
"reflectionType": "SEMANTIC",
"reflectionEngine": "SemanticAnalysisReflectionEngine",
"totalExecutionTimeMs": 1471
}
```
------------------------------------------------------------

# Reflection Result Fields
```
ReflectionResult
│
├── passed
├── retryRecommended
├── confidenceScore
├── reflectionSummary
├── detectedProblems[]
├── decision
├── reflectionType
├── reflectionEngine
└── totalExecutionTimeMs
```
------------------------------------------------------------

# Supported Reflection Engines
```
GeneralRuleBasedReflectionEngine
│
└── Validates workflow execution

AnalyzeCodeRuleBasedReflectionEngine
│
└── Validates issue structure

SemanticAnalysisReflectionEngine
│
└── Validates review quality

Detects duplicates
Detects hallucinations
Detects weak findings
```
------------------------------------------------------------

# Actual Execution Example
```
executionHistories
│
├── VALIDATE_PR_STATE
│      └── SUCCESS
│
├── FETCH_PR_DIFF
│      └── SUCCESS
│
├── PARSE_DIFF
│      └── SUCCESS
│
├── ANALYZE_CODE
│      └── SEMANTIC REFLECTION FAILED
│      └── RETRY RECOMMENDED
│
├── ANALYZE_CODE (RETRY)
│      └── SEMANTIC REFLECTION PASSED
│
├── ADD_REVIEW_COMMENTS
│      └── SUCCESS
│
├── GENERATE_REVIEW
│      └── SUCCESS
│
└── OVERALL_STATUS
			└── SUCCESS
```
------------------------------------------------------------

# Complete Sample Response
```
{
  "finalReview": {
    "issues": [
      {
        "fileName": "ResponseMapper.java",
        "lineNumber": 20,
        "side": "RIGHT",
        "severity": "MEDIUM",
        "description": "Logging parsing failures at INFO level",
        "suggestion": "Use ERROR level logging",
        "diffPosition": 9
      }
    ]
  },
  "executionHistories": [
    {
      "actionType": "VALIDATE_PR_STATE",
      "status": "SUCCESS",
      "executionTimeMs": 903,
      "summary": "Pull request is open"
    },
    {
      "actionType": "FETCH_PR_DIFF",
      "status": "SUCCESS",
      "executionTimeMs": 999,
      "summary": "PR fetched successfully"
    },
    {
      "actionType": "PARSE_DIFF",
      "status": "SUCCESS",
      "executionTimeMs": 1,
      "summary": "Diff parsed successfully"
    },
    {
      "actionType": "ANALYZE_CODE",
      "status": "SUCCESS",
      "executionTimeMs": 9995,
      "summary": "Code analyzed"
    },
    {
      "actionType": "ADD_REVIEW_COMMENTS",
      "status": "SUCCESS",
      "executionTimeMs": 200364,
      "summary": "Review comments added successfully"
    },
    {
      "actionType": "GENERATE_REVIEW",
      "status": "SUCCESS",
      "executionTimeMs": 1,
      "summary": "Review generated successfully"
    }
  ],
  "overallStatus": "SUCCESS",
  "totalExecutionTimeMs": 218053
}
```

# Domain Models

AgentContext
- Central workflow state

ReviewRequestDto
- Incoming request

CodeReviewResponseDto
- Final review

Issue
- Review finding

DiffFile
- Changed file

DiffHunk
- Diff block

DiffLine
- Line metadata

PullRequestMetadata
- PR information

ReflectionResult
- Reflection output

ReflectionAIResponse
- AI reflection result

---

# GitHub Integration

Capabilities:

- Fetch PR
- Fetch diff
- Resolve diff positions
- Post inline review comments

Services:

GitHubApiClient
GitHubPullRequestService
GitHubService
IssuePositionResolver

---

# Prompting Architecture

ThinkEnginePromptBuilder
ThinkEnginePromptTemplate
ThinkEnginePromptVersion

Reflection Prompt Layer

ReflectEnginePromptBuilder
ReflectEnginePromptTemplate

Supports future:

- Prompt versioning
- Prompt mutation
- A/B testing

---

# Logging

Correlation ID based logging.

Pattern:

timestamp
correlationId
level
logger
message

Provides complete request traceability.

---

# Current Project Strengths

✅ Agentic workflow

✅ Think-Act-Reflect architecture

✅ Automatic retries

✅ Semantic validation

✅ GitHub inline comments

✅ Execution observability

✅ Prompt versioning foundation

✅ Reflection driven quality control

✅ Modular action handler architecture

✅ Spring AI integration

---

# Future Roadmap

Multi-Agent Reviewers

Security Review Agent

Performance Review Agent

Architecture Review Agent

Code Smell Agent

Auto Fix Suggestions

PR Scorecard

RAG Based Repository Understanding

Vector Database Memory

Historical Review Learning

Reviewer Personas

GitHub Checks API

Jira Integration

Slack Integration

Autonomous PR Fixing

---

# 📦 Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core language |
| Spring Boot | Backend framework |
| Spring AI | LLM integration |
| GitHub API | PR diff fetching |
| OpenAI/Ollama | AI analysis |
| Concurrent Cache | Performance optimization |
---



# Author

Prince Agrawal

Review Genius Agent

AI Powered Autonomous Code Review Platform
