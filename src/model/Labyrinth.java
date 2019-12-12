package model;


import java.util.Queue;
import java.util.Random;

import model.generationAlgorithm.GrowingTree;
import model.solvingAlgorithm.BreadthFirstSearch;
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
	private GenerationAlgorithmStrategy algorithm = new GrowingTree();
	private SolvingAlgorithmStrategy solver = new BreadthFirstSearch();
	private Player player;
	private boolean enableAutoPlayer;
	private boolean generationFinished = false;
	
	public Labyrinth(int width, int height, Position startPosition, Position endPosition, GenerationAlgorithmStrategy algorithm, SolvingAlgorithmStrategy algorithmSolve, boolean autoPlayer, boolean enableAutoPlayer) {
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
		this.setAutoPlayer(autoPlayer);
		this.enableAutoPlayer = enableAutoPlayer;
		this.algorithm = algorithm;
		this.solver = algorithmSolve;
	}
	
	public Labyrinth(int width, int height, Position startPosition, Position endPosition, GenerationAlgorithmStrategy algorithm, SolvingAlgorithmStrategy algorithmSolve, boolean enableAutoPlayer) {
		this(width, height, startPosition, endPosition, algorithm, algorithmSolve, false, enableAutoPlayer);
	}
	
	public Labyrinth(int width, int height, GenerationAlgorithmStrategy algorithm, SolvingAlgorithmStrategy algorithmSolve, boolean enableAutoPlayer) {
		this(width, height, new Position(0, 0), new Position(width - 1, height - 1), algorithm, algorithmSolve, enableAutoPlayer);
	}
	
	public Labyrinth(int width, int height, Position startPosition, Position endPosition, boolean enableAutoPlayer) {
		this(width, height, startPosition, endPosition, new GrowingTree(), new BreadthFirstSearch(), enableAutoPlayer);
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
		return position == null ? null : cells[position.getY()][position.getX()];
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
	 * {@link model.Player#isAutoPlayer()}
	 */
	public boolean isAutoPlayer() {
		return this.player != null && this.player.isAutoPlayer();
	}

	/**
	 * {@link model.Player#setAutoPlayer(boolean autoPlayer)}
	 * @param autoPlayer (boolean) true if the player must move automatically according to the path to the end position, false otherwise
	 */
	public void setAutoPlayer(boolean autoPlayer) {
		if(this.player != null) this.player.setAutoPlayer(autoPlayer);
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
					if(x + 1 >= 0 && x + 1 < this.getWidth()) return new Position(x + 1, y);
					break;
				case WEST:
					if(x - 1 >= 0 && x - 1 < this.getWidth()) return new Position(x - 1, y);
					break;
				default:
					break;
			}
		}
		
		if(directionY != null && y >= 0 && y < this.getHeight()) {
			switch(directionY) {
				case NORTH:
					if(y - 1 >= 0 && y - 1 < this.getHeight()) return new Position(x, y - 1);
					break;
				case SOUTH:
					if(y + 1 >= 0 && y + 1 < this.getHeight()) return new Position(x, y + 1);
					break;
				default:
					break;
			}
		}
		
		return null;
	}
	
	/**
	 * Return the direction to the neighbour cell given current cell position and neighbour position
	 * @param cellPosition ({@link Position}) The position of the cell from where to search
	 * @param neighbourPosition ({@link Position}) The position of the cell from where to search
	 * @return ({@link Direction})
	 */
	public Direction getNeighbour(Position cellPosition, Position neighbourPosition) {
		int x = cellPosition.getX();
		int y = cellPosition.getY();
		
		int xNeighbour = neighbourPosition.getX();
		int yNeighbour = neighbourPosition.getY();
		
		if(x >= 0 && x < this.getWidth() && xNeighbour >= 0 && xNeighbour < this.getWidth()) {
			if(xNeighbour - x == -1) return Direction.WEST;
			if(xNeighbour - x == 1) return Direction.EAST;
		}
		
		if(y >= 0 && y < this.getHeight() && yNeighbour >= 0 && yNeighbour < this.getHeight()) {
			if(yNeighbour - y == -1) return Direction.NORTH;
			if(yNeighbour - y == 1) return Direction.SOUTH;
		}
		
		return null;
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
				canMove = currentCell != null && neighbourCell != null && currentCell.getNorth() == CellValue.EMPTY && neighbourCell.getSouth() == CellValue.EMPTY && neighbourCell.getValue() == CellValue.EMPTY;
				break;
			case SOUTH:
				canMove = currentCell != null && neighbourCell != null && currentCell.getSouth() == CellValue.EMPTY && neighbourCell.getNorth() == CellValue.EMPTY && neighbourCell.getValue() == CellValue.EMPTY;
				break;
			case EAST:
				canMove = currentCell != null && neighbourCell != null && currentCell.getEast() == CellValue.EMPTY && neighbourCell.getWest() == CellValue.EMPTY && neighbourCell.getValue() == CellValue.EMPTY;
				break;
			case WEST:
				canMove = currentCell != null && neighbourCell != null && currentCell.getWest() == CellValue.EMPTY && neighbourCell.getEast() == CellValue.EMPTY && neighbourCell.getValue() == CellValue.EMPTY;
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
	
	/**
	 * Calculate a path to the exit and return this path.<br />
	 * Return null if no path was found.
	 * @return ({@link Queue}<{@link Position}>) The path
	 */
	public Queue<Position> getPath() {
		return this.solver.getPath(this);
	}
	
	/**
	 * Inform is the computer is still searching a path (when auto player is enabled)
	 * @return (boolean) true if the computer search a path, false otherwise
	 */
	public boolean isSearchingPath() {
		return this.solver.isSearchingPath();
	}

	/**
	 * Get CellValue ({@link CellValue#WALL} or {@link CellValue#EMPTY}) of the edges around the current cell<br />
	 * Used to display the labyrinth
	 * @param pos ({@link Position}) The position of the cell
	 * @return ({@link CellValue}[3]) An array of CellValue which contains three entries :<br />
	 * {@link CellValue}[0] = The west value<br />
	 * {@link CellValue}[1] = The north value<br />
	 * {@link CellValue}[2] = The north-west value
	 */
	public CellValue[] getCellAround(Position pos) {
		CellValue[] res = new CellValue[3];
		
		Cell c = this.getCell(pos);
		
		Position posWest = this.getNeighbour(pos, Direction.WEST, null);
		Cell cWest = this.getCell(posWest);
		CellValue vWest = (cWest != null && c.getWest() == CellValue.WALL && cWest.getEast() == CellValue.WALL) ? CellValue.WALL : CellValue.EMPTY;
		
		res[0] = vWest;
		
		Position posNorth = this.getNeighbour(pos, null, Direction.NORTH);
		Cell cNorth = this.getCell(posNorth);
		CellValue vNorth = (cNorth != null && c.getNorth() == CellValue.WALL && cNorth.getSouth() == CellValue.WALL) ? CellValue.WALL : CellValue.EMPTY;
		
		res[1] = vNorth;
		
		CellValue vNorthWest = (vWest == CellValue.WALL || vNorth == CellValue.WALL || (posNorth != null && this.getCellAround(posNorth)[0] == CellValue.WALL) || (posWest != null && this.getCellAround(posWest)[1] == CellValue.WALL)) ? CellValue.WALL : CellValue.EMPTY;
		
		res[2] = vNorthWest;
		
		return res;
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
				
				if((posEst != null && posEst.equals(pos)) || ((c != null && c.getEast() == CellValue.WALL) || (cEst != null && cEst.getWest() == CellValue.WALL))) {
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
				
				if((posSud != null && posSud.equals(pos)) || ((c != null && c.getSouth() == CellValue.WALL) || (cSud != null && cSud.getNorth() == CellValue.WALL))) {
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