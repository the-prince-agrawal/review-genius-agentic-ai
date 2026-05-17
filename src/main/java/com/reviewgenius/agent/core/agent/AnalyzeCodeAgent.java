package com.reviewgenius.agent.core.agent;

import com.reviewgenius.agent.core.think.LLMClient;
import com.reviewgenius.agent.core.think.prompt.ThinkEnginePromptBuilder;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.Issue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static com.reviewgenius.agent.util.CommonUtil.getAnalyzeCodeMockResponse;
import static com.reviewgenius.agent.util.IssueParserUtil.getParsedDifferenceChunks;
import static com.reviewgenius.agent.util.IssueParserUtil.parseIssues;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyzeCodeAgent {

  @Value("${agent.analysis.chunk-size:1000}")
  private int chunkSize;
  @Value("${agent.llm.client.analyze-code.disabled:false}")
  private boolean isAnalyzeCodeLLMClientDisabled;

  private final LLMClient llmClient;
  private final ThinkEnginePromptBuilder thinkEnginePromptBuilder;
  private final Executor asyncExecutor;

  public List<Issue> analyze(AgentContext context, String parsedDiff) {
    List<String> diffChunks = getParsedDifferenceChunks(parsedDiff, chunkSize);
    log.info("Starting parallel analysis. totalChunks={}", diffChunks.size());

    List<CompletableFuture<List<Issue>>> futures = diffChunks
        .stream()
        .map(diffChunk -> analyzeChunkAsync(context, diffChunk))
        .toList();

    CompletableFuture<Void> allFutures = CompletableFuture
        .allOf(futures.toArray(new CompletableFuture[0]));
    allFutures.join();

    return futures.stream()
        .map(CompletableFuture::join)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  private CompletableFuture<List<Issue>> analyzeChunkAsync(AgentContext context, String diffChunk) {
    return CompletableFuture.supplyAsync(() -> {
      String chunkPrompt = thinkEnginePromptBuilder.buildCodeReviewPrompt(context, diffChunk);
      log.debug("Sending chunk to LLM. chunkSize={}", diffChunk.length());
      String analysisResult = callLLM(chunkPrompt);
      addAnalysis(context, analysisResult);
      return parseIssues(analysisResult);
    }, asyncExecutor);
  }

  private String callLLM(String chunkPrompt) {
    if (isAnalyzeCodeLLMClientDisabled) {
      return getAnalyzeCodeMockResponse("static/code-review-mock-response.json");
    }
    return llmClient.getResponse(chunkPrompt);
  }

  private synchronized void addAnalysis(AgentContext context, String analysisResult) {
    context.getAnalysis().add(analysisResult);
  }
}
