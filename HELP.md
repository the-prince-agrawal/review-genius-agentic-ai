# 🚀 Review Genius Agent

AI-powered autonomous pull request review system built using Java, Spring Boot, Spring AI, and Agentic Workflow Architecture.

The system fetches GitHub pull request diffs, analyzes changed code using LLMs, identifies issues, and generates structured review feedback automatically.

---

# ✨ Features

- AI-powered pull request review
- GitHub PR diff fetching
- Intelligent code analysis using LLM
- Modular agentic workflow architecture
- Think → Act → Observe execution model
- Structured issue detection
- Severity-based review generation
- Extensible action-handler framework
- Spring AI integration
- Caching support
- Clean layered architecture

---

# 🧠 Architecture Overview

```text
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
         ┌─────────────────┼─────────────────┐
         ▼                 ▼                 ▼
    ┌─────────┐      ┌─────────┐      ┌─────────┐
    │ THINK   │ ---> │  ACT    │ ---> │ OBSERVE │
    └─────────┘      └─────────┘      └─────────┘
                           │
                           ▼
                ┌─────────────────────┐
                │ GitHub + LLM APIs   │
                └─────────────────────┘
```

---

# 🏗️ Project Structure

```text
src/main/java/com/reviewgenius/agent

├── config
│   └── CacheConfig
│
├── controller
│   └── AgentController
│
├── core
│   ├── act
│   │   ├── handler
│   │   └── registry
│   │
│   ├── think
│   │   └── prompt
│   │
│   └── observe
│
├── enums
├── model
├── orchestrator
├── service
└── util
```

---

# ⚙️ Agent Workflow

## 1. Fetch Pull Request

The system fetches GitHub PR diff data using GitHub APIs.

Handled By:
- FetchPRHandler
- GitHubService

---

## 2. Parse Diff

Raw git diff is transformed into structured file-level changes.

Handled By:
- ParseDiffHandler
- DiffParserUtil

---

## 3. Analyze Code

Changed code is sent to the LLM for intelligent review.

Handled By:
- AnalyzeCodeHandler
- LLMClient
- PromptBuilder

---

## 4. Parse AI Response

AI response is converted into structured issue objects.

Handled By:
- IssueParserUtil

---

## 5. Generate Review

Final human-readable review summary is generated.

Handled By:
- GenerateReviewHandler

---

# 🧩 Core Components

| Component | Responsibility |
|---|---|
| AgentController | REST API entry point |
| AgentOrchestrator | Controls full workflow |
| ThinkEngine | Decides next action |
| ActionExecutor | Executes handlers |
| ObservationHandler | Tracks workflow state |
| LLMClient | Communicates with LLM |
| PromptBuilder | Creates prompts |
| GitHubService | Fetches PR diffs |

---

# 📦 Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Core language |
| Spring Boot | Backend framework |
| Spring AI | LLM integration |
| Maven | Build tool |
| GitHub API | PR diff fetching |
| OpenAI/Ollama | AI analysis |
| Concurrent Cache | Performance optimization |

---

# 🔥 Supported Agent Actions

| Action | Description |
|---|---|
| FETCH_PR | Fetch GitHub PR diff |
| PARSE_DIFF | Parse raw diff |
| ANALYZE_CODE | AI-based code analysis |
| GENERATE_REVIEW | Final review generation |

---

# 📄 Sample Review Output

```text
🔍 CODE REVIEW SUMMARY

File       : UserService.java
Line       : 42
Severity   : HIGH

Issue      :
Potential NullPointerException while accessing user object.

Suggestion :
Add null validation before dereferencing the object.

--------------------------------------------------
```

---

# 🚀 Getting Started

## 1. Clone Repository

```bash
git clone https://github.com/princeagrawal024/review-genius-agentic-ai.git
```

## 2. Configure Environment Variables

```bash
GITHUB_TOKEN=your_github_token
OPENAI_API_KEY=your_openai_key
```

## 3. Build Project

```bash
mvn clean install
```

## 4. Run Application

```bash
mvn spring-boot:run
```

---

# ⚙️ API Endpoint

## Review Pull Request

```http
POST /agent/review
```

## Sample Request

```json
{
  "prURL": "https://github.com/princeagrawal024/log-intelligence-engine/pull/1",
  "reviewType": "focus on null pointer exception"
}
```

## Sample Response-1
```json
🔍 CODE REVIEW SUMMARY

File       : src/main/java/com/princeagrawal/ai/logintelligence/config/CacheConfig.java
Line       : 10
Severity   : MEDIUM
Issue      : Potential null pointer exception when accessing cache manager.
Suggestion : Ensure that the cacheManager() method is not returning null before using it.

----------------------------------------

File       : src/main/java/com/princeagrawal/ai/logintelligence/service/LogAnalysisService.java
Line       : 5
Severity   : MEDIUM
Issue      : Cacheable annotation may lead to null pointer exception if LOGS_CACHE is not properly initialized.
Suggestion : Verify that LOGS_CACHE is initialized and not null before using it in the @Cacheable annotation.

----------------------------------------
```

## Sample Response-2
```json
🔍 CODE REVIEW SUMMARY

File       : src/main/java/com/princeagrawal/ai/logintelligence/config/CacheConfig.java
Line       : 10
Severity   : MEDIUM
Issue      : Using ConcurrentMapCacheManager may not be suitable for high-concurrency scenarios.
Suggestion : Consider using a more robust cache manager like EhCache or Caffeine for better performance under load.

----------------------------------------

File       : src/main/java/com/princeagrawal/ai/logintelligence/service/LogAnalysisService.java
Line       : 5
Severity   : MEDIUM
Issue      : Cacheable annotation may lead to stale data if not properly configured.
Suggestion : Ensure that the cache eviction strategy is in place to handle data consistency.

----------------------------------------


```

---

# 🧪 Testing

Run tests using:

```bash
mvn test
```

---

# 🛠️ Future Enhancements

- Multi-agent collaboration
- GitHub inline review comments
- Auto-fix suggestions
- Security vulnerability analysis
- Performance optimization analysis
- Slack/Jira integration
- Vector memory support
- RAG-enabled code understanding
- SonarQube integration
- PR review scoring
- Autonomous code fixing

---

# 🧠 Design Patterns Used

- Strategy Pattern
- Orchestrator Pattern
- State Machine Pattern
- DTO Pattern
- Agentic Workflow Pattern
- ReAct Architecture
- Builder Pattern

---

# 📌 Key Highlights

✅ Modular architecture

✅ Extensible action system

✅ AI-native workflow

✅ Enterprise-ready structure

✅ Clear separation of concerns

✅ Agentic execution model

✅ Modern AI engineering practices

---

# 👨‍💻 Author

Prince Agrawal

Senior Software Engineer | AI-Enabled Backend Engineer

Review Genius Agent

AI-Powered Autonomous Code Review Platform

Built with Java + Spring AI