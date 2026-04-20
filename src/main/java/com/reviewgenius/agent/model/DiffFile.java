package com.reviewgenius.agent.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DiffFile {
  private String fileName;
  private List<String> addedLines = new ArrayList<>();
  private List<String> removedLines = new ArrayList<>();

  public DiffFile(String fileName) {
    this.fileName = fileName;
  }
}
