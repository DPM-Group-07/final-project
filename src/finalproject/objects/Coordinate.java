package finalproject.objects;

/**
 * This is an object holding two coordinate values, X and Y. No other functionality
 * is implemented in this class. It has been created to help refactor the code and
 * make it more modular and easier to understand.
 * @author maxsn
 *
 */
public class Coordinate {
	private double x;
	private double y;
	
	/**
	 * Create a new coordinate with and x-value and a y-value.
	 * @param x The x-value of the coordinate.
	 * @param y The y-value of the coordinate.
	 */
	public Coordinate(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Get the x-value of this coordinate.
	 * @return The x-value of the coordinate.
	 */
	public double getX() {
		return x;
	}
	
	/**
	 * Get the y-value of this coordinate.
	 * @return The y-value of this coordinate.
	 */
	public double getY() {
		return y;
	}
}
