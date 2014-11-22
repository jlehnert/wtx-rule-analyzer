package test.net.lehnert.wtx.ruleparser;

import java.io.IOException;
import java.util.List;

import net.lehnert.wtx.ruleparser.ASTNode;
import net.lehnert.wtx.ruleparser.PathContext;
import net.lehnert.wtx.ruleparser.WtxParseException;
import net.lehnert.wtx.ruleparser.WtxRuleParser;

import org.junit.Test;

public class WtxRuleParserTest {

	@Test
	public void test() throws WtxParseException, IOException {
		WtxRuleParser wtxRuleParser = new WtxRuleParser();
		ASTNode node = wtxRuleParser.parseRule("=\"Hallo\"+ 20");
		System.out.println(node);
	}

	@Test
	public void test2() throws WtxParseException, IOException {
		WtxRuleParser wtxRuleParser = new WtxRuleParser();
		ASTNode node = wtxRuleParser.parseRule("=/* mal sehen */f_map_foo(TRIMLEFT(TRIMRIGHT(Foo: Bar: Baz)))");
		System.out.println(node);
	}
	
	@Test
	public void test3() throws WtxParseException, IOException {
		WtxRuleParser wtxRuleParser = new WtxRuleParser();
		ASTNode node = wtxRuleParser.parseRule("=f_map_foo(Foo, \"Bar\")");
		System.out.println(node);
	}
	
	@Test
	public void test4() throws WtxParseException, IOException {
		WtxRuleParser wtxRuleParser = new WtxRuleParser();
		ASTNode node = wtxRuleParser.parseRule("=IF(Bar: Baz = \"Foo\" | Bar: Baz = 23.5, \"Foo\", f_map_foo(Foo, \"Bar\"))");
		System.out.println(node);
	}
	
	@Test
	public void test5() throws WtxParseException, IOException {
		WtxRuleParser wtxRuleParser = new WtxRuleParser();
		ASTNode node = wtxRuleParser.parseRule("=IF(MEMBER(Bar: Baz, { 123, 456 }), \"Foo\", \"Bar\")");
		System.out.println(node);
	}
	
	@Test
	public void test6() throws WtxParseException, IOException {
		WtxRuleParser wtxRuleParser = new WtxRuleParser();
		ASTNode node = wtxRuleParser.parseRule("=(1+2)");
		System.out.println(node);
	}
	
	@Test
	public void testAnalyzePaths() throws WtxParseException, IOException {
		WtxRuleParser wtxRuleParser = new WtxRuleParser();
		List<String> result = wtxRuleParser.getAllPathsOfRule("=(1+2)", new PathContext());
		printAllPaths(result);
	}
	
	@Test
	public void testAnalyzePaths2() throws WtxParseException, IOException {
		WtxRuleParser wtxRuleParser = new WtxRuleParser();
		List<String> result = wtxRuleParser.getAllPathsOfRule("=IF(MEMBER(Bar: Baz, { 123, 456 }), \"Foo\", \"Bar\")", new PathContext());
		printAllPaths(result);
	}
	
	@Test
	public void testAnalyzePaths3() throws WtxParseException, IOException {
		WtxRuleParser wtxRuleParser = new WtxRuleParser();
		List<String> result = wtxRuleParser.getAllPathsOfRule("=IF(Bar: Baz = \"Foo\" | Bar: Baz = 23.5, \"Foo\", f_map_foo(Foo, \"Bar\"))", new PathContext());
		printAllPaths(result);
	}
	
	@Test
	public void testAnalyzePaths4() throws WtxParseException, IOException {
		WtxRuleParser wtxRuleParser = new WtxRuleParser();
		PathContext pathContext = new PathContext();
		pathContext.addPrefixMapping("Baz", "Faz:Froz:Fruzz");
		List<String> result = wtxRuleParser.getAllPathsOfRule("=IF(Bar: Baz = \"Foo\" | Bar: Baz = 23.5, \"Foo\", f_map_foo(Foo, \"Bar\"))", pathContext);
		printAllPaths(result);
	}

	private void printAllPaths(List<String> result) {
		if (result.isEmpty()) {
			System.out.println("NO PATHS");
		} else {
			for(String s: result) {
				System.out.println(s);
			}
		}
	}
}
