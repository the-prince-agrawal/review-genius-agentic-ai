package com.reviewgenius.agent.observability.trace;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {

    String correlationId = request.getHeader(TraceConstants.CORRELATION_ID_HEADER);
    if (!StringUtils.hasText(correlationId)) {
      correlationId = UUID.randomUUID().toString();
    }

    MDC.put(TraceConstants.CORRELATION_ID, correlationId);
    response.setHeader(TraceConstants.CORRELATION_ID_HEADER, correlationId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      // MDC.clear();
      MDC.remove(TraceConstants.CORRELATION_ID);
    }
  }
}