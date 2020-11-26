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

    Graphics gc;

    HashMap<String, Node> procedures;

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
	static Integer transitions[][] = {
			//             espace lettre chiffre   [    ]    avance    Gauche    Droite    Repeat   	    autre
			/*  0 */    {      0,     1,      2,  101, 102,    103,      104,      105,      106,      107,     null      },
			/*  1 */    {    101,     1,      1, 201, 201, 201, 201, 201, 201, 201,     null      },
			/*  2 */    {    202,   202,      2, 202, 202, 201, 201, 201, 201, 201,     null      },
			/*  3 */    {    203,   203,    203, 204, 203, 201, 201, 201, 201, 201,     null      },
			/*  4 */    {    205,   205,    205, 206, 205, 201, 201, 201, 201, 201,     null      },
			/*  5 */    {    207,   207,    207, 208, 207, 201, 201, 201, 201, 201,     null      },
			/*  6 */    {    209,   209,    209, 210, 209, 201, 201, 201, 201, 201,     null      },

			// 101 accepte                        														   (goBack : non)
			// 102 accepte Avance la tortue de n pixels   													   (goBack : oui)
			// 103 accepte Tourne la tortue de n degres vers la gauche                                         (goBack : oui)
			// 104 accepte Tourne la tortue de n degres vers la droite                                         (goBack : oui)
			// 105 accepte Change la couleur du tracé, n étant une val numérique de 0 à 12, 
			//             à faire correspondre aux couleurs prédéfinies dans la classe java.awt.Color         (goBack : non)
			// 106 accepte Défini une rocédure nommé ident, dont les instructions se trouvent entre crochets   (goBack : oui)
			// 107 accepte call ident Si une procédure nommée ideent a été au préalable
			// 			   défini, appelle cette procédure (càd exécute les instructions de cette procedure).  (goBack : non)


	};

	static final int ETAT_INITIAL = 0;

	private int indiceSymbole(Character c) {
		if (c == null) return 0;
		if (Character.isWhitespace(c)) return 0;
		if (Character.isLetter(c)) return 1;
		if (Character.isDigit(c)) return 2;
		if (c == '[') return 3;
		if (c == ']') return 4;
		return 5;
	}

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
					System.out.println("Accepte forward");
					Nodes.add(new Node(NodeClass.nForward));
				} else if (e == 102) {
					System.out.println("Accepte la définition de la procédure ident  " + buf);
					Nodes.add(new Node(NodeClass.nProc, buf));
					sr.goBack();
				} else if (e == 103) {
					System.out.println("Accepte la procédure appelé " + buf);
					Nodes.add(new Node(NodeClass.nCall, buf));
					sr.goBack();
				} else if (e == 104) {
					System.out.println("Accepte left");
					Nodes.add(new Node(NodeClass.nLeft));
					sr.goBack();
				} else if (e == 105) {
					System.out.println("Accepte right");
					Nodes.add(new Node(NodeClass.nRight));
				} else if (e == 106) {
					System.out.println("Accepte repeat");
					Nodes.add(new Node(NodeClass.nRepeat));
					sr.goBack();
				} else if (e == 107) {
					System.out.println("Accepte digit");
					Nodes.add(new Node(NodeClass.nColor));
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
	
    void interpreter(String s, double x, double y, Graphics gc) throws Exception {
        System.out.println();
        this.gc = gc;
        this.x = x;
        this.y = y;
        direction = initDirection;
        procedures = new HashMap();
        
        // récupère un exemple 'en dur' d'arbre syntaxique abstrait
        // A FAIRE : remplacer par l'implémentation d'une analyse syntaxique descendante
        
        Node root = exampleAst();

        System.out.println("Arbre syntaxique abstrait :");
        printAst(root, 0);
        evalRoot(root);
    }

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

    void eval(Node n) {
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
            // A FAIRE : implémenter l'interprétation des noeuds nCall et nColor
        }
    }

    void forward(double length) {
        double destX = x + Math.sin(direction*Math.PI*2/360) * length;
        double destY = y + Math.cos(direction*Math.PI*2/360) * length;
        gc.drawLine((int)x, (int)y, (int)destX, (int)destY);
        x = destX;
        y = destY;
    }

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
}
