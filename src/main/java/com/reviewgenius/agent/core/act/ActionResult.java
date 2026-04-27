package com.reviewgenius.agent.core.act;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ActionResult {
  private ActionResultStatus status;
  private String message;
  private Object data;
}
