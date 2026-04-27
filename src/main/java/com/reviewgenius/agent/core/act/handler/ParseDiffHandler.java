package com.reviewgenius.agent.core.act.handler;

import com.reviewgenius.agent.core.act.ActionHandler;
import com.reviewgenius.agent.core.act.ActionResult;
import com.reviewgenius.agent.model.AgentContext;
import com.reviewgenius.agent.util.DiffParserUtil;
import org.springframework.stereotype.Service;

import static com.reviewgenius.agent.core.act.ActionResultStatus.SUCCESS;

@Service
public class ParseDiffHandler implements ActionHandler {
  @Override
  public ActionResult execute(AgentContext context) {
    String parsed = DiffParserUtil.parse(context.getRawDiff());
    context.setParsedDiff(parsed);
    return new ActionResult(SUCCESS, "Diff parsed", parsed);
  }
}
