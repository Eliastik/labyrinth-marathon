package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import model.util.Direction;
import model.util.Position;

/**
 * The class that must be extended by a labyrinth generation algorithm
 * @author Eliastik
 * @version 1.1
 * @since 30/11/2019
 */
public abstract class GenerationAlgorithmStrategy {
	private boolean stopped = false;
	
	/**
	 * Generate a labyrinth
	 * @param labyrinth ({@link Labyrinth})
	 * @param random ({@link Random}) A seedable Random
	 * @param start ({@link Position}) The start position of the player / some algorithms can use it as start position in their process
	 * @param end ({@link Position}) The end position where the player must go
	 * @param stepByStep (boolean) The algorithm sleeps between iterations to demonstrate how it works (to be run by an independent Thread)
	 */
	public abstract void generate(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep);
	
	/**
	 * Erase the grid of the labyrinth passed in parameter
	 * @param labyrinth ({@link Labyrinth}) The labyrinth
	 */
	public void eraseGrid(Labyrinth labyrinth) {
		for(int i = 0; i < labyrinth.getHeight(); i++) {
			for(int j = 0; j < labyrinth.getWidth(); j++) {
				Position pos = new Position(j, i);
				Cell cell = labyrinth.getCell(pos);
				cell.setValue(CellValue.EMPTY);
				
				List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
				
				for(Direction dir : directions) {
					cell.setEdgeToDirection(dir, CellValue.EMPTY);
					cell.setOppositeEdge(dir, CellValue.EMPTY);
					
					Cell neighbour = labyrinth.getCell(labyrinth.getNeighbour(pos, dir, dir));
					
					if(neighbour != null) {
						neighbour.setValue(CellValue.EMPTY);
						neighbour.setEdgeToDirection(dir, CellValue.EMPTY);
						neighbour.setOppositeEdge(dir, CellValue.EMPTY);
					}
				}
			}
		}
	}
	
	/**
	 * Stop the generation
	 */
	public void stop() {
		this.stopped = true;
	}

	/**
	 * Inform if the generation is stopped
	 * @return (boolean) true if the generation is stopped, false otherwise
	 */
	public boolean isStopped() {
		return stopped;
	}
	
	/**
	 * Reverse the stop flag
	 */
	public void restart() {
		this.stopped = false;
	}
}