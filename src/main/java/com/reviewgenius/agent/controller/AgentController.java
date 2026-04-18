package com.reviewgenius.agent.controller;

import com.reviewgenius.agent.orchestrator.AgentOrchestrator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/agent")
public class AgentController {

  private final AgentOrchestrator orchestrator;

  public AgentController(AgentOrchestrator orchestrator) {
    this.orchestrator = orchestrator;
  }

  @GetMapping("/run")
  public ResponseEntity<String> run(@RequestParam String input) {
    return ResponseEntity.ok(orchestrator.runAgent(input));
  }
}