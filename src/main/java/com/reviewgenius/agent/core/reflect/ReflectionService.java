package com.reviewgenius.agent.core.reflect;

import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.reflect.registry.ReflectionEngineRegistry;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.enums.ReflectionDecision;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReflectionResult;
import com.reviewgenius.agent.observability.logging.ReflectionLogger;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReflectionService {
  private final ReflectionLogger reflectionLogger;
  private final ReflectionEngineRegistry registry;
  public List<ReflectionResult> executeReflection(ActionType actionType, ActionResult<?> result, AgentContext context) {
    List<ReflectionResult> reflectionResults = new ArrayList<>();

    List<ReflectionEngine> reflectionEngines = registry.getReflectionEngines(actionType);

    for (ReflectionEngine engine : reflectionEngines) {
      log.info("Executing reflection. actionType={}, reflectionEngine={}", actionType,
          engine.getClass().getSimpleName());
      long startTime = System.currentTimeMillis();
      ReflectionResult reflectionResult = engine.reflect(actionType, result, context);
      reflectionResult.setReflectionEngine(engine.getClass().getSimpleName());
      reflectionResults.add(reflectionResult);
      reflectionLogger.logReflection(actionType, reflectionResult);
      long totalExecutionTimeMs = System.currentTimeMillis() - startTime;
      reflectionResult.setTotalExecutionTimeMs(totalExecutionTimeMs);
      if (!ReflectionDecision.ACCEPT.equals(reflectionResult.getDecision())) {
        break;
      }
    }
    return reflectionResults;
  }
}
