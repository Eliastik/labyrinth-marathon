package model;

import model.util.Direction;

/**
 * A cell
 * @author Eliastik
 * @version 1.1
 * @since 30/11/2019
 */
public class Cell {
	/* The case value */
	private CellValue value;
	/* The 4 walls around the case */
	private CellValue northEdge;
	private CellValue eastEdge;
	private CellValue southEdge;
	private CellValue westEdge;
	
	/**
	 * Construct a new cell
	 * @param valeur ({@link CellValue}) The value of the cell
	 * @param north ({@link CellValue}) The value of the north edge
	 * @param east ({@link CellValue}) The value of the east edge
	 * @param west ({@link CellValue}) The value of the west edge
	 * @param south ({@link CellValue}) The value of the south edge
	 */
	public Cell(CellValue valeur, CellValue north, CellValue east, CellValue west, CellValue south)  {
		this.value = valeur;
		this.northEdge = north;
		this.eastEdge = east;
		this.southEdge = south;
		this.westEdge = west;
	}
	
	/**
	 * Construct a new cell surrounded by walls
	 */
	public Cell() {
		this(CellValue.WALL, CellValue.WALL, CellValue.WALL, CellValue.WALL, CellValue.WALL);
	}

	/**
	 * Return the value of this cell
	 * @return ({@link CellValue}) The value
	 */
	public CellValue getValue() {
		return value;
	}

	/**
	 * Set the value of this cell
	 * @param value ({@link CellValue}) The new value
	 */
	public void setValue(CellValue value) {
		this.value = value;
	}

	/**
	 * Return the value of the north edge of this cell
	 * @return ({@link CellValue}) The value
	 */
	public CellValue getNorth() {
		return northEdge;
	}

	/**
	 * Set the value of the north edge of this cell
	 * @param north ({@link CellValue}) The new value
	 */
	public void setNorth(CellValue north) {
		this.northEdge = north;
	}
	
	/**
	 * Return the value of the east edge of this cell
	 * @return ({@link CellValue}) The value
	 */
	public CellValue getEast() {
		return eastEdge;
	}

	/**
	 * Set the value of the east edge of this cell
	 * @param east ({@link CellValue}) The new value
	 */
	public void setEast(CellValue east) {
		this.eastEdge = east;
	}

	/**
	 * Return the value of the south edge of this cell
	 * @return ({@link CellValue}) The value
	 */
	public CellValue getSouth() {
		return southEdge;
	}

	/**
	 * Set the value of the south edge of this cell
	 * @param south ({@link CellValue}) The new value
	 */
	public void setSouth(CellValue south) {
		this.southEdge = south;
	}

	/**
	 * Return the value of the west edge of this cell
	 * @return ({@link CellValue}) The value
	 */
	public CellValue getWest() {
		return westEdge;
	}

	/**
	 * Set the value of the west edge of this cell
	 * @param west ({@link CellValue}) The new value
	 */
	public void setWest(CellValue west) {
		this.westEdge = west;
	}
	
	/**
	 * Get the value of the edge in the opposite direction to that passed in parameter
	 * @param direction ({@link Direction}) The direction
	 * @return ({@link CellValue}) The value of the opposite edge
	 */
	public CellValue getOppositeEdge(Direction direction) {
		switch(direction) {
			case NORTH:
				return this.getSouth();
			case SOUTH:
				return this.getNorth();
			case EAST:
				return this.getWest();
			case WEST:
				return this.getEast();
		}
		
		return null;
	}
	
	/**
	 * Set the value of the edge in the opposite direction to that passed in parameter
	 * @param direction ({@link Direction}) The direction
	 * @param value ({@link CellValue}) The new value
	 */
	public void setOppositeEdge(Direction direction, CellValue value) {
		switch(direction) {
			case NORTH:
				this.setSouth(value);
				break;
			case SOUTH:
				this.setNorth(value);
				break;
			case EAST:
				this.setWest(value);
				break;
			case WEST:
				this.setEast(value);
				break;
		}
	}
	
	/**
	 * Get the value of the cell in the direction passed in parameter
	 * @param direction ({@link Direction}) The direction
	 * @return ({@link CellValue}) The value of the edge
	 */
	public CellValue getEdgeToDirection(Direction direction) {
		switch(direction) {
			case NORTH:
				return this.getNorth();
			case SOUTH:
				return this.getSouth();
			case EAST:
				return this.getEast();
			case WEST:
				return this.getWest();
		}
		
		return null;
	}
	
	/**
	 * Set the value of the cell in the direction passed in parameter
	 * @param direction ({@link Direction}) The direction
	 * @param value ({@link CellValue}) The value
	 */
	public void setEdgeToDirection(Direction direction, CellValue value) {
		switch(direction) {
			case NORTH:
				this.setNorth(value);
				break;
			case SOUTH:
				this.setSouth(value);
				break;
			case EAST:
				this.setEast(value);
				break;
			case WEST:
				this.setWest(value);
				break;
		}
	}
	
	public String toString() {
		return "[Case] value = " + this.getValue() + " ; north = " + this.getNorth() + " ; south = " + this.getSouth() + " ; east = " + this.getEast() + " ; west = " + this.getWest();
	}
}