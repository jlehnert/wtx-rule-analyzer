package net.lehnert.wtx.ruleparser;

import java.util.ArrayList;
import java.util.List;

public class BinaryOperatorNode implements ASTNode {
	private String operatorType;
	private ASTNode arg1;
	private ASTNode arg2;
	public BinaryOperatorNode(String operatorType, ASTNode arg1, ASTNode arg2) {
		this.operatorType = operatorType;
		this.arg1 = arg1;
		this.arg2 = arg2;
	}
	
	@Override
	public String toString() {
		return "BINARY OP("+ operatorType+","+arg1 +"," + arg2+")";
	}

	@Override
	public List<String> evaluateAllPaths(PathContext pathContext) {
		List<String> result = new ArrayList<String>();
		result.addAll(arg1.evaluateAllPaths(pathContext));
		result.addAll(arg2.evaluateAllPaths(pathContext));
		return result;
	}
}
