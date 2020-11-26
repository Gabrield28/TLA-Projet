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
					Nodes.add(new Node(NodeClass.nRight, "["));
				} else if (e == 102) {
					System.out.println("Accepte ]");
					Nodes.add(new Node(NodeClass.nLeft, "]"));
					sr.goBack();
				} else if (e == 103) {
					System.out.println("Accepte la définition de la procédure ident  " + buf);
					
					if(buf.contains("repeat") == true) Nodes.add(new Node(NodeClass.nRepeat, buf));
					else if(buf.contains("forward") == true) Nodes.add(new Node(NodeClass.nForward, buf));
					else if(buf.contains("right") == true) Nodes.add(new Node(NodeClass.nRight, buf));
					else if(buf.contains("left") == true) Nodes.add(new Node(NodeClass.nLeft, buf));
					
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
	
	/**
	 * 
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
        
        // récupère un exemple 'en dur' d'arbre syntaxique abstrait
        // A FAIRE : remplacer par l'implémentation d'une analyse syntaxique descendante
        
        Node root = exampleAst();

        System.out.println("Arbre syntaxique abstrait :");
        printAst(root, 0);
        evalRoot(root);
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
     * 
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
     * 
     * @return
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
}
