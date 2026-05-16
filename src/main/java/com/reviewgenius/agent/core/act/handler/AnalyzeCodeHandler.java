package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.think.LLMClient;
import com.reviewgenius.agent.core.think.prompt.PromptBuilder;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.Issue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static com.reviewgenius.agent.enums.ActionType.ANALYZE_CODE;
import static com.reviewgenius.agent.util.IssueParserUtil.getParsedDifferenceChunks;
import static com.reviewgenius.agent.util.IssueParserUtil.parseIssues;

@Service
@Slf4j
public class AnalyzeCodeHandler implements ActionHandler {

  @Value("${agent.analysis.chunk-size:1000}")
  private int chunkSize;
  private final LLMClient LLMClient;
  private final PromptBuilder promptBuilder;

  private final Executor asyncExecutor;

  public AnalyzeCodeHandler(LLMClient LLMClient, PromptBuilder promptBuilder, Executor asyncExecutor) {
    this.LLMClient = LLMClient;
    this.promptBuilder = promptBuilder;
    this.asyncExecutor = asyncExecutor;
  }

  @Override
  public ActionResult<?> execute(AgentContext context) {
    String parsedDiff = context.getParsedDiff();
    List<String> diffChunks = getParsedDifferenceChunks(parsedDiff, chunkSize);
    log.info("Starting parallel analysis. totalChunks={}", diffChunks.size());

    try {
      List<CompletableFuture<List<Issue>>> futures = diffChunks.stream()
          .map(diffChunk -> CompletableFuture.supplyAsync(() -> {
            String chunkPrompt = promptBuilder.buildCodeReviewPrompt(context, diffChunk);
            log.debug("Sending chunk to LLM. chunkSize={}", diffChunk.length());
            String analysisResult = LLMClient.getResponse(chunkPrompt);
            context.getAnalysis().add(analysisResult);
            return parseIssues(analysisResult);
          }, asyncExecutor)).toList();

      CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
      allFutures.join();
      List<Issue> allIssues = futures.stream()
          .map(CompletableFuture::join)
          .flatMap(List::stream)
          .collect(Collectors.toList());
      context.setIssues(allIssues);
      log.info("Parallel code analysis completed. totalIssues={}", allIssues.size());
      return ActionResult.success("Code analyzed",
          Map.of("issueCount", allIssues.size(), "chunkCount", diffChunks.size()));
    } catch (Exception ex) {
      log.error("Error during parallel code analysis: {}", ex.getMessage(), ex);
      return ActionResult.failure("Action execution failed", ex.getMessage());
    }
  }

  @Override
  public ActionType getType() {
    return ANALYZE_CODE;
  }
}
