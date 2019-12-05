package model;

import model.util.Direction;

/**
 * A cell
 * @author Eliastik
 * @version 1.0
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
	
	public Cell(CellValue valeur, CellValue north, CellValue east, CellValue west, CellValue south)  {
		this.value = valeur;
		this.northEdge = north;
		this.eastEdge = east;
		this.southEdge = south;
		this.westEdge = west;
	}
	
	public Cell() {
		this(CellValue.WALL, CellValue.WALL, CellValue.WALL, CellValue.WALL, CellValue.WALL);
	}

	public CellValue getValue() {
		return value;
	}

	public void setValue(CellValue value) {
		this.value = value;
	}

	public CellValue getNorth() {
		return northEdge;
	}

	public void setNorth(CellValue north) {
		this.northEdge = north;
	}

	public CellValue getEast() {
		return eastEdge;
	}

	public void setEast(CellValue east) {
		this.eastEdge = east;
	}

	public CellValue getSouth() {
		return southEdge;
	}

	public void setSouth(CellValue south) {
		this.southEdge = south;
	}

	public CellValue getWest() {
		return westEdge;
	}

	public void setWest(CellValue west) {
		this.westEdge = west;
	}
	
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