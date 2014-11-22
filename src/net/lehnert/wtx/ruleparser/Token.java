package net.lehnert.wtx.ruleparser;

public class Token {
	private String value;
	private Type type;
	
	public Token(Type type, String value) {
		this.type = type;
		this.value = value;
	}
	
	public Type getType() {
		return type;
	}
	
	public String getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return type.name()+"='"+value+"'";
	}
	
	public enum Type {
		LPAREN, RPAREN, COMMA, ATOM, STRING, COLON, LCURLY, RCURLY, EQUALS, 
		PLUS, MINUS, STAR, SLASH,
		EOF, ERROR, NUMBER, NOT_EQUALS, EXCLAMATION_MARK, AND, OR, 
	}
}
