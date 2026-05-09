package com.reviewgenius.agent.core.act;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionResult<T> {

  private ActionResultStatus status;

  private String message;

  private String errorMessage;

  private Exception exception;

  private boolean recoverable;

  private T metadata;

  public static <T> ActionResult<T> success(
      String message,
      T metadata) {

    return ActionResult.<T>builder()
        .status(ActionResultStatus.SUCCESS)
        .message(message)
        .metadata(metadata)
        .recoverable(false)
        .build();
  }

  public static <T> ActionResult<T> failure(
      String message,
      String errorMessage) {

    return ActionResult.<T>builder()
        .status(ActionResultStatus.FAILURE)
        .message(message)
        .errorMessage(errorMessage)
        .recoverable(false)
        .build();
  }
}
