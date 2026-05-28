package com.reviewgenius.agent.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DiffHunk {
  private String header;
  private List<DiffLine> lines = new ArrayList<>();
}