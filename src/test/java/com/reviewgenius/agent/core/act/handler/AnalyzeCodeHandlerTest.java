package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.act.ActionResultStatus;
import com.reviewgenius.agent.core.think.LLMClient;
import com.reviewgenius.agent.core.think.prompt.PromptBuilder;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReviewRequestDto;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyzeCodeHandlerTest {

  @Mock
  private LLMClient llmClient;

  @Mock
  private PromptBuilder promptBuilder;

  @InjectMocks
  private AnalyzeCodeHandler analyzeCodeHandler;

  @Test
  @Disabled
  void testExecute() {
    AgentContext context = getAgentContext();
    doCallRealMethod().when(promptBuilder).buildCodeReviewPrompt(context);
    when(llmClient.getResponse(anyString())).thenReturn(getAnalysisResult());
    ActionResult actionResult = analyzeCodeHandler.execute(context);
    assertEquals(ActionResultStatus.SUCCESS, actionResult.getStatus());
  }

  private AgentContext getAgentContext() {
    ReviewRequestDto reviewRequestDto = new ReviewRequestDto();
    reviewRequestDto.setReviewType("review type");
    reviewRequestDto.setPrURL("PR_URL");
    return AgentContext.builder()
        .inputDto(reviewRequestDto)
        .parsedDiff("PARSED_DIFF")
        .completed(false)
        .build();
  }

  private String getAnalysisResult() {
    return """
        ```json
        [
          {
            "fileName": "src/main/java/com/princeagrawal/ai/logintelligence/config/CacheConfig.java",
            "lineNumber": 10,
            "severity": "MEDIUM",
            "description": "Using ConcurrentMapCacheManager may lead to memory issues if the cache grows large.",
            "suggestion": "Consider using a more scalable cache manager like EhCache or Redis for production."
          },
          {
            "fileName": "src/main/java/com/princeagrawal/ai/logintelligence/service/LogAnalysisService.java",
            "lineNumber": 3,
            "severity": "MEDIUM",
            "description": "Cacheable annotation may lead to stale data if not properly configured.",
            "suggestion": "Ensure that cache expiration and invalidation strategies are implemented."
          }
        ]
        ```
        """;
  }
}