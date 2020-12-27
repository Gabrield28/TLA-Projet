package ex;

import java.util.ArrayList;


public class AnalyseSyntaxique {

	private int pos;
	private int profondeur;
	private ArrayList<Token> tokens;

	/**
	 * @param tokens
	 * @throws Exception
	 */
	public Node parser(ArrayList<Token> tokens) throws Exception {
		this.tokens = tokens;
		pos = 0;
		Node expr = S();
		System.out.println("Fin atteinte = " + (pos == tokens.size()));
		return expr;
	}

	/*
	 * 
	 * méthodes des symboles non terminaux
	 * 
	 */

	/**
	 * @return 
	 * @throws Exception
	 */
	private Node S() throws Exception {

		if (getTokenClass() == TokenClass.repeat) {

			// production S -> repeat intVal S'S

			Token ident = getToken();
			printNode("repeat"); // affiche la valeur int

			if (getTokenClass() == TokenClass.intVal) {

				Token tokIntVal = getToken();
				printNode(tokIntVal.getValue()); // affiche la valeur int
				//profondeur++;
				Node n = new Node(tokIntVal);
				n.appendNode(S_prime());
				Node n3 = S();
				if(n3 != null) {
					n3.prependNode(n);
					return n3;
				}else {
					return n;
				}
			} throw new Exception("intVal est attendu");
			//n1.appendNode(n2);
		}
		if(getTokenClass() == TokenClass.procedure) {
			
			// production S -> procedure ident S'S
			
			Token ident = getToken();
			printNode("procedure");

			if (getTokenClass() == TokenClass.ident) {

				Token name = getToken();
				printNode(name.getValue()); // affiche la valeur int
				//profondeur++;
				Node n = new Node(name);
				n.appendNode(S_prime());
				Node n3 = S();
				if(n3 != null) {
					n3.prependNode(n);
					return n3;
				}else {
					return n;
				}
			} throw new Exception("un nom de procédure est attendu");

		}

		if (getTokenClass() == TokenClass.right || getTokenClass() == TokenClass.left
				|| getTokenClass() == TokenClass.forward || getTokenClass() == TokenClass.color) {

			// production S -> AS

			profondeur++;
			Node n1 = A();
			//System.out.println("n2 : S() pas lancée");
			profondeur--;
			Node n2 = S();
			//System.out.println("n2 : S() lancée");

			if(n2 != null) {

				n2.prependNode(n1);
				return n2;
			}else {
				return n1;
			}
		}

		if (isEOF() || getTokenClass() == TokenClass.intVal || getTokenClass() == TokenClass.rightHook) {
			// production S -> epsilon
			return null;
		}
		
		throw new Exception("repeat, procedure, left, right, forward ou color est attendu");

	}


	private Node S_prime() throws Exception {

		if (getTokenClass() == TokenClass.leftHook) {

			// production S' -> [S]

			getToken();
			printNode("[");

			profondeur++;
			Node n1 = S();

			if (getTokenClass() == TokenClass.rightHook) {
				getToken();
				printNode("]");
				Node n3 = S();
				return n3;
			}

			throw new Exception("] attendu");

		}

		throw new Exception("[ attendu");
	}

	private Node A() throws Exception {

		if (getTokenClass() == TokenClass.right || getTokenClass() == TokenClass.left
				|| getTokenClass() == TokenClass.forward || getTokenClass() == TokenClass.color) {

			// production A -> left intval ou right intval ou forward intval ou color intval
			Token ident = getToken();
			printNode(ident.getValue()); // affiche le token forward, right, left ou color
			Node n = new Node(ident);

			if (getTokenClass() == TokenClass.intVal) {



				Token tokIntVal = getToken();
				printNode(tokIntVal.getValue()); // affiche la valeur int
				Node n1 = new Node(tokIntVal);
				return n1;
			}
			throw new Exception("int attendu");

		}
		throw new Exception("right, left, forward ou color attendu");
	}

	/*
	 * 
	 * autres méthodes
	 * 
	 */

	private boolean isEOF() {
		return pos >= tokens.size();
	}

	/*
	 * Retourne la classe du prochain token à lire SANS AVANCER au token suivant
	 */
	private TokenClass getTokenClass() {
		if (pos >= tokens.size()) {
			return null;
		} else {
			return tokens.get(pos).getCl();
		}
	}

	/*
	 * Retourne le prochain token à lire ET AVANCE au token suivant
	 */
	private Token getToken() {
		if (pos >= tokens.size()) {
			return null;
		} else {
			Token current = tokens.get(pos);
			pos++;
			return current;
		}

	}

	/**
	 * @param s
	 */
	private void printNode(String s) {
		for (int i = 0; i < profondeur; i++) {
			System.out.print("    ");
		}
		System.out.println(s);
	}
}
