package model;


/**
 * The possible values of a cell or cell edge
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public enum CellValue {
	/**
	 * Wall value
	 */
	WALL,
	/**
	 * Empty value
	 */
	EMPTY,
	/**
	 * Crossed value (if the player crossed the cell)
	 */
	CROSSED;
}