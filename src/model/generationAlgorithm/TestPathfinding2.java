package model.generationAlgorithm;

import java.util.Random;

import model.Cell;
import model.CellValue;
import model.GenerationAlgorithmStrategy;
import model.Labyrinth;
import model.util.Direction;
import model.util.Position;

/**
 * A test for the pathfinding (solving) algorithms
 * @author Eliastik
 * @version 1.1
 * @since 19/12/2019
 */
public class TestPathfinding2 extends GenerationAlgorithmStrategy {
	@Override
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep) {
		this.eraseGrid(labyrinth);
		int middleGridWidth = labyrinth.getWidth() / 2;
		int middleGridHeight = labyrinth.getHeight() / 2;
		
		for(int i = 0; i < labyrinth.getHeight(); i++) {
			if(this.isStopped()) return;
			
			Position current = new Position(middleGridWidth, i);
			Cell currentCell = labyrinth.getCell(current);
				
			Position west = labyrinth.getNeighbour(current, Direction.WEST, Direction.WEST);
			Cell westCell = labyrinth.getCell(west);
			Position east = labyrinth.getNeighbour(current, Direction.EAST, Direction.EAST);
			Cell eastCell = labyrinth.getCell(east);
			Position north = labyrinth.getNeighbour(current, Direction.NORTH, Direction.NORTH);
			Cell northCell = labyrinth.getCell(north);
			Position south = labyrinth.getNeighbour(current, Direction.SOUTH, Direction.SOUTH);
			Cell southCell = labyrinth.getCell(south);
			
			if(i != middleGridHeight && i != 0 && i != labyrinth.getHeight() - 1) {
				currentCell.setValue(CellValue.WALL);
				currentCell.setEdgeToDirection(Direction.WEST, CellValue.WALL);
				currentCell.setEdgeToDirection(Direction.EAST, CellValue.WALL);
				currentCell.setEdgeToDirection(Direction.NORTH, CellValue.WALL);
				currentCell.setEdgeToDirection(Direction.SOUTH, CellValue.WALL);
				
				if(westCell != null) {
					westCell.setOppositeEdge(Direction.WEST, CellValue.WALL);
				}
				
				if(eastCell != null) {
					eastCell.setOppositeEdge(Direction.EAST, CellValue.WALL);
				}
				
				if(northCell != null) {
					northCell.setOppositeEdge(Direction.NORTH, CellValue.WALL);
				}
				
				if(southCell != null) {
					southCell.setOppositeEdge(Direction.SOUTH, CellValue.WALL);
				}
			}
		}
	}
}