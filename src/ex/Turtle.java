package ex;

public class Turtle {
	
	private double direction;
	private double x;
	private double y;

	/**
	 * Constructeur
	 * @param direction
	 * @param x
	 * @param y
	 * @param gc
	 */
	public Turtle(double direction, double x, double y) {
		super();
		this.direction = direction;
		this.x = x;
		this.y = y;
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
}
