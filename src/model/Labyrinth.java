package model;


import java.util.Random;

import algorithms.AldousBroder;
import util.Direction;
import util.Position;

/**
 * A labyrinth
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public class Labyrinth {
	private Cell[][] cells;
	private Position startPosition;
	private Position endPosition;
	private GenerationAlgorithm algorithm = new AldousBroder();
	private Player player;
	private boolean autoPlayer;
	private boolean enableAutoPlayer;
	
	public Labyrinth(int width, int height, Position startPosition, Position endPosition, GenerationAlgorithm algorithm, boolean autoPlayer, boolean enableAutoPlayer) {
		if((width <= 1 && height <= 1) || width <= 0 || height <= 0) {
			throw new IllegalArgumentException("Impossible to build a labyrinth with only one cell or less");
		}
		
		cells = new Cell[height][width];
		this.startPosition = startPosition;
		this.endPosition = endPosition;
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				cells[i][j] = new Cell();
			}
		}
		
		this.player = new Player(this.startPosition, Direction.SOUTH, this);
		this.autoPlayer = autoPlayer;
		this.enableAutoPlayer = enableAutoPlayer;
		this.algorithm = algorithm;
	}
	
	public Labyrinth(int width, int height, Position startPosition, GenerationAlgorithm algorithm, Position endPosition, boolean enableAutoPlayer) {
		this(width, height, startPosition, endPosition, algorithm, false, enableAutoPlayer);
	}
	
	public Labyrinth(int width, int height, Position startPosition, Position endPosition, boolean enableAutoPlayer) {
		this(width, height, startPosition, endPosition, new AldousBroder(), false, enableAutoPlayer);
	}
	
	public Labyrinth(int width, int height, Position startPosition, Position endPosition) {
		this(width, height, startPosition, endPosition, true);
	}
	
	public Labyrinth(int width, int height) {
		this(width, height, new Position(0, 0), new Position(width - 1, height - 1));
	}
	
	public Labyrinth() {
		this(5, 5);
	}
	
	public Cell getCase(Position position) {
		return cells[position.getY()][position.getX()];
	}

	/**
	 * Generate randoms start and end positions<br />
	 * Reset the player position
	 * @param random (Random) The pseudo-random number generator
	 */
	public void generateStartEndPositions(Random random) {
		this.startPosition = Position.random(this.getWidth(), this.getHeight(), random);
		
		do {
			endPosition = Position.random(this.getWidth(), this.getHeight(), random);
		} while(endPosition.equals(startPosition));
	}
	
	/**
	 * Generate randoms start and end positions<br />
	 * Reset the player position
	 * @param seed (long) The value for the pseudo-random number generator
	 */
	public void generateStartEndPositions(long seed) {
		this.generateStartEndPositions(new Random(seed));
	}
	
	/**
	 * 
	 * @return (int) Return the height of the grid
	 */
	public int getHeight() {
		return this.cells.length;
	}

	/**
	 * 
	 * @return (int) Return the width of the grid
	 */
	public int getWidth() {
		return this.cells[0].length;
	}
	
	public Position getStartPosition() {
		return this.startPosition;
	}

	public void setStartPosition(Position startPosition) {
		this.startPosition = startPosition;
		this.player.setPosition(this.startPosition);
		this.player.setDirection(Direction.SOUTH);
	}

	public Position getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(Position endPosition) {
		this.endPosition = endPosition;
		this.player.setPosition(this.startPosition);
		this.player.setDirection(Direction.SOUTH);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public boolean isAutoPlayer() {
		return autoPlayer;
	}

	public void setAutoPlayer(boolean autoPlayer) {
		this.autoPlayer = autoPlayer;
	}

	public boolean isAutoPlayerEnabled() {
		return enableAutoPlayer;
	}

	public void setAutoPlayerEnabled(boolean enableJoueurAuto) {
		this.enableAutoPlayer = enableJoueurAuto;
	}

	/**
	 * Return the position of a neighbour cell given current cell position and two directions
	 * @param cellPosition (Position) The position of the cell from where to search
	 * @param directionX (Direction) The X direction
	 * @param directionY (Direction) The Y direction
	 * @return (Position)
	 */
	public Position getNeighbour(Position cellPosition, Direction directionX, Direction directionY) {
		int x = cellPosition.getX();
		int y = cellPosition.getY();
		
		if(directionX != null && x >= 0 && x < this.getWidth()) {
			switch(directionX) {
				case EAST:
					if(x + 1 >= 0 && x + 1 < this.getWidth()) x++;
					break;
				case WEST:
					if(x - 1 >= 0 && x - 1 < this.getWidth()) x--;
					break;
				default:
					break;
			}
		}
		
		if(directionY != null && y >= 0 && y < this.getHeight()) {
			switch(directionY) {
				case NORTH:
					if(y - 1 >= 0 && y - 1 < this.getHeight()) y--;
					break;
				case SOUTH:
					if(y + 1 >= 0 && y + 1 < this.getHeight()) y++;
					break;
				default:
					break;
			}
		}
		
		return new Position(x, y);
	}
	
	public boolean canMoveTo(Cell currentCell, Cell neighbourCell, Direction direction) {
		boolean canMove = true;
		
		switch(direction) {
			case NORTH:
				canMove = currentCell.getNorth() == CellValue.EMPTY && neighbourCell.getSouth() == CellValue.EMPTY && neighbourCell.getValue() == CellValue.EMPTY;
				break;
			case SOUTH:
				canMove = currentCell.getSouth() == CellValue.EMPTY && neighbourCell.getNorth() == CellValue.EMPTY && neighbourCell.getValue() == CellValue.EMPTY;
				break;
			case EAST:
				canMove = currentCell.getEast() == CellValue.EMPTY && neighbourCell.getWest() == CellValue.EMPTY && neighbourCell.getValue() == CellValue.EMPTY;
				break;
			case WEST:
				canMove = currentCell.getWest() == CellValue.EMPTY && neighbourCell.getEast() == CellValue.EMPTY && neighbourCell.getValue() == CellValue.EMPTY;
				break;
		}
		
		return canMove;
	}
	
	/**
	 * Generate the labyrinth
	 * @param seed (long) Value for the pseudo-random number generator (two same seeds => same labyrinth generated (if start and end positions are identical, same grid size, same algorithm))
	 */
	public void generate(long seed) {
		Random random = new Random(seed);
		this.getCase(this.startPosition).setValue(CellValue.EMPTY);
		algorithm.generate(this, random, this.startPosition, this.endPosition);
	}

	public String toString() {
		String res = "+";
		for(int i = 0; i < this.getWidth(); i++) res += "--+";
		
		for(int i = 0; i < this.getHeight(); i++) {
			res += "\n|";
			
			for(int j = 0; j < this.getWidth(); j++) {
				Position pos = new Position(j, i);
				Cell c = this.getCase(pos);
				Position posEst = this.getNeighbour(pos, Direction.EAST, Direction.EAST);
				Cell cEst = this.getCase(posEst);
				
				if(pos.equals(this.getPlayer().getPosition())) {
					res += "00";
				} else {
					if(c.getValue() == CellValue.WALL) res += "##";
					if(c.getValue() == CellValue.CROSSED) res += "~~";
					if(c.getValue() == CellValue.EMPTY) res += "  ";
				}
				
				if(posEst.equals(pos) || (c.getEast() == CellValue.WALL && cEst.getWest() == CellValue.WALL)) {
					res += "|";
				} else {
					res += " ";
				}
			}
			
			res += "\n";
			
			for(int j = 0; j < this.getWidth(); j++) {
				Position pos = new Position(j, i);
				Cell c = this.getCase(pos);
				Position posSud = this.getNeighbour(pos, Direction.SOUTH, Direction.SOUTH);
				Cell cSud = this.getCase(posSud);
				
				res += "+";
				
				if(posSud.equals(pos) || (c.getSouth() == CellValue.WALL && cSud.getNorth() == CellValue.WALL)) {
					res += "--";
				} else {
					res += "  ";
				}
			}
			
			res += "+";
		}
		
		return res;
	}
}