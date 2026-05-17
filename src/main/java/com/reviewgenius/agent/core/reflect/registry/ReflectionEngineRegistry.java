package com.reviewgenius.agent.core.reflect.registry;

import com.reviewgenius.agent.core.reflect.AnalyzeCodeRuleBasedReflectionEngine;
import com.reviewgenius.agent.core.reflect.GeneralRuleBasedReflectionEngine;
import com.reviewgenius.agent.core.reflect.ReflectionEngine;
import com.reviewgenius.agent.core.reflect.SemanticAnalysisReflectionEngine;
import com.reviewgenius.agent.enums.ActionType;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ReflectionEngineRegistry {
  private final GeneralRuleBasedReflectionEngine general;
  private final AnalyzeCodeRuleBasedReflectionEngine analyze;
  private final SemanticAnalysisReflectionEngine semantic;
  private Map<ActionType, List<ReflectionEngine>> reflectionEngineMap;

  @PostConstruct
  public void init() {
    reflectionEngineMap = Map.of(
        ActionType.FETCH_PR, List.of(general),
        ActionType.PARSE_DIFF, List.of(general),
        ActionType.ANALYZE_CODE, List.of(general, analyze, semantic));
  }

  public List<ReflectionEngine> getReflectionEngines(ActionType actionType) {
    return reflectionEngineMap.getOrDefault(actionType, List.of());
  }
}