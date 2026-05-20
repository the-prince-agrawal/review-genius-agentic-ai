package com.reviewgenius.agent.core.agent;

import com.reviewgenius.agent.core.think.LLMClient;
import com.reviewgenius.agent.core.think.prompt.ThinkEnginePromptBuilder;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.Issue;
import com.reviewgenius.agent.util.IssueParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.reviewgenius.agent.util.CommonUtil.getAnalyzeCodeMockResponse;
import static com.reviewgenius.agent.util.IssueParserUtil.splitParsedDiffIntoChunks;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyzeCodeAgent {

  @Value("${agent.analysis.chunk-size:1000}")
  private int chunkSize;

  @Value("${agent.analysis.chunk-analysis-timeout-seconds:60}")
  private int chunkAnalysisTimeoutSeconds;

  @Value("${agent.llm.client.analyze-code.disabled:false}")
  private boolean isAnalyzeCodeLLMClientDisabled;

  private final LLMClient llmClient;
  private final ThinkEnginePromptBuilder thinkEnginePromptBuilder;
  private final Executor asyncExecutor;
  private final IssueParserUtil issueParserUtil;

  public List<Issue> analyze(AgentContext context, String parsedDiff) {
    List<String> diffChunks = splitParsedDiffIntoChunks(parsedDiff, chunkSize);
    log.info("Starting parallel analysis. totalChunks={}", diffChunks.size());
    AtomicInteger counter = new AtomicInteger();
    List<CompletableFuture<List<Issue>>> futures = diffChunks
        .stream()
        .map(diffChunk -> {
          int chunkNumber = counter.incrementAndGet();
          return analyzeChunkAsync(context, diffChunk, chunkNumber)
              .orTimeout(chunkAnalysisTimeoutSeconds, TimeUnit.SECONDS)
              .exceptionally(ex -> logErrorAndGetEmptyIssueList(ex, chunkNumber));
        }).toList();

    return futures.stream()
        .map(CompletableFuture::join)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  private static List<Issue> logErrorAndGetEmptyIssueList(Throwable ex, int chunkNumber) {
    log.error("Chunk analysis failed. chunk={}", chunkNumber, ex);
    return List.of();
  }

  private CompletableFuture<List<Issue>> analyzeChunkAsync(AgentContext context, String diffChunk, int chunkNumber) {
    return CompletableFuture.supplyAsync(() -> {
      String chunkPrompt = thinkEnginePromptBuilder.buildCodeReviewPrompt(context, diffChunk);
      log.debug("Sending chunk to LLM. chunkSize={}, chunkIndex={}", diffChunk.length(), chunkNumber);
      String analysisResult = callLLM(chunkPrompt);
      List<Issue> issues = issueParserUtil.parseIssues(analysisResult);
      context.getAnalysis().add(analysisResult);
      return issues;
    }, asyncExecutor);
  }

  private String callLLM(String chunkPrompt) {
    if (isAnalyzeCodeLLMClientDisabled) {
      return getAnalyzeCodeMockResponse("static/code-review-mock-response.json");
    }
    return llmClient.getResponse(chunkPrompt);
  }

}
