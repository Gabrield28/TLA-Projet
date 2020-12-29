package analyseSyntaxique;

import java.util.ArrayList;
import analyseLexicale.Token;
import analyseLexicale.TokenClass;



public class AnalyseSyntaxique {

	static int pos;
	static int profondeur;
	static ArrayList<Token> tokens;
	static Node root = new Node(NodeClass.nBlock);

	/**
	 * @param tokens
	 * @throws Exception
	 */
	public static Node parser(ArrayList<Token> tokens) throws Exception {
		AnalyseSyntaxique.tokens = tokens;

		pos = 0;
		Node expr = B();
		System.out.println();
		System.out.println("Fin atteinte = " + (pos == tokens.size()));
		System.out.println();
		return expr;

	}

	/*
	 * méthodes des symboles non terminaux
	 */

	/**
	 * Cette methode permet de continuer à ajouter des noeuds tant qu'il y a des tokens
	 * @return
	 * @throws Exception
	 */
	private static Node B() throws Exception {

		// production B -> S | epsilon
		Node n = new Node(NodeClass.nBlock);

		while (!(getTokenClass() == TokenClass.rightHook || isEOL())) {

			n.appendNode(S());
		}

		getToken();
		
		return n;

	}

	/**
	 * S -> repeat int S'S | procedure ident S'S | call ident | AS
	 * @return 
	 * @throws Exception
	 */
	static Node S() throws Exception {

		//production S -> repeat int S'S

		if (getTokenClass() == TokenClass.repeat) {

			Node n = nextTokInt(NodeClass.nRepeat, "Un entier est attendu après repeat");
			n.appendNode(S_prime());
			return n;

		}

		//production S -> procedure ident S'S

		if (getTokenClass() == TokenClass.procedure) {

			Node n = nextTokStr(NodeClass.nProc, "Un nom de procedure est attendu après procedure");
			n.appendNode(S_prime());
			return n;

		}

		//production S -> call ident S
		
		if (getTokenClass() == TokenClass.call) {

			return nextTokStr(NodeClass.nCall, "Un nom de procedure valide est attendu après call");
		}


		if (getTokenClass() == TokenClass.right || getTokenClass() == TokenClass.left
				|| getTokenClass() == TokenClass.forward || getTokenClass() == TokenClass.color) {

			// production S -> AS

			Node n1 = A();
			return n1;
		}
		
		throw new Exception("repeat, procedure, call, left, right, forward ou color est attendu");

	}


	/**
	 * S' -> [B]
	 * @return
	 * @throws Exception
	 */
	static Node S_prime() throws Exception {

		// production S' -> [B]
		
		if (getTokenClass() == TokenClass.leftHook) {
			getToken();
			Node n1 = B();
			return n1;

		} else {
			Node n1 = S();
			return n1;
		}


	}

	/**
	 * A -> right intval | left intval | forward intval | color intval
	 * @return
	 * @throws Exception
	 */
	static Node A() throws Exception {

		if (getTokenClass() == TokenClass.right) {

			// production A -> right intval 
			
			return nextTokInt(NodeClass.nRight, "int attendu après right");
		}
		if (getTokenClass() == TokenClass.left) {

			// production A -> left intval
			
			return nextTokInt(NodeClass.nLeft, "int attendu après left");
		}
		if (getTokenClass() == TokenClass.forward) {

			// production A -> forward intval 

			return nextTokInt(NodeClass.nForward, "int attendu après forward");

		}
		if (getTokenClass() == TokenClass.color) {

			// production A -> color intval

			return nextTokInt(NodeClass.nColor, "int attendu après color");

		}
		throw new Exception("right, left, forward ou color attendu");
	}

	/*
	 * Les autres méthodes
	 */

	/**
	 * Pour un soucis d'optimisation j'ai choisi de créer ces 2 methodes qui 
	 * permettent de recuperer le token après les tokens call, procedure, color...
	 * et verifier si ces derniers sont valides.
	 */
	
	/**
	 * nextToInt permet de lire le token après les tokens repeat, color, forward, left et right
	 * @param tc
	 * @param mess est le message qui s'affiche lorsque le token suivant n'est pas integer
	 * @return
	 * @throws Exception
	 */
	private static Node nextTokInt(NodeClass x, String mess) throws Exception {

		getToken();

		if (getTokenClass() != TokenClass.intVal)
			throw new Exception(mess);

		String val = getToken().getValue();

		return new Node(x, val);
	}

	/**
	 * Même méthode que nextTokInt, sauf que les tokens suivant ne sont pas 
	 * des int mais des ident qui se trouvent apès les tokens procedure et call 
	 * @param tc
	 * @param mess est le message qui s'affiche lorsque le token suivant n'est pas integer
	 * @return
	 * @throws Exception
	 */
	private static Node nextTokStr(NodeClass x, String mess) throws Exception {

		getToken();

		if (getTokenClass() != TokenClass.ident)
			throw new Exception(mess);

		String val = getToken().getValue();

		return new Node(x, val);
	}

	/**
	 * return true si end of line apres un symbole
	 * @return
	 */
	static boolean isEOL() { 
		return pos >= tokens.size();
	}

	/*
	 * Retourne la classe du prochain token à lire SANS AVANCER au token suivant
	 */
	static TokenClass getTokenClass() {
		if (isEOL()) {
			return null;
		} else {
			return tokens.get(pos).getCl();
		}
	}

	/*
	 * Retourne le prochain token à lire ET AVANCE au token suivant
	 */
	static Token getToken() {
		if (isEOL()) {
			return null;
		} else {
			Token current = tokens.get(pos);
			pos++;
			return current;
		}
	}

}
