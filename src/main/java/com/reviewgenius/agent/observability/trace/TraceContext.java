/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.observability.trace;

import org.slf4j.MDC;

public final class TraceContext {
  private TraceContext() {
  }

  public static String getCorrelationId() {
    return MDC.get(TraceConstants.CORRELATION_ID);
  }
}
