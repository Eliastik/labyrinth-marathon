package model.util;


import java.util.Random;

/**
 * Represents a position on the labyrinth
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public class Position {
	int x;
	int y;
	
	/**
	 * Construct a new Position
	 * @param x (int) x position (abscissa)
	 * @param y (int) y position (ordinate)
	 */
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Return the X position
	 * @return (int) the abscissa
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Set the X position
	 * @param x (int) the abscissa
	 */
	public void setX(int x) {
		this.x = x;
	}
	
	/**
	 * Return the Y position
	 * @return (int) the ordinate
	 */
	public int getY() {
		return y;
	}
	
	/**
	 * Set the Y position
	 * @param y (int) the ordinate
	 */
	public void setY(int y) {
		this.y = y;
	}
	
	/**
	 * Return a random position given width and height of a labyrinth
	 * @param width (int)
	 * @param height (int)
	 * @param random ({@link Random})
	 * @return A random {@link Position}
	 */
	public static Position random(int width, int height, Random random) {
		return new Position(random.nextInt(width), random.nextInt(height));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	public String toString() {
		return "[Position] x = " + this.getX() + " ; y = " + this.getY();
	}
}