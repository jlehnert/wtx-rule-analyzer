package net.lehnert.wtx.ruleparser;

import java.util.HashMap;

public class PathContext {
	private HashMap<String, String> prefixMapping;
	
	public PathContext() {
		this.prefixMapping = new HashMap<String, String>();
	}
	
	public void addPrefixMapping(String prefix, String path) {
		prefixMapping.put(prefix, path);
	}
	
	public boolean containsPrefix(String prefix) {
		return prefixMapping.containsKey(prefix);
	}

	public String getMappingForPrefix(String prefix) {
		return prefixMapping.get(prefix);
	}
}
