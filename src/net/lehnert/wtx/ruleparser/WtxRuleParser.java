package net.lehnert.wtx.ruleparser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.lehnert.wtx.ruleparser.Token.Type;

public class WtxRuleParser {
	private WtxRuleLexer lexer;
	private ArrayList<Token> savedTokens;

	public WtxRuleParser() {
		this.lexer = new WtxRuleLexer();
		this.savedTokens = new ArrayList<Token>();
	}
	
	public List<String> getAllPathsOfRule(String rule, PathContext pathContext) throws WtxParseException, IOException {
		ASTNode node = parseRule(rule);
		return node.evaluateAllPaths(pathContext);
	}
	
	public ASTNode parseRule(String rule) throws WtxParseException, IOException {
		lexer.setRule(rule);
		savedTokens.clear();
		Token token = lexer.getNextToken();
		if (token.getType() != Token.Type.EQUALS) {
			throw new WtxParseException("Rule needs to start with =");
		}
		return parseExpression();
	}

	/*
	 * Grammar for WTX Rules
	 * 
	 * WARNING: THIS GRAMMAR AND THE PARSER DO NOT RESPECT OPERATOR PRECEDENCE!
	 * 
	 * rule          := "=" expression
	 * expression    := number 
	 *               |  string
	 *               |  atom "(" parameterset ")"
	 *               |  path
	 *               | "(" expression ")"
	 *               |  expression "+" expression
	 *               |  expression "-" expression
	 *               |  expression "*" expression
	 *               |  expression "/" expression
	 *               |  expression "|" expression
	 *               |  expression "&" expression
	 *               |  expression "=" expression
	 *               |  expression "!=" expression
	 *               |  "{" listmemberset "}"
	 * atom          := "A-Za-z" ("A-Za-z0-9_")
	 * parameterset  := nil
	 *               |  expression
	 *               |  expression ("," expression)+
	 * path          := atom
	 *               |  atom (":" atom)+
	 * listmemberset := number ("," number)+
	 *               |  string ("," string)+                                    
	 * string        := "\"" ([^"])+ "\""
	 * 
	 * The lexer filters whitespace and comments
	 */
	
	private ASTNode parseExpression() throws IOException, WtxParseException {
		ASTNode node = null;
		Token token = nextToken();
		if (token.getType() == Token.Type.ATOM) {
		    Token token2 = nextToken();
		    saveToken(token);
		    saveToken(token2);
		    if (token2.getType() == Token.Type.LPAREN) {
		    	node = parseFunction();
			} else {			
				node = parsePath();
			}		    
		} else if (token.getType() == Token.Type.LPAREN) { 
			node = parseExpression();
			nextToken(Token.Type.RPAREN);
			return node;
		} else if (token.getType() == Token.Type.LCURLY) {
			saveToken(token);
			return parseList();
		} else if (token.getType() == Token.Type.NUMBER) {
			node = new NumberNode(token.getValue());
		} else if (token.getType() == Token.Type.STRING) {
			node = new StringNode(token.getValue());
		} else {
			throw new WtxParseException("Unimplemented token "+token);
		}
		token = nextToken();
		if (token.getType() == Token.Type.PLUS |
			token.getType() == Token.Type.MINUS |
			token.getType() == Token.Type.STAR |
			token.getType() == Token.Type.SLASH |
			token.getType() == Token.Type.OR |
			token.getType() == Token.Type.AND |
			token.getType() == Token.Type.EQUALS |
			token.getType() == Token.Type.NOT_EQUALS |
			(token.getType() == Token.Type.ATOM && token.getValue().equals("IN"))) { 
			ASTNode node2 = parseExpression();
			BinaryOperatorNode binaryOperatorNode = new BinaryOperatorNode(token.getValue(), node, node2);
			return binaryOperatorNode;
		} else if (token.getType() == Token.Type.COMMA) {
			saveToken(token);
			return node;
		} else if (token.getType() == Token.Type.RPAREN) {
			saveToken(token);
			return node;
		} else if (token.getType() == Token.Type.EOF) {
			return node;
		} else if (token.getType() == Token.Type.ATOM) {
			saveToken(token);
			return node;
		} else {
			throw new WtxParseException("Expected binary operator, got "+token.getType()+" ["+token.getValue()+"]");
		}
	}

	private ASTNode parseList() throws WtxParseException, IOException {
		nextToken(Token.Type.LCURLY);
		Token firstToken = nextToken();	
		if (firstToken.getType() != Token.Type.NUMBER && 
			firstToken.getType() != Token.Type.STRING) {
			throw new WtxParseException("Expected number or string, got "+firstToken);
		}
		saveToken(firstToken);
		ListNode listNode = new ListNode();
		while(true) {
			Token next = nextToken();
			if (next.getType() == Token.Type.NUMBER) {
				listNode.add(new NumberNode(next.getValue()));
			} else if (next.getType() == Token.Type.STRING) {
				listNode.add(new StringNode(next.getValue()));
			} else if (next.getType() == Token.Type.COMMA) {
				continue;
			} else if (next.getType() == Token.Type.RCURLY) {
				return listNode;
			} else {
				throw new WtxParseException("Expected , or }, got "+next);
			}
		}		
	}

	private ASTNode parsePath() throws WtxParseException, IOException {
		Token firstAtom = nextToken(Token.Type.ATOM);
		PathNode pathNode = new PathNode(firstAtom.getValue());
		Token next = nextToken();
		if (next.getType() == Token.Type.COLON) {
			while(true) {
				Token nextAtom = nextToken(Token.Type.ATOM);
				pathNode.addToPath(nextAtom.getValue());
				next = nextToken();
				if (next.getType() != Token.Type.COLON) {
					saveToken(next);
					return pathNode;
				}
			}
		} else {
			saveToken(next);
			return pathNode;
		}
	}

	private ASTNode parseFunction() throws WtxParseException, IOException {
		Token functionToken = nextToken(Token.Type.ATOM);		
		Token lparen = nextToken(Token.Type.LPAREN);
		FunctionNode functionNode = new FunctionNode(functionToken.getValue());
		Token rparen = nextToken();
		if (rparen.getType() == Token.Type.RPAREN) {
			return functionNode;
		} else {
			saveToken(rparen);
			while(true) {
				ASTNode parameterNode = parseExpression();
				functionNode.addParameter(parameterNode);
				Token comma = nextToken();
				if (comma.getType() == Token.Type.COMMA) {
					continue;
				} else if (comma.getType() == Token.Type.RPAREN) {
					return functionNode;
				} else {
					throw new WtxParseException("Expected comma or rparen, got "+comma);
				}
			}
			
		}
	}

	private Token nextToken(Type type) throws WtxParseException, IOException {
		Token token = nextToken();
		if (token.getType() != type) {
			throw new WtxParseException("Expected token of type "+type);
		}
		return token;
	}

	private void saveToken(Token token) {
		savedTokens.add(token);
	}

	private Token nextToken() throws IOException {
		if (savedTokens.isEmpty()) {
			return lexer.getNextToken();
		} else {
			return savedTokens.remove(0);
		}		
	}
}