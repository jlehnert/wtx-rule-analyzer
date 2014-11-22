package net.lehnert.wtx.ruleparser;

import java.util.ArrayList;
import java.util.List;

public class StringNode implements ASTNode {
	private String value;
	
	public StringNode(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "STRING("+value+")";
	}

	@Override
	public List<String> evaluateAllPaths(PathContext pathContext) {		
		return new ArrayList<String>();
	}
}
