package com.reviewgenius.agent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Issue {

  private String fileName;
  private int lineNumber;
  private String severity; // LOW, MEDIUM, HIGH
  private String description;
  private String suggestion;
}