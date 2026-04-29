package com.reviewgenius.agent.core.act;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ActionResult {
  private ActionResultStatus status;
  private String message;
  private Object data;
  String error;

  public static ActionResult success(String message, Object data) {
    return new ActionResult(ActionResultStatus.SUCCESS, message, data, null);
  }

  public static ActionResult failure(String message, String error) {
    return new ActionResult(ActionResultStatus.FAILURE, message, null, error);
  }
}
