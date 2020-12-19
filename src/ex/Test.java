package ex;

import java.io.IOException;
import java.util.ArrayList;

public class Test {


//	public static void main(String[] args) throws IOException {

		
		//test(" left left 10 forward 34 right 34");
		//testParser(" repeat 5[left left 10 forward 34 right 34]");
		
		//testParser( "repeat 20 [ right 12 left 23 repeat 13 [ left 19] left 5 left 23]");
/**				testParser("repeat 12 [\r\n" + 
						"forward 20\r\n" + 
						"right 30\r\n" + 
						"\r\n" + 
						"repeat 6[\r\n" + 
						"forward 2\r\n" + 
						"left 70\r\n" + 
						"]\r\n" + 
						"\r\n" + 
						"repeat 12[\r\n" + 
						"forward 4\r\n" + 
						"right 10\r\n" + 
						"]\r\n" + 
						"left 10\r\n" + 
						"]");
		/**
		**/	
		
		//testParser("repeat 5 [left 50 left 10] right 34");
		//testParser(" left 50 left 10 forward 34 right 34");
//	}



	public static void testParser(String s) {
		SourceReader sr = new SourceReader(s);
		ArrayList<Token> tokens = new Lexer().lexer(sr);
		Node expr;
		try {
			expr = new AnalyseSyntaxique().parser(tokens);
			Interpreter.printAst(expr, 0);
			//Interpreter.eval(expr);
			//System.out.println("Résultat = " + r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void test(String entree) {
		System.out.println();
		SourceReader sr = new SourceReader(entree);
		ArrayList<Token> tokens = new Lexer().lexer(sr);
		for(Token t: tokens) {
			System.out.println(t);
		}

		
	}

	public static void testParser1(String s) {
		SourceReader sr = new SourceReader(s);
		System.out.println("------------------------------ PARSER --------------------------------------");
		ArrayList<Token> tokens = new Lexer().lexer(sr);
		try {
			new AnalyseSyntaxique().parser(tokens);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("------------------------------ FIN --------------------------------------");
	}



}
