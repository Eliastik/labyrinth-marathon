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
	 * Construct a new Positino
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
	 * @param random (Random)
	 * @return A random Position
	 */
	public static Position random(int width, int height, Random random) {
		return new Position(random.nextInt(width), random.nextInt(height));
	}
	
	public boolean equals(Object other) {
		return this.getX() == ((Position) other).getX() && this.getY() == ((Position) other).getY();
	}
	
	public String toString() {
		return "[Position] x = " + this.getX() + " ; y = " + this.getY();
	}
}