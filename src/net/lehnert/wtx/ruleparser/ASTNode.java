package net.lehnert.wtx.ruleparser;

import java.util.List;

public interface ASTNode {
	public List<String> evaluateAllPaths(PathContext pathContext);
}
