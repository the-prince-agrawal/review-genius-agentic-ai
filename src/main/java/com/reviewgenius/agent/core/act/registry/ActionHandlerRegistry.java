/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.core.act.registry;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.enums.ActionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ActionHandlerRegistry {
  private final Map<ActionType, ActionHandler> handlerMap;
  @Autowired
  public ActionHandlerRegistry(List<ActionHandler> handlers) {
    Map<ActionType, ActionHandler> map = new HashMap<>();
    for (ActionHandler handler : handlers) {
      map.put(handler.getType(), handler);
    }
    this.handlerMap = map;
  }

  public ActionHandler getActionHandler(ActionType type) {
    return handlerMap.get(type);
  }
}
