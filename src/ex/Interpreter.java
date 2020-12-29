package ex;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.*;
import analyseLexicale.Token;
import analyseLexicale.TokenClass;
import analyseLexicale.Lexer;
import analyseLexicale.SourceReader;
import analyseSyntaxique.AnalyseSyntaxique;
import analyseSyntaxique.Node;
import analyseSyntaxique.NodeClass;

public class Interpreter {

	final double initDirection = 180;

	double x;
	double y;
	double direction;
	Color color;

	Graphics gc;

	HashMap<String, Node> procedures;
	private ArrayList<Token> tokens;

	/**
	 * Tableau de 13 couleurs pour les couleurs
	 */
	Color colors[] = {
			Color.BLACK,
			Color.BLUE,
			Color.CYAN,
			Color.DARK_GRAY,
			Color.GRAY,
			Color.GREEN,
			Color.LIGHT_GRAY,
			Color.MAGENTA,
			Color.ORANGE,
			Color.PINK,
			Color.RED,
			Color.WHITE,
			Color.YELLOW
	};



	/**
	 * Constructeur de la classe interpreter et lancement des analyses
	 * @param s
	 * @param x
	 * @param y
	 * @param gc
	 * @throws Exception
	 */
	void interpreter(String s, double x, double y, Graphics gc) throws Exception {
		System.out.println();
		this.gc = gc;
		this.x = x;
		this.y = y;
		direction = initDirection;
		procedures = new HashMap();
		

		System.out.println("--------------------- Analyse Lexicale ---------------------");
		SourceReader sr = new SourceReader(s);
		tokens = new Lexer().lexer(sr);

		try {
			
			System.out.println("--------------------- Arbre Syntaxique Abstrait ---------------------");
			Node root = AnalyseSyntaxique.parser(tokens);
			printAst(root, 0);
			evalRoot(root);
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}


	/**
	 * Méthode evalRoot
	 * @param root
	 */
	void evalRoot(Node root) {
		Iterator<Node> it = root.getChildren();

		while (it.hasNext()) {

			Node n = it.next();

			if (n.getCl() == NodeClass.nProc) {
				procedures.put(n.getValue(), n);
			} else {
				eval(n);
			}
		}
	}

	/**
	 * méthode eval prend en paramètre un Noeud (ceux liés dans l'analyse syntaxique)
	 * @param n
	 */
	public void eval(Node n) {
		Iterator<Node> it = n.getChildren();

		switch (n.getCl()) {

		case nBlock:
			while (it.hasNext()) {
				eval(it.next());
			}
			break;

		case nForward:
			forward(Integer.valueOf(n.getValue()));
			break;

		case nLeft:
			direction = (direction + Integer.valueOf(n.getValue())) % 360;
			break;

		case nRight:
			direction = (direction - Integer.valueOf(n.getValue())) % 360;			
			break;

		case nRepeat:
			int count = Integer.valueOf(n.getValue());
			Node nodeToRepeat = it.next();
			for (int i = 0; i < count; i++) {
				eval(nodeToRepeat);
			}
			break;

		case nProc:
			procedures.put(n.getValue(), n);
			break;

		/**
		* Lorsque procedure ident est lancée, on stock dans un hashmap le nom (ident)
		* de la procedure et les noeuds correspondant, puis lorsque call ident est appelé
		* , on récupere les noeuds correspondant a l'ident dans la hashmap procedures 
		*/
		case nCall:
			if(procedures.containsKey(n.getValue())) {

				Iterator<Node> its = procedures.get(n.getValue()).getChildren();

				Node nodeToRepeats = its.next();
				eval(nodeToRepeats);
			}else {
				System.out.println("la procédure n'existe pas");
			}
			break;

		/**
		* On a un tableau de couleurs de 13 couleurs, on verifie donc que le numéro de la couleur saisie
		* fait partie des couleurs disponible, sinon on affiche une erreur
		*/
		case nColor:
			int col = Integer.valueOf(n.getValue());
			if (col >= 0 && col < 14) {
				color = colors[col];
				gc.setColor(color);
			}else {
				System.out.println("Cette couleur n'existe pas ! ");
			}
			break;
		}
	}


	/**
	 * Avance la turtle vers l'avant l'unité de longueur 'length'
	 * @param length 
	 */
	void forward(double length) {
		double destX = x + Math.sin(direction*Math.PI*2/360) * length;
		double destY = y + Math.cos(direction*Math.PI*2/360) * length;
		gc.drawLine((int)x, (int)y, (int)destX, (int)destY);
		x = destX;
		y = destY;
	}

	/**
	 * Cette méthode permet d'afficher l'AST
	 * @param n
	 * @param profondeur
	 */
	static void printAst(Node n, int profondeur) {
		StringBuilder s = new StringBuilder();
		for(int i=0;i<profondeur;i++) {
			s.append("  ");
		}
		s.append(n.toString());
		System.out.println(s);
		Iterator<Node> children = n.getChildren();
		while(children.hasNext()) {
			printAst(children.next(), profondeur +1);
		}
	}

}
