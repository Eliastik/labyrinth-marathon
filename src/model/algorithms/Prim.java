package model.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Cell;
import model.CellValue;
import model.GenerationAlgorithmStrategy;
import model.Labyrinth;
import model.util.Direction;
import model.util.Position;

/**
 * The Prim's algorithm
 * @author Eliastik
 * @version 1.0
 * @since 06/12/2019
 * @see <a href="http://weblog.jamisbuck.org/2011/1/10/maze-generation-prim-s-algorithm.html">http://weblog.jamisbuck.org/2011/1/10/maze-generation-prim-s-algorithm.html</a>
 */
public class Prim implements GenerationAlgorithmStrategy {
	@Override
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep) {
		List<Position> frontier = new ArrayList<>();
		
		Position position;
		this.mark(labyrinth, start, frontier);
		
		while(!frontier.isEmpty()) {
			position = frontier.remove(random.nextInt(frontier.size()));
			Cell c = labyrinth.getCell(position);
			
			if(stepByStep) {
				CellValue currentVal = c.getValue();
				c.setValue(CellValue.CURRENT);
				
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				c.setValue(currentVal);
			}
			
			List<Position> neighbors = this.neighbours(labyrinth, position);
			
			if(!neighbors.isEmpty()) {
				Position positionNext = neighbors.get(random.nextInt(neighbors.size()));
				Cell cNext = labyrinth.getCell(positionNext);
				Direction direction = labyrinth.getNeighbour(position, positionNext);
				
				if(direction != null) {
					c.setValue(CellValue.EMPTY);
					c.setEdgeToDirection(direction, CellValue.EMPTY);
					cNext.setValue(CellValue.EMPTY);
					cNext.setOppositeEdge(direction, CellValue.EMPTY);
				}
			}
			
			this.mark(labyrinth, position, frontier);
		}
	}
	
	private void mark(Labyrinth labyrinth, Position position, List<Position> frontier) {
		Cell c = labyrinth.getCell(position);
		c.setValue(CellValue.EMPTY);
		
		this.addFrontier(labyrinth, position, labyrinth.getNeighbour(position, Direction.NORTH, Direction.NORTH), frontier);
		this.addFrontier(labyrinth, position, labyrinth.getNeighbour(position, Direction.EAST, Direction.EAST), frontier);
		this.addFrontier(labyrinth, position, labyrinth.getNeighbour(position, Direction.WEST, Direction.WEST), frontier);
		this.addFrontier(labyrinth, position, labyrinth.getNeighbour(position, Direction.SOUTH, Direction.SOUTH), frontier);
	}
	
	private void addFrontier(Labyrinth labyrinth, Position current, Position other, List<Position> frontier) {
		if(!current.equals(other)) {
			Cell c = labyrinth.getCell(other);
			
			if(other.getX() >= 0 && other.getY() >= 0 && other.getX() < labyrinth.getWidth() && other.getY() < labyrinth.getHeight() && c.getValue() == CellValue.WALL) {
				c.setValue(CellValue.FRONTIER);
				frontier.add(other);
			}
		}
	}
	
	private List<Position> neighbours(Labyrinth labyrinth, Position position) {
		List<Position> p = new ArrayList<>();
		
		Position north = labyrinth.getNeighbour(position, Direction.NORTH, Direction.NORTH);
		Position east = labyrinth.getNeighbour(position, Direction.EAST, Direction.EAST);
		Position west = labyrinth.getNeighbour(position, Direction.WEST, Direction.WEST);
		Position south = labyrinth.getNeighbour(position, Direction.SOUTH, Direction.SOUTH);
		
		if(labyrinth.getCell(north).getValue() == CellValue.EMPTY) {
			p.add(north);
		}
		
		if(labyrinth.getCell(east).getValue() == CellValue.EMPTY) {
			p.add(east);
		}
	
		if(labyrinth.getCell(west).getValue() == CellValue.EMPTY) {
			p.add(west);
		}
		
		if(labyrinth.getCell(south).getValue() == CellValue.EMPTY) {
			p.add(south);
		}
		
		return p;
	}
}