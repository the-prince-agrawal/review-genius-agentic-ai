package com.reviewgenius.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AgentContext {
  private ReviewRequestDto inputDto;
  private List<String> steps = new ArrayList<>();
  private String rawDiff;
  private String parsedDiff;
  private String analysis;
  private String review;
  private boolean completed;
}