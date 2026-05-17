package com.reviewgenius.agent.core.reflect;

import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.core.reflect.registry.ReflectionEngineRegistry;
import com.reviewgenius.agent.enums.ActionType;
import com.reviewgenius.agent.enums.ReflectionDecision;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.model.ReflectionResult;
import com.reviewgenius.agent.observability.logging.ReflectionLogger;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReflectionService {
  private final ReflectionLogger reflectionLogger;
  private final ReflectionEngineRegistry registry;
  public List<ReflectionResult> executeReflection(ActionType actionType, ActionResult<?> result, AgentContext context) {
    List<ReflectionResult> reflectionResults = new ArrayList<>();

    List<ReflectionEngine> reflectionEngines = registry.getReflectionEngines(actionType);

    for (ReflectionEngine engine : reflectionEngines) {
      ReflectionResult reflectionResult = engine.reflect(actionType, result, context);
      reflectionResult.setReflectionEngine(engine.getClass().getSimpleName());
      reflectionResults.add(reflectionResult);
      reflectionLogger.logReflection(actionType, reflectionResult);
      if (!ReflectionDecision.ACCEPT.equals(reflectionResult.getDecision())) {
        break;
      }
    }
    return reflectionResults;
  }
}
