package net.lehnert.wtx.ruleparser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class NumberNode implements ASTNode {
	private BigDecimal number;
	
	public NumberNode(String number) {
		this.number = new BigDecimal(number);
	}
	
	@Override
	public String toString() {
		return "NUMBER("+number+")";
	}

	@Override
	public List<String> evaluateAllPaths(PathContext pathContext) {
		return new ArrayList<String>();
	}
}
