package ex;

import java.util.ArrayList;

public class AnalyseSyntaxique {

	/**
     * tableau de transition
     */
	static Integer transitions[][] = {
			 //             espace lettre chiffre   [     ]         autre
			/*  0 */    {      0,     1,      2,  101,  102,         null      },
			/*  1 */    {    201,     1,      1,  201,  201,          null      },
			/*  2 */    {    202,   202,      2,  202,  202,         null      },			
			

			// 101 accepte [                        (goBack : non)
			// 102 accepte ]                        (goBack : non)                     
                       
		
			// 201 accepte identifiant ou mot clé   (goBack : oui)
			// 202 accepte entier                   (goBack : oui)


	};

	static final int ETAT_INITIAL = 0;

	/**
	 * Retourne l'indice de chaque symbole en rapport avec
	 * le tableau de transition au dessus
	 * @param c
	 * @return int
	 */
	private int indiceSymbole(Character c) {
		
		try {
			if (c == null) return 0;
			if (Character.isWhitespace(c)) return 0;
			if (Character.isLetter(c)) return 1;
			if (Character.isDigit(c)) return 2;
			if (c == '[') return 3;
			if (c == ']') return 4;
			else
				return 5;
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Désolé, le symbole n'est pas dans l'alphabet...");
		}
		return -1;
	}
	
	/**
	 * permet d'attribuer des Tokens aux différents éléments lus
	 * @param sr
	 * @return
	 */
	public ArrayList<Node> lexer(SourceReader sr) {
		ArrayList<Node> Nodes = new ArrayList<Node>();
		String buf="";
		int etat = ETAT_INITIAL;
		
		while (true) {
			Character c = sr.lectureSymbole();
			Integer e = transitions[etat][indiceSymbole(c)];
			if (e == null) {
				System.out.println(" pas de transition depuis état " + etat + " avec symbole " + c);
				return new ArrayList<Node>(); // renvoie une liste vide
			}
			if (e >= 100) {
				if (e == 101) {
					System.out.println("Accepte [");
					Nodes.add(new Node(NodeClass.nProc, "["));
				} else if (e == 102) {
					System.out.println("Accepte ]");
					Nodes.add(new Node(NodeClass.nProc, "]"));
					sr.goBack();
				} else if (e == 103) {
					System.out.println("Accepte la définition de la procédure ident  " + buf);
					
					if(buf.contains("repeat") == true) Nodes.add(new Node(NodeClass.nRepeat, buf));
					else if(buf.contains("forward") == true) Nodes.add(new Node(NodeClass.nForward, buf));
					else if(buf.contains("right") == true) Nodes.add(new Node(NodeClass.nRight, buf));
					else if(buf.contains("left") == true) Nodes.add(new Node(NodeClass.nLeft, buf));
					else if(buf.contains("color") == true) Nodes.add(new Node(NodeClass.nColor, buf));
					
					sr.goBack();
				} else if (e == 202) {
					System.out.println("Accepte intVal " + buf);
					Nodes.add(new Node(NodeClass.nBlock, buf));
					sr.goBack();
				}
				etat = 0;
				buf = "";
			} else {
				etat = e;
				if (etat>0) buf = buf + c;
			}
			if (c==null) break;
		}
		return Nodes;
	}
}
