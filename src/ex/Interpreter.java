package ex;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

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
		System.out.println("---------------------");
		SourceReader sr = new SourceReader(s);
		tokens = new Lexer().lexer(sr);
		//lancer parser

		try {
			new AnalyseSyntaxique().parser(tokens);
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println();
		this.gc = gc;
		this.x = x;
		this.y = y;
		direction = initDirection;
		procedures = new HashMap();

		Turtle t = new Turtle(direction, x, y);



		// récupère un exemple 'en dur' d'arbre syntaxique abstrait
		// A FAIRE : remplacer par l'implémentation d'une analyse syntaxique descendante

		//Node root = exampleAst();


		
		int loop = 0;


		if (findRepeat()) {
			getRepeat();

		}

		while (loop != tokens.size()) {

			eval(null);

			loop++;

		}
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
	 * 
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
	 * @param depth
	 */
	static void printAst(Node n, int depth) {
		StringBuilder s = new StringBuilder();
		for(int i=0;i<depth;i++) s.append("  ");
		s.append(n.toString());
		System.out.println(s);
		Iterator<Node> children = n.getChildren();
		while(children.hasNext()) {
			printAst(children.next(), depth + 1);
		}
	}

	/**
	 * Exemple
	 * @return Node
	 */
	static Node exampleAst() {
		Node root = new Node(NodeClass.nBlock);

		root.appendNode(new Node(NodeClass.nRight, "90"));

		Node n1 = new Node(NodeClass.nBlock);
		n1.appendNode(new Node(NodeClass.nForward, "40"));
		n1.appendNode(new Node(NodeClass.nRight, "10"));
		Node n11 = new Node(NodeClass.nRepeat, "3");
		n11.appendNode(n1);
		root.appendNode(n11);

		root.appendNode(new Node(NodeClass.nForward, "50"));

		// root.appendNode(new Node(NodeClass.nRight, "90"));

		Node n2 = new Node(NodeClass.nBlock);
		n2.appendNode(new Node(NodeClass.nLeft, "90"));
		n2.appendNode(new Node(NodeClass.nForward, "20"));
		Node n21 = new Node(NodeClass.nRepeat, "3");
		n21.appendNode(n2);
		root.appendNode(n21);

		return root;
	}

	/**
	 * Permet de retouner true si le token repeat existe dans la liste
	 * 
	 * @return boolean
	 */
	private boolean findRepeat() {
		for (Token t : tokens) {
			if (t.getCl() == TokenClass.repeat)
				return true;
		}
		return false;
	}

	/**
	 * permet de trier la liste pour traiter les repeat, en outre on recupère
	 * un repeat on sauvegarde sa valeur et on enregistre autant de fois ses valeurs
	 * entre les crochets
	 * 
	 */
	private void getRepeat() {
		Token token_tampon;
		int position_debut = 0;
		int position_fin = tokens.size();
		int valeur_repeat = 0;
		int compteur_leftHook = 0;

		/**
		 * la premiere contient les tokens avant le repeat, 
		 * la deuxieme  contient les tokens entre les crochets du repeat autant de 
		 * fois que la valeur du repeat et,
		 *  la troisieme contient les tokens 
		 * apres le crochet fermant du repeat puis je rassemble la liste
            a la fin on a une liste sans repeat sans chrochets
		 */

		ArrayList<Token> liste1 = new ArrayList();
		ArrayList<Token> liste2 = new ArrayList();
		ArrayList<Token> liste3 = new ArrayList();

		token_tampon = tokens.get(position_debut);

		while (token_tampon.getCl() != TokenClass.leftHook) {
			if (token_tampon.getCl() == TokenClass.repeat) {
				position_debut++;
			}
			position_debut++;
			token_tampon = tokens.get(position_debut);
		}
		position_debut++;

		position_fin = position_debut;

		token_tampon = tokens.get(position_fin);

		while (token_tampon.getCl() != TokenClass.rightHook || compteur_leftHook != 0) {
			if (token_tampon.getCl() == TokenClass.leftHook) {
				compteur_leftHook++;
			}
			if (token_tampon.getCl() == TokenClass.rightHook) {
				compteur_leftHook--;
			}
			position_fin++;
			token_tampon = tokens.get(position_fin);
		}
		position_fin++;

		addToken(position_debut, position_fin, liste1, liste2, liste3);

		fusion(liste1, liste2, liste3);

		if (findRepeat())
			getRepeat();
		;
	}

	/**
	 * Permet d'ajouter les tokens dans les listes au moment du trie
	 * 
	 * @param position_debut
	 * @param position_fin
	 * @param liste1
	 * @param liste2
	 * @param liste3
	 */
	private void addToken(int position_debut, int position_fin, ArrayList<Token> liste1, ArrayList<Token> liste2,
			ArrayList<Token> liste3) {
		int valeur_repeat;
		for (int i = 0; i < (position_debut - 3); i++) {
			liste1.add(tokens.get(i));
		}

		for (int i = (position_fin); i < tokens.size(); i++) {
			liste3.add(tokens.get(i));
		}

		valeur_repeat = Integer.parseInt(tokens.get(position_debut - 2).getValue());
		for (int i = 1; i <= valeur_repeat; i++) {
			for (int j = (position_debut); j < (position_fin - 1); j++) {
				liste2.add(tokens.get(j));
			}
		}
	}

	/**
	 * Permet de fusionner les trois listes triers pour mettre à jour la liste
	 * principale des tokens
	 * 
	 * @param liste1
	 * @param liste2
	 * @param liste3
	 */
	private void fusion(ArrayList<Token> liste1, ArrayList<Token> liste2, ArrayList<Token> liste3) {
		tokens = new ArrayList();
		tokens.addAll(liste1);
		tokens.addAll(liste2);
		tokens.addAll(liste3);
	}
}
