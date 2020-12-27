package ex;


public class Turtle{
	
	private double direction;
	private double x;
	private double y;

	/**
	 * Constructeur
	 * @param direction
	 * @param x la position de la tortue sur l'axe horizontale
	 * @param y la position de la tortue sur l'axe verticale
	 */
	public Turtle(double direction, double x, double y) {
		super();
		this.direction = direction;
		this.x = x;
		this.y = y;
	}

	/**
	 * Change la position de la tortue en 'degree' vers la gauche
	 * @param degree l'angle
	 */
	public void left(int degree) {
		this.direction += degree;
	}
	
	/**
	 * Change la position de la tortue en 'degree' vers la droite
	 * @param degree l'angle
	 */
	public void right(int degree) {
		this.direction -= degree;
	}
	
	/**
	 * @return double
	 */
	public double getDirection() {
		return direction;
	}

	/**
	 * @param direction
	 */
	public void setDirection(double direction) {
		this.direction = direction;
	}

	/**
	 * @return double
	 */
	public double getX() {
		return x;
	}
	/**
	 * 
	 * @return double
	 */
	public double getY() {
		return y;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public String toString() {
		return "Tortue [direction=" + direction + ", x=" + x + ", y=" + y + "]";
	}
}
