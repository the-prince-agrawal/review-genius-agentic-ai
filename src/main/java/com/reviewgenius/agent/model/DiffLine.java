package com.reviewgenius.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiffLine {

  private String type;
  private String content;

  private Integer oldLineNumber;
  private Integer newLineNumber;

  private Integer diffPosition;
}