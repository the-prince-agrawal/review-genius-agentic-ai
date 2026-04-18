package com.reviewgenius.agent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentContext {
  private String input;
  private List<String> steps = new ArrayList<>();
  private boolean completed;
}