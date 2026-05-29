/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.reviewgenius.agent.enums.ReflectionDecision;
import com.reviewgenius.agent.enums.ReflectionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReflectionResult {
  private boolean passed;
  private Boolean retryRecommended;
  private Double confidenceScore;
  private String reflectionSummary;
  private List<String> detectedProblems = new ArrayList<>();
  private ReflectionDecision decision;
  private ReflectionType reflectionType;
  private String reflectionEngine;
  private Long totalExecutionTimeMs;

  public void addProblem(String problem) {
    this.detectedProblems.add(problem);
  }
}
