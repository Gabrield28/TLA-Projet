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

	/**
	/**
	 * Méthode des symboles non terminaux
	 * @return 
	 * @throws Exception
	 */
	private Node S() throws Exception {

		if (getTokenClass() == TokenClass.repeat) {

			// production S -> repeat S"

			Token t = getToken();
			printNode("repeat");
			profondeur++;
			Node n = new Node(t);
			n.appendNode(S_second());
			profondeur--;
			return n;

			/**
			Token tokRepeat = getToken();
			printNode("repeat");  //affiche repeat
			profondeur++;
			Node n1 = new Node(tokRepeat);
			n1.appendNode(S_second());
			profondeur--;
			return n1;
			 **/

		}

		if (getTokenClass() == TokenClass.right || getTokenClass() == TokenClass.left
				|| getTokenClass() == TokenClass.forward) {

			// production S -> AS

			//System.out.println("token de direction ");
			profondeur++;
			Node n1 = A();
			profondeur--;
			Node n2 = S();
			if (n2 != null) {
				n2.prependNode(n1);
				return n2;
			} else {
				return n1;
			}
		}

		/**
			profondeur++;
			Node n1 = A();
			profondeur--;
			Node n2 = S();
			if(n2 != null) {
				n2.prependNode(n1);
				return n2;
			}else {
				return n1;
			}

		}**/

		if(isEOF()|| getTokenClass() == TokenClass.rightHook) { //

			// production S -> epsilon
			if(isEOF()) {
				System.out.println("end of file");
				return null;
			}else if(getTokenClass() == TokenClass.rightHook) {
				
				//Token t = getToken();
				//printNode("]");
				//profondeur--;
				//Node n5 = new Node(t);
				return null;
				
			}
			
			
		}throw new Exception("repeat, forward, left, right ou epsilon était attendu.");



	}

	private Node S_second() throws Exception {

		if (getTokenClass() == TokenClass.intVal) {

			// production S" -> intVal S'

			Token tokIntVal = getToken();
			printNode(tokIntVal.getValue());  //affiche la valeur du token intVal
			profondeur++;
			Node n1 = new Node(tokIntVal);
			n1.appendNode(S_prime());
			Node n2 = S_prime();
			n2.prependNode(n1);
			System.out.println("S donne intval S'");

		}

		throw new Exception("intVal ou [ attendu");

	}

	private Node S_prime() throws Exception {

		if (getTokenClass() == TokenClass.leftHook) {

			// production S' -> [ SS ] 

			//System.out.println("je viens de voir un [");
			Token t = getToken();
            printNode("[");
            profondeur++;
            Node n = new Node(t);
            Node n1 = S();
            n.appendNode(n1);
            profondeur--;
            
			//getToken();
			
			Node n2 = S();
			profondeur++;
			n1.appendNode(n2);
			if (getTokenClass() == TokenClass.rightHook) {
				//System.out.println("je rentre dans la boucle ");
				
				Token t1 = getToken();
	            printNode("]");
	            profondeur++;
	            Node n4 = new Node(t1);
	            //n4.appendNode(S());
	            //profondeur--;
	            /**
				Node n3 = S();
				if (n3 != null) {
					n3.prependNode(n4);
					return n3;
				} else {
					return n4;
				
				}*/
			}throw new Exception("] attendu ");
			
			
		}throw new Exception(" [ attendu");

		// production S' -> [S]
		/**
			Token lHook = getToken();
			printNode("[");
            profondeur++;
            Node n1 = new Node(lHook);
            n1.appendNode(S());
            Node n2 = S();
            profondeur--;

            if(getTokenClass() == TokenClass.rightHook) {
				Token rHook = getToken();
				printNode("[");
	            profondeur++;
	            Node n3 = new Node(lHook);
	            n2.appendNode(n3);
			} throw new Exception("] attendu");



			Token rHook = getToken();
			printNode("[");

			profondeur++;
			Node n1 = new Node(rHook);
			n1.appendNode(S());
			Node n2 = S();
			profondeur--;
			//Node n3 = S();
			//n2.appendNode(n3);

			if (getTokenClass() == TokenClass.rightHook) {

				getToken();
				printToken("]");
				Node n4 = S();
				return n4;

			}throw new Exception(" ] attendu");
		 **/
		//}throw new Exception(" [ attendu");


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
			throw new Exception("right, left, forward ou color attendu");
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
		return pos >= tokens.size();
	}

	/*
	 * Retourne la classe du prochain Token à lire SANS AVANCER au Token suivant
	 */
	private TokenClass getTokenClass() {
		if (pos >= tokens.size()) {
			return null;
		} else {
			return tokens.get(pos).getCl();
		}
	}

	/*
	 * Retourne le prochain Token à lire ET AVANCE au Token suivant
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
	private void printToken(String s) {
		for (int i = 0; i < profondeur; i++) {
			System.out.print("    ");
		}
		System.out.println(s);
	}

	private void printNode(String s) {
		for(int i=0;i<profondeur;i++) {
			System.out.print("    ");
		}
		System.out.println(s);
	}
}
