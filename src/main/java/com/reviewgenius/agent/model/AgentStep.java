package com.reviewgenius.agent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentStep {
  private String thought;
  private String action;
  private String observation;
}
