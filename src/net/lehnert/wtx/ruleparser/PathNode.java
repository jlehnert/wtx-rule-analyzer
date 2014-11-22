package net.lehnert.wtx.ruleparser;

import java.util.ArrayList;
import java.util.List;

public class PathNode implements ASTNode {
	private String path;
	
	public PathNode(String path) {
		this.path = path;
	}
	
	public void addToPath(String path) {
		this.path = this.path + ":" + path;
	}
	
	@Override
	public String toString() {
		return "PATH("+path+")";
	}

	@Override
	public List<String> evaluateAllPaths(PathContext pathContext) {
		List<String> result = new ArrayList<String>();
		String[] parts = path.split(":");
		String prefix = parts[parts.length-1];
		if (pathContext.containsPrefix(prefix)) {
			StringBuffer buf = new StringBuffer();
			for(int i=0; i<parts.length-1; i++) {
				buf.append(parts[i]);
				buf.append(":");
			}
			buf.append(pathContext.getMappingForPrefix(prefix));
			result.add(buf.toString());
		} else {
			result.add(path);
		}
		return result;
	}
}
