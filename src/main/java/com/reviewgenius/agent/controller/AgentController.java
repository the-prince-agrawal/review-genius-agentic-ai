package com.reviewgenius.agent.controller;

import com.reviewgenius.agent.model.ReviewRequestDto;
import com.reviewgenius.agent.orchestrator.AgentOrchestrator;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agent")
public class AgentController {

  private final AgentOrchestrator orchestrator;

  public AgentController(AgentOrchestrator orchestrator) {
    this.orchestrator = orchestrator;
  }

  @PostMapping("/review")
  public ResponseEntity<String> reviewPR(@Valid @RequestBody ReviewRequestDto input) {
    return ResponseEntity.ok(orchestrator.runAgent(input));
  }
}