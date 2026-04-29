package com.reviewgenius.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentContext {
  private ReviewRequestDto inputDto;
  private List<String> steps = new ArrayList<>();
  List<Issue> issues;
  Map<String, Object> metadata;
  private String rawDiff;
  private String parsedDiff;
  private String analysis;
  private String review;
  private boolean completed;
}