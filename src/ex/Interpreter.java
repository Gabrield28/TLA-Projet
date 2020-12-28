package ex;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.*;

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
	 * Couleurs
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
	 * Constructeur interpreter
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
		Turtle t = new Turtle(direction, x, y);
		
		System.out.println("--------------------- Analyse Lexicale ---------------------");
		SourceReader sr = new SourceReader(s);
		tokens = new Lexer().lexer(sr);
		//lancer parser

		try {
			System.out.println("--------------------- Analyse Syntaxique ---------------------");
			
			//new AnalyseSyntaxique().parser(tokens);
			Node root = AnalyseSyntaxique.parser(tokens);
			
			System.out.println("--------------------- Arbre Syntaxique Abstrait ---------------------");
			printAst(root, 0);
			evalRoot(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		try {
			System.out.println("--------------------- Arbre syntaxique abstrait ---------------------");
			Node root = exampleAst();
			
	        System.out.println("Arbre syntaxique abstrait :");
	        printAst(root, 0);
	        evalRoot(root);
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		// récupère un exemple 'en dur' d'arbre syntaxique abstrait
		// A FAIRE : remplacer par l'implémentation d'une analyse syntaxique descendante

		//Node root = exampleAst();
	}


	/**
	 * 
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
	 * 
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
			//right(Integer.valueOf(n.getValue()));
			direction = (direction - Integer.valueOf(n.getValue())) % 360;			
			break;

		case nRepeat:
			int count = Integer.valueOf(n.getValue());
			Node nodeToRepeat = it.next();
			for (int i = 0; i < count; i++) {
				eval(nodeToRepeat);
			}
			break;

		case nCall:

			if(procedures.containsValue(n.getValue())) {
				//eval(procedures.values());
			}else {
				System.out.println("la procédure n'existe pas");
			}
			break;

		case nColor:
			int col = Integer.valueOf(n.getValue());
			if (col > 0 && col < 12) {
				color = colors[col];
			}else {
				System.out.println("Cette couleur n'existe pas ! ");
			}
			break;
			// A FAIRE : implémenter l'interprétation des noeuds nCall et nColor
		}
	}

	/**
	 * Bouge la tortue vers l'avant par  'steps'
	 * @param length le nombre de steps
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
			printAst(children.next(), profondeur + 1);
		}
	}

	/**
	 * Exemple d'un AST
	 * @return Node
	 */
	static Node exampleAst() {
		Node root = new Node(NodeClass.nBlock);

		root.appendNode(new Node(NodeClass.nRight, "180"));

		Node n1 = new Node(NodeClass.nBlock);
		n1.appendNode(new Node(NodeClass.nForward, "5"));
		n1.appendNode(new Node(NodeClass.nRight, "90"));
		Node n11 = new Node(NodeClass.nRepeat, "2");
		n11.appendNode(n1);
		root.appendNode(n11);

		root.appendNode(new Node(NodeClass.nForward, "10"));

		root.appendNode(new Node(NodeClass.nRight, "90"));

		Node n2 = new Node(NodeClass.nBlock);
		n2.appendNode(new Node(NodeClass.nLeft, "90"));
		n2.appendNode(new Node(NodeClass.nForward, "20"));
		Node n21 = new Node(NodeClass.nRepeat, "3");
		n21.appendNode(n2);
		root.appendNode(n21);

		return root;

	}
}
