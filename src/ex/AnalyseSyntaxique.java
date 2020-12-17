package ex;

import java.util.ArrayList;


public class AnalyseSyntaxique {

	private int pos;
	private int profondeur;
	private ArrayList<Token> Tokens;

	/**
	 * @param Tokens
	 * @throws Exception
	 */
	public Node parser(ArrayList<Token> Tokens) throws Exception {
		this.Tokens = Tokens;
		pos = 0;
		Node expr = S();
		
		System.out.println("Fin atteinte = " + (pos == Tokens.size()));
		return expr;
	}

    
	/**
	 * Méthode des symboles non terminaux
	 * @return 
	 * @throws Exception
	 */
	private Node S() throws Exception {

		if (getTokenClass() == TokenClass.repeat) {

			// production S -> repeat S"S

			Token ident = getToken();

			printToken(ident.getValue()); // affiche la valeur int
			S_second();
			profondeur--;
			S();

			return null;
		}

		if (getTokenClass() == TokenClass.right || getTokenClass() == TokenClass.left
				|| getTokenClass() == TokenClass.forward) {

			// production S -> AS

			profondeur++;
			A();
			profondeur--;
			S();

			return null;
		}
		return null;

	}

	private Node S_second() throws Exception {

		if (getTokenClass() == TokenClass.intVal) {

			// production S" -> intVal S'

			Token tokIntVal = getToken();

			printToken(tokIntVal.getValue()); // affiche la valeur int
			//S_prime();
			return null;
		}

		throw new Exception("intVal ou [ attendu");

	}

	private Node S_prime() throws Exception {

		if (getTokenClass() == TokenClass.leftHook) {

			// production S' -> [SS]

			getToken();
			printToken("[");

			profondeur++;
			S();

			profondeur--;
			S();

			if (getTokenClass() == TokenClass.rightHook) {
				getToken();
				printToken("]");
				S();
				return null;
			}

			throw new Exception("[ attendu");

		}

		throw new Exception(" ] attendu");
	}

	private Node A() throws Exception {

		if (getTokenClass() == TokenClass.right || getTokenClass() == TokenClass.left
				|| getTokenClass() == TokenClass.forward || getTokenClass() == TokenClass.color) {

			// production A -> left n ou right n ou forward n ou color n

			printToken(getToken().getValue());
			
			if (getTokenClass() == TokenClass.intVal) {



				Token tokIntVal = getToken();
				printToken(tokIntVal.getValue()); // affiche la valeur n

				return null;
			}
			throw new Exception("entier attendu");
			
		}else {
			throw new Exception("right, left or forward attendu");
		}
		
	}

	/*
	 * --------- autres méthodes ---------- 
	 */

	/**
	 * END OF FILE
	 * @return
	 */
	private boolean isEOF() {
		return pos >= Tokens.size();
	}

	/*
	 * Retourne la classe du prochain Token à lire SANS AVANCER au Token suivant
	 */
	private TokenClass getTokenClass() {
		if (pos >= Tokens.size()) {
			return null;
		} else {
			return Tokens.get(pos).getCl();
		}
	}

	/*
	 * Retourne le prochain Token à lire ET AVANCE au Token suivant
	 */
	private Token getToken() {
		if (pos >= Tokens.size()) {
			return null;
		} else {
			Token current = Tokens.get(pos);
			pos++;
			return current;
		}
	}

	/**
	 * @param s
	 */
	private void printToken(String s) {
		for (int i = 0; i < profondeur; i++) {
			System.out.print("    ");
		}
		System.out.println(s);
	}

}
