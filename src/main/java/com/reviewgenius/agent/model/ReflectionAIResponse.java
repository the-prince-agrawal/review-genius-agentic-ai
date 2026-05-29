/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.model;

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
public class ReflectionAIResponse {
  private boolean retryRecommended;
  private double confidenceScore;
  private String reflectionSummary;

  @Builder.Default
  private List<String> detectedProblems = new ArrayList<>();
}
