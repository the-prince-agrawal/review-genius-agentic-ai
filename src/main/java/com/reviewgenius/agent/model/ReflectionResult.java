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
  private boolean retryRecommended;
  private double confidenceScore;
  private String reflectionSummary;
  private List<String> detectedProblems = new ArrayList<>();
  private ReflectionDecision decision;
  private ReflectionType reflectionType;

  public void addProblem(String problem) {
    this.detectedProblems.add(problem);
  }
}
