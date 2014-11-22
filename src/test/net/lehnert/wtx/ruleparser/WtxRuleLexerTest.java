package test.net.lehnert.wtx.ruleparser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import net.lehnert.wtx.ruleparser.Token;
import net.lehnert.wtx.ruleparser.WtxRuleLexer;

import org.junit.Test;

public class WtxRuleLexerTest {

	@Test
	public void testLexer() throws IOException {
		WtxRuleLexer lexer = new WtxRuleLexer();
		lexer.setRule("=\"Hallo\"+ 20");
		printLexerTokens(lexer);
	}
	
	@Test
	public void testLexer2() throws IOException {
		WtxRuleLexer lexer = new WtxRuleLexer();
		lexer.setRule("=f_map_foo(TRIMLEFT(TRIMRIGHT(Foo: Bar: Baz)))");
		printLexerTokens(lexer);
	}

	@Test
	public void testLexerComment() throws IOException {
		WtxRuleLexer lexer = new WtxRuleLexer();
		lexer.setRule("=//f_map_foo(TRIMLEFT(TRIMRIGHT(Foo: Bar: Baz)))");
		printLexerTokens(lexer);
	}
	
	@Test
	public void testLexerComment2() throws IOException {
		WtxRuleLexer lexer = new WtxRuleLexer();
		lexer.setRule("=/* mal sehen */f_map_foo(TRIMLEFT(TRIMRIGHT(Foo: Bar: Baz, 23, 42.4E19, 4E10)))");
		printLexerTokens(lexer);
	}
	
	private void printLexerTokens(WtxRuleLexer lexer) throws IOException {
		Token token;
		while((token = lexer.getNextToken()).getType() != Token.Type.EOF) {
			System.out.println(token);
		}
	}

}
