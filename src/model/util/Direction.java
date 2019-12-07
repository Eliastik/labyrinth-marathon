package model.util;

/**
 * Represents a direction
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public enum Direction {
	/**
	 * The north direction
	 */
	NORTH,
	/**
	 * The south direction
	 */
	SOUTH,
	/**
	 * The east direction
	 */
	EAST,
	/**
	 * The west direction
	 */
	WEST;
	
	/**
	 * Return the opposite direction to that passed in parameter
	 * @param dir ({@link model.util.Direction})
	 * @return ({@link model.util.Direction}) The opposite direction
	 */
	public static Direction getOpposite(Direction dir) {
		switch(dir) {
			case NORTH:
				return SOUTH;
			case SOUTH:
				return NORTH;
			case EAST:
				return WEST;
			case WEST:
				return EAST;
		}
		
		return null;
	}
}