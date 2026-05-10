package com.reviewgenius.agent.model;

import com.reviewgenius.agent.enums.IssueSeverity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewIssueDto {

  private String fileName;
  private int lineNumber;
  private IssueSeverity severity;
  private String description;
  private String suggestion;
}