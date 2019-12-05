package model;


import java.util.Random;

import model.algorithms.AldousBroder;
import model.util.Direction;
import model.util.Position;

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
	private GenerationAlgorithmStrategy algorithm = new AldousBroder();
	private Player player;
	private boolean autoPlayer;
	private boolean enableAutoPlayer;
	private boolean generationFinished = false;
	
	public Labyrinth(int width, int height, Position startPosition, Position endPosition, GenerationAlgorithmStrategy algorithm, boolean autoPlayer, boolean enableAutoPlayer) {
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
	
	public Labyrinth(int width, int height, Position startPosition, Position endPosition, GenerationAlgorithmStrategy algorithm, boolean enableAutoPlayer) {
		this(width, height, startPosition, endPosition, algorithm, false, enableAutoPlayer);
	}
	
	public Labyrinth(int width, int height, GenerationAlgorithmStrategy algorithm, boolean enableAutoPlayer) {
		this(width, height, new Position(0, 0), new Position(width - 1, height - 1), algorithm, enableAutoPlayer);
	}
	
	public Labyrinth(int width, int height, Position startPosition, Position endPosition, boolean enableAutoPlayer) {
		this(width, height, startPosition, endPosition, new AldousBroder(), false, enableAutoPlayer);
	}
	
	public Labyrinth(int width, int height, boolean enableAutoPlayer) {
		this(width, height, new Position(0, 0), new Position(width - 1, height - 1), enableAutoPlayer);
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
	
	/**
	 * Get the cell at the position passed in parameter
	 * @param position ({@link Position}) The position
	 * @return The cell
	 */
	public Cell getCell(Position position) {
		return cells[position.getY()][position.getX()];
	}

	/**
	 * Generate randoms start and end positions<br />
	 * Reset the player position
	 * @param random ({@link Random}) The pseudo-random number generator
	 */
	public void generateStartEndPositions(Random random) {
		this.startPosition = Position.random(this.getWidth(), this.getHeight(), random);
		
		do {
			endPosition = Position.random(this.getWidth(), this.getHeight(), random);
		} while(endPosition.equals(startPosition));
	}
	
	/**
	 * Generate randoms start and end positions<br />
	 * Reset the player position to the new start position generated
	 * @param seed (long) The value for the pseudo-random number generator
	 */
	public void generateStartEndPositions(long seed) {
		this.generateStartEndPositions(new Random(seed));
	}
	
	/**
	 * Get the height of the grid
	 * @return (int) Return the height of the grid
	 */
	public int getHeight() {
		return this.cells.length;
	}

	/**
	 * Get the width of the grid
	 * @return (int) Return the width of the grid
	 */
	public int getWidth() {
		return this.cells[0].length;
	}
	
	/**
	 * Get the start position
	 * @return ({@link Position}) The start position
	 */
	public Position getStartPosition() {
		return this.startPosition;
	}

	/**
	 * Set the start position<br />
	 * Reset the position of the player to the new start position
	 * @param startPosition ({@link Position}) The new start position
	 */
	public void setStartPosition(Position startPosition) {
		this.startPosition = startPosition;
		this.player.setPosition(this.startPosition);
		this.player.setDirection(Direction.SOUTH);
	}

	/**
	 * Get the end position
	 * @return ({@link Position}) The end position
	 */
	public Position getEndPosition() {
		return endPosition;
	}

	/**
	 * Set the end position<br />
	 * Reset the position of the player to the start position
	 * @param endPosition ({@link Position}) The new end position
	 */
	public void setEndPosition(Position endPosition) {
		this.endPosition = endPosition;
		this.player.setPosition(this.startPosition);
		this.player.setDirection(Direction.SOUTH);
	}

	/**
	 * Get the player
	 * @return ({@link Player}) The player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * Set the player
	 * @param player ({@link Player}) The player
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/**
	 * Get the auto player setting
	 * @return (boolean) true if the player move automatically according to the path to the end position, false otherwise
	 */
	public boolean isAutoPlayer() {
		return autoPlayer;
	}

	/**
	 * Set the auto player setting
	 * @param autoPlayer (boolean) true if the player must move automatically according to the path to the end position, false otherwise
	 */
	public void setAutoPlayer(boolean autoPlayer) {
		this.autoPlayer = autoPlayer;
	}

	/**
	 * Get the auto player enabling of this labyrinth
	 * @return (boolean) false if the auto player is forbidden for this labyrinth, true otherwise
	 */
	public boolean isAutoPlayerEnabled() {
		return enableAutoPlayer;
	}

	/**
	 * Set the auto player enabling of this labyrinth
	 * @param enableJoueurAuto (boolean) false if the auto player must be forbidden for this labyrinth, true otherwise
	 */
	public void setAutoPlayerEnabled(boolean enableJoueurAuto) {
		this.enableAutoPlayer = enableJoueurAuto;
	}

	/**
	 * Return the position of a neighbour cell given current cell position and two directions
	 * @param cellPosition ({@link Position}) The position of the cell from where to search
	 * @param directionX ({@link Direction}) The X direction
	 * @param directionY ({@link Direction}) The Y direction
	 * @return ({@link Position})
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
	
	/**
	 * Inform if the player can move the other cell passed in parameter according to the direction
	 * @param currentCell ({@link Cell}) The current cell
	 * @param neighbourCell ({@link Cell}) The other cell
	 * @param direction ({@link Direction}) The direction
	 * @return (boolean) true if the player can move, false otherwise
	 */
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
	 * @param stepByStep (boolean) The algorithm sleeps between iterations to demonstrate how it works (to be run by an independent Thread)
	 */
	public void generate(long seed, boolean stepByStep) {
		Random random = new Random(seed);
		this.getCell(this.startPosition).setValue(CellValue.EMPTY);
		algorithm.generate(this, random, this.startPosition, this.endPosition, stepByStep);
		this.generationFinished = true;
	}

	/**
	 * Inform if the generation of the labyrinth is finished
	 * @return (boolean) true if the generation is finished, false otherwise
	 */
	public boolean isGenerationFinished() {
		return generationFinished;
	}

	public String toString() {
		String res = "+";
		for(int i = 0; i < this.getWidth(); i++) res += "--+";
		
		for(int i = 0; i < this.getHeight(); i++) {
			res += "\n|";
			
			for(int j = 0; j < this.getWidth(); j++) {
				Position pos = new Position(j, i);
				Cell c = this.getCell(pos);
				Position posEst = this.getNeighbour(pos, Direction.EAST, Direction.EAST);
				Cell cEst = this.getCell(posEst);
				
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
				Cell c = this.getCell(pos);
				Position posSud = this.getNeighbour(pos, Direction.SOUTH, Direction.SOUTH);
				Cell cSud = this.getCell(posSud);
				
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