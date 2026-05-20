package com.reviewgenius.agent.model;

import com.reviewgenius.agent.enums.IssueDiffSide;
import com.reviewgenius.agent.enums.IssueSeverity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Issue {

  private String fileName;
  private Integer lineNumber;
  private IssueDiffSide side;
  private IssueSeverity severity;
  private String description;
  private String suggestion;
}