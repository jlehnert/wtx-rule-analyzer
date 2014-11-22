package net.lehnert.wtx.ruleparser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FunctionNode implements ASTNode {
	private String functionName;
	private List<ASTNode> parameterList;
	
	public FunctionNode(String functionName) {
		this.functionName = functionName;
		this.parameterList = new ArrayList<ASTNode>();
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	public void addParameter(ASTNode node) {
		parameterList.add(node);
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("FUNCTION(");
		buf.append(functionName).append(",");
		Iterator<ASTNode> iter = parameterList.iterator();
		while (iter.hasNext()) {
			ASTNode astNode = (ASTNode) iter.next();
			buf.append(astNode);
			if (iter.hasNext()) {
				buf.append(",");
			}
		}
		buf.append(")");
		return buf.toString();
	}

	@Override
	public List<String> evaluateAllPaths(PathContext pathContext) {
		List<String> result = new ArrayList<String>();
		for(ASTNode node: parameterList) {
			result.addAll(node.evaluateAllPaths(pathContext));
		}
		return result;
	}
}
