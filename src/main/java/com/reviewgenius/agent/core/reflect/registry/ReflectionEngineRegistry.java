/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

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
        ActionType.VALIDATE_PR_STATE, List.of(general),
        ActionType.FETCH_PR_DIFF, List.of(general),
        ActionType.PARSE_DIFF, List.of(general),
        ActionType.ANALYZE_CODE, List.of(general, analyze, semantic),
        ActionType.ADD_REVIEW_COMMENTS, List.of(general)); // TODO add more reflection here
  }

  public List<ReflectionEngine> getReflectionEngines(ActionType actionType) {
    return reflectionEngineMap.getOrDefault(actionType, List.of());
  }
}