package analyseSyntaxique;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import analyseLexicale.Token;


public class Node {
    private List<Node> children = new ArrayList<Node>();
    private NodeClass cl;
    private Token t;
    private String value;
    
    /**
     * Constructeur 1
     * @param cl
     */
    Node(NodeClass cl) {
        this.cl = cl;
    }
    
    /**
     * Constructeur 2
     * @param cl
     * @param value
     */
    Node(NodeClass cl, String value) {
        this.cl = cl;
        this.value = value;
    }
    
    /**
     * Constructeur 1
     * @param cl
     */
    Node(Token t) {
        this.t = t;
    }
    
    /**
     * retirer noeud
     * @param n
     */
    void prependNode(Node n) {
        children.add(0, n);
    }

    /**
     * ajout noeud
     * @param n
     */
    void appendNode(Node n) {
        children.add(n);
    }

    /**
     * merge with node
     * @param other
     */
    void mergeWithNode(Node other) {
        Iterator<Node> it = other.getChildren();
        while(it.hasNext()) {
            this.children.add(it.next());
        }
    }
    
    /**
     * 
     * @return
     */
    public Iterator<Node> getChildren() {
        return this.children.iterator();
    }

    /**
     * @return boolean
     */
    boolean hasChildren() {
        return !children.isEmpty();
    }

    /**
     * 
     * @return C1
     */
    public NodeClass getCl() {
        return cl;
    }

    /**
     * @return String
     */
    public String getValue() {
        return value;
    }
    
    /**
     * affichage
     */
    public String toString() {
        String s = "<";
        if (cl != null) s = s + cl.toString();
        if (value != null) s = s + ", " + value;
        return s + ">";
    }

	public Token getT() {
		return t;
	}
}
