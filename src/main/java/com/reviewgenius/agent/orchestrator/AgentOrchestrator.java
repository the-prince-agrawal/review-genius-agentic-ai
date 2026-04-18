package com.reviewgenius.agent.orchestrator;

import com.reviewgenius.agent.model.AgentContext;

public class AgentOrchestrator {
    public String runAgent(String input) {

        AgentContext context = new AgentContext();
        context.setInput(input);

        int maxSteps = 5;

        for (int i = 0; i < maxSteps; i++) {

            String thought = think(context);
            String action = act(thought);
            String observation = observe(action);

            context.getSteps().add(
                    "Thought: " + thought +
                            " | Action: " + action +
                            " | Observation: " + observation
            );

            if (isDone(context)) {
                break;
            }
        }

        return formatResponse(context);
    }
    private String think(AgentContext context) {

        if (context.getSteps().isEmpty()) {
            return "Need to fetch code";
        } else if (context.getSteps().size() == 1) {
            return "Analyze code";
        } else {
            return "Generate review";
        }
    }
    private String act(String thought) {

        if (thought.contains("fetch")) {
            return "Calling GitHub API (simulated)";
        } else if (thought.contains("Analyze")) {
            return "Calling LLM (simulated)";
        } else {
            return "Formatting review";
        }
    }
    private String observe(String action) {

        if (action.contains("GitHub")) {
            return "Code fetched successfully";
        } else if (action.contains("LLM")) {
            return "Analysis complete";
        } else {
            return "Review ready";
        }
    }

    private boolean isDone(AgentContext context) {
        return context.getSteps().size() >= 3;
    }

    private String formatResponse(AgentContext context) {
        return String.join("\n", context.getSteps());
    }
}

