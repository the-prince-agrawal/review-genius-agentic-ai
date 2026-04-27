package com.reviewgenius.agent.core.act.registry;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.handler.AnalyzeCodeHandler;
import com.reviewgenius.agent.core.act.handler.FetchPRHandler;
import com.reviewgenius.agent.core.act.handler.GenerateReviewHandler;
import com.reviewgenius.agent.core.act.handler.ParseDiffHandler;
import com.reviewgenius.agent.enums.ActionType;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ActionHandlerRegistry {
  private final Map<ActionType, ActionHandler> handlerMap;

  public ActionHandlerRegistry(
      FetchPRHandler fetchPRHandler,
      ParseDiffHandler parseDiffHandler,
      AnalyzeCodeHandler analyzeCodeHandler,
      GenerateReviewHandler generateReviewHandler) {
    handlerMap = Map.of(
        ActionType.FETCH_PR, fetchPRHandler,
        ActionType.PARSE_DIFF, parseDiffHandler,
        ActionType.ANALYZE_CODE, analyzeCodeHandler,
        ActionType.GENERATE_REVIEW, generateReviewHandler);
  }

  public ActionHandler getActionHandler(ActionType type) {
    return handlerMap.get(type);
  }
}
