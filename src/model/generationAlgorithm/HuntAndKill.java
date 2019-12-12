package model.generationAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.Cell;
import model.CellValue;
import model.GenerationAlgorithmStrategy;
import model.Labyrinth;
import model.util.Direction;
import model.util.Position;

/**
 * The Hunt and Kill algorithm
 * @author Eliastik
 * @version 1.0
 * @since 05/12/2019
 * @see <a href="http://weblog.jamisbuck.org/2011/1/24/maze-generation-hunt-and-kill-algorithm.html">http://weblog.jamisbuck.org/2011/1/24/maze-generation-hunt-and-kill-algorithm.html</a>
 */
public class HuntAndKill extends GenerationAlgorithmStrategy {
	@Override
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep) {
		Position current = start;
		boolean[] huntFinished = new boolean[labyrinth.getHeight()];
		
		while(current != null) {
			if(this.isStopped()) return;
			
			current = this.walk(labyrinth, random, current, end, stepByStep);
			if(current == null) current = this.hunt(labyrinth, random, stepByStep, huntFinished);
		}
	}
	
	private Position walk(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep) {
		if(start == null) return null;
		
		Position p = start;
		Cell c = labyrinth.getCell(p);
		
		if(stepByStep) {
			CellValue initialValue = c.getValue();
			c.setValue(CellValue.CURRENT);
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			c.setValue(initialValue);
		}
		
		List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
		Collections.shuffle(directions, random);
		
		for(Direction dir : directions) {
			if(this.isStopped()) return null;
			
			Position pNext = labyrinth.getNeighbour(p, dir, dir);
			Cell cNext = labyrinth.getCell(pNext);
			
			if(cNext != null && cNext.getValue() == CellValue.WALL) {
				c.setValue(CellValue.EMPTY);
				c.setEdgeToDirection(dir, CellValue.EMPTY);
				cNext.setValue(CellValue.EMPTY);
				cNext.setOppositeEdge(dir, CellValue.EMPTY);
				
				return pNext;
			}
		}
		
		return null;
	}
	
	private Position hunt(Labyrinth labyrinth, Random random, boolean stepByStep, boolean[] finished) {
		for(int i = 0; i < labyrinth.getHeight(); i++) {
			if(!finished[i]) {
				for(int j = 0; j < labyrinth.getWidth(); j++) {
					if(this.isStopped()) return null;
					
					Position p = new Position(j, i);
					Cell c = labyrinth.getCell(p);
					
					if(stepByStep) {
						CellValue initialValue = c.getValue();
						c.setValue(CellValue.CURRENT);
						
						try {
							Thread.sleep(25);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						
						c.setValue(initialValue);
					}
					
					if(c.getValue() == CellValue.WALL) {
						List<Direction> neighbours = new ArrayList<>();
						
						Cell cNorth = labyrinth.getCell(labyrinth.getNeighbour(p, Direction.NORTH, Direction.NORTH));
						Cell cSouth = labyrinth.getCell(labyrinth.getNeighbour(p, Direction.SOUTH, Direction.SOUTH));
						Cell cEast = labyrinth.getCell(labyrinth.getNeighbour(p, Direction.EAST, Direction.EAST));
						Cell cWest = labyrinth.getCell(labyrinth.getNeighbour(p, Direction.WEST, Direction.WEST));
						
						if(cNorth != null && cNorth.getValue() != CellValue.WALL) {
							neighbours.add(Direction.NORTH);
						}
						
						if(cSouth != null && cSouth.getValue() != CellValue.WALL) {
							neighbours.add(Direction.SOUTH);
						}
						
						if(cEast != null && cEast.getValue() != CellValue.WALL) {
							neighbours.add(Direction.EAST);
						}
						
						if(cWest != null && cWest.getValue() != CellValue.WALL) {
							neighbours.add(Direction.WEST);
						}
						
						if(neighbours.size() <= 0) continue;
						
						Direction dirChoice = neighbours.get(random.nextInt(neighbours.size()));
						
						c.setValue(CellValue.EMPTY);
						c.setEdgeToDirection(dirChoice, CellValue.EMPTY);
						labyrinth.getCell(labyrinth.getNeighbour(p, dirChoice, dirChoice)).setValue(CellValue.EMPTY);
						labyrinth.getCell(labyrinth.getNeighbour(p, dirChoice, dirChoice)).setOppositeEdge(dirChoice, CellValue.EMPTY);

						finished[i] = false;
						return p;
					} else {
						finished[i] = true;
						continue;
					}
				}
			}
		}
		
		return null;
	}
}