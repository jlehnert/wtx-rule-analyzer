package net.lehnert.wtx.ruleparser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ListNode implements ASTNode {
	private List<ASTNode> listMembers;
	
	public ListNode() {
		listMembers = new ArrayList<ASTNode>();
	}

	public void add(ASTNode node) {
		listMembers.add(node);
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer("LIST(");
		Iterator<ASTNode> iter = listMembers.iterator();
		while (iter.hasNext()) {
			ASTNode node = (ASTNode) iter.next();
			buf.append(node);
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
		for(ASTNode node: listMembers) {
			result.addAll(node.evaluateAllPaths(pathContext));
		}
		return result;
	}
	
}
