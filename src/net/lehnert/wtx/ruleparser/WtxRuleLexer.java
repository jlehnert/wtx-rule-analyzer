package net.lehnert.wtx.ruleparser;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Pattern;

public class WtxRuleLexer {
	private InputStream inputStream;
	private enum LexerState {
		START, STRING, ATOM, ERROR, END, EOF, CHECK_NOT_EQUALS, CHECK_COMMENT, ONE_LINE_COMMENT, MULTI_LINE_COMMENT, MULTI_LINE_COMMENT_CHECK_END, WHITESPACE, NUMBER, NUMBER_AFTER_DOT, NUMBER_AFTER_E
	}
	private LexerState state;
	private StringBuffer buf;
	private Pattern atomPattern = Pattern.compile("[A-Za-z0-9_]");
	private Pattern atomStartPattern = Pattern.compile("[A-Za-z]");
	
	public WtxRuleLexer() {
	}
	
	public void setRule(String rule) {
		this.state = LexerState.START;
		this.buf = new StringBuffer();
		try {
			this.inputStream = new BufferedInputStream(new ByteArrayInputStream(rule.getBytes("ISO-8859-1")));
		} catch (UnsupportedEncodingException ignored) {
			ignored.printStackTrace(); //FIXME
		}
	}
	
	public Token getNextToken() throws IOException {
		while(true) {
			inputStream.mark(1024);
			int i = inputStream.read();
			if (i==-1) {
				if (state != LexerState.ATOM &&
					state != LexerState.NUMBER &&
					state != LexerState.NUMBER_AFTER_DOT &&
					state != LexerState.NUMBER_AFTER_E) {
					return new Token(Token.Type.EOF, null);
				} else if (state == LexerState.NUMBER |
						   state == LexerState.NUMBER_AFTER_DOT |
						   state == LexerState.NUMBER_AFTER_E) {
					if (buf.length() > 0) {
						state = LexerState.EOF;
						return new Token(Token.Type.NUMBER, buf.toString());
					} else {
						return new Token(Token.Type.EOF, null);
					}
				} else if (state == LexerState.ATOM) {
					if (buf.length() > 0) {
						state = LexerState.EOF;
						return new Token(Token.Type.ATOM, buf.toString());
					} else {
						return new Token(Token.Type.EOF, null);
					}
				}
			}
			char c = (char) i;		
			if (state == LexerState.START) {
				if (c == '(') {
					return new Token(Token.Type.LPAREN, "(");
				} else if (c == ')') {
					return new Token(Token.Type.RPAREN, ")");
				} else if (c == '{') {
					return new Token(Token.Type.LCURLY, "{");
				} else if (c == '}') {
					return new Token(Token.Type.RCURLY, "}");
				} else if (c == ',') {
					return new Token(Token.Type.COMMA, ",");
				} else if (c == '+') {
					return new Token(Token.Type.PLUS, "+");
				} else if (c == '-') {
					return new Token(Token.Type.MINUS, "-");
				} else if (c == '*') {
					return new Token(Token.Type.STAR, "*");
				} else if (c == ':') {
					return new Token(Token.Type.COLON, ":");
				} else if (c == '|') {
					return new Token(Token.Type.OR, "|");
				} else if (c == '&') {
					return new Token(Token.Type.AND, "&");	
				} else if (c == '!') {
					state = LexerState.CHECK_NOT_EQUALS;
 				} else if (c == '/') {
					state = LexerState.CHECK_COMMENT;
					buf = new StringBuffer();
				} else if (c == '=') {
					return new Token(Token.Type.EQUALS, "=");
				} else if (Character.isWhitespace(c)) {
					state = LexerState.WHITESPACE;
				} else if (Character.isDigit(c)) {
					state = LexerState.NUMBER;
					buf = new StringBuffer();
					buf.append(c);
				} else if (atomStartPattern.matcher(String.valueOf(c)).matches()) {
					state = LexerState.ATOM;
					buf = new StringBuffer();
					buf.append(c);
				} else if (c == '\"') {
					state = LexerState.STRING;
					buf = new StringBuffer();
				}
			} else if (state == LexerState.CHECK_NOT_EQUALS) {
				if (c == '=') {
					state = LexerState.START;
					return new Token(Token.Type.NOT_EQUALS, "!=");
				} else {
					state = LexerState.START;
					inputStream.reset();
					return new Token(Token.Type.EXCLAMATION_MARK, "!");
				}
			} else if (state == LexerState.WHITESPACE) {
				if (Character.isWhitespace(c)) {
					// ignore
				} else {
					inputStream.reset();
					state = LexerState.START;									
				}
			} else if (state == LexerState.ATOM) {
				if (atomPattern.matcher(String.valueOf(c)).matches()) {
					buf.append(c);
				} else {
					inputStream.reset();
					state = LexerState.START;
					return new Token(Token.Type.ATOM, buf.toString());		
				}
			} else if (state == LexerState.STRING) {
				if (c == '\"') {
					state = LexerState.START;
					return new Token(Token.Type.STRING, buf.toString());		
				} else {
					buf.append(c);
				}
			} else if (state == LexerState.CHECK_COMMENT) {
				if (c == '/') {
					state = LexerState.ONE_LINE_COMMENT;
				} else if (c == '*') {
					state = LexerState.MULTI_LINE_COMMENT;
				} else {
					state = LexerState.START;
					inputStream.reset();
					return new Token(Token.Type.SLASH, "/");
				}
			} else if (state == LexerState.ONE_LINE_COMMENT) {
				if (c == '\n') {
					state = LexerState.START;
				} 
			} else if (state == LexerState.MULTI_LINE_COMMENT) {
				if (c == '*') {
					state = LexerState.MULTI_LINE_COMMENT_CHECK_END;
				}
			} else if (state == LexerState.MULTI_LINE_COMMENT_CHECK_END) {
				if (c == '/') {
					state = LexerState.START;
				} else {
					state = LexerState.MULTI_LINE_COMMENT;
				}
			} else if (state == LexerState.NUMBER) {
				if (Character.isDigit(c)) {
					buf.append(c);
				} else if (c == '.') {
					buf.append(c);
					state = LexerState.NUMBER_AFTER_DOT;
				} else if (c == 'E') {
					buf.append(c);
					state = LexerState.NUMBER_AFTER_E;
				} else {
					inputStream.reset();
					state = LexerState.START;
					return new Token(Token.Type.NUMBER, buf.toString());
				}
			} else if (state == LexerState.NUMBER_AFTER_DOT) {
				if (Character.isDigit(c)) {
					buf.append(c);
				} else if (c == 'E') {
					buf.append(c);
					state = LexerState.NUMBER_AFTER_E;
				} else {
					inputStream.reset();
					state = LexerState.START;
					return new Token(Token.Type.NUMBER, buf.toString());
				}
			} else if (state == LexerState.NUMBER_AFTER_E) {
				if (Character.isDigit(c)) {
					buf.append(c);
				} else {
					inputStream.reset();
					state = LexerState.START;
					return new Token(Token.Type.NUMBER, buf.toString());
				}
			} else {			
				state = LexerState.ERROR;
				return new Token(Token.Type.ERROR, null);
			}
		}
	}
}
