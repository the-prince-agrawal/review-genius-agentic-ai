/*
 * Copyright (c) 2026 Prince Agrawal
 * All Rights Reserved.
 */

package com.reviewgenius.agent.controller;

import com.reviewgenius.agent.model.ReviewRequestDto;
import com.reviewgenius.agent.observability.execution.AgentExecutionResult;
import com.reviewgenius.agent.orchestrator.AgentOrchestrator;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agent")
@AllArgsConstructor
public class AgentController {

  private final AgentOrchestrator orchestrator;

  @PostMapping("/review")
  public ResponseEntity<AgentExecutionResult> reviewPR(
      @Valid @RequestBody ReviewRequestDto input,
      @RequestParam(defaultValue = "false") boolean debug) {
    return ResponseEntity.ok(orchestrator.runAgent(input, debug));
  }
}