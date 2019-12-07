package model.algorithms;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import model.Cell;
import model.CellValue;
import model.GenerationAlgorithmStrategy;
import model.Labyrinth;
import model.util.Direction;
import model.util.Position;

/**
 * The Wilson's algorithm
 * @author Eliastik
 * @version 1.0
 * @since 07/12/2019
 * @see <a href="http://weblog.jamisbuck.org/2011/1/20/maze-generation-wilson-s-algorithm.html">http://weblog.jamisbuck.org/2011/1/20/maze-generation-wilson-s-algorithm.html</a>
 */
public class Wilson extends GenerationAlgorithmStrategy {
	@Override
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep) {
		labyrinth.getCell(start).setValue(CellValue.EMPTY);
		int remaining = labyrinth.getWidth() * labyrinth.getHeight() - 1;
		
		List<Map.Entry<Position, Direction>> walk;
		
		while(remaining > 0) {
			if(this.isStopped()) return;
			
			walk = this.walk(labyrinth, random, stepByStep);
			
			if(walk != null) {
				for(Map.Entry<Position, Direction> e : walk) {
					if(this.isStopped()) return;
					
					Position current = e.getKey();
					Cell cCurrent = labyrinth.getCell(current);
					
					if(stepByStep) {
						CellValue initialValue = cCurrent.getValue();
						cCurrent.setValue(CellValue.CURRENT);
						
						try {
							Thread.sleep(150);
						} catch (InterruptedException e2) {
							e2.printStackTrace();
						}
						
						cCurrent.setValue(initialValue);
					}
					
					Position next = labyrinth.getNeighbour(e.getKey(), e.getValue(), e.getValue());
					Cell cNext = labyrinth.getCell(next);
					
					cCurrent.setValue(CellValue.EMPTY);
					cCurrent.setOppositeEdge(e.getValue(), CellValue.EMPTY);
					cNext.setValue(CellValue.EMPTY);
					cNext.setEdgeToDirection(e.getValue(), CellValue.EMPTY);
					
					remaining--;
				}
			}
		}
	}
	
	public List<Map.Entry<Position, Direction>> walk(Labyrinth labyrinth,  Random random, boolean stepByStep) {
		while(true) {
			if(this.isStopped()) return null;
			
			Position pCurrent = Position.random(labyrinth.getWidth(), labyrinth.getHeight(), random);
			Cell cCurrent = labyrinth.getCell(pCurrent);
			
			if(cCurrent.getValue() != CellValue.WALL) continue;
			
			Map<Position, Direction> visits = new HashMap<>();
			visits.put(pCurrent, null);
			
			Position start = pCurrent;
			boolean walking = true;
			
			while(walking) {
				if(this.isStopped()) return null;
				
				walking = false;
				
				List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
				Collections.shuffle(directions, random);
				
				for(Direction dir : directions) {
					if(this.isStopped()) return null;
					
					Position pNext = labyrinth.getNeighbour(pCurrent, dir, dir);
					Cell cNext = labyrinth.getCell(pNext);
					
					if(pNext != null) {
						if(stepByStep) {
							CellValue initialValue = cNext.getValue();
							cNext.setValue(CellValue.CURRENT);
							
							try {
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							
							cNext.setValue(initialValue);
						}
						
 						visits.put(pCurrent, dir);
						
						if(labyrinth.getCell(pNext).getValue() != CellValue.WALL) {
							break;
						} else {
							pCurrent = pNext;
							walking = true;
							break;
						}
					}
				}
			}
			
			List<Map.Entry<Position, Direction>> path = new ArrayList<>();
			Position p = start;
			
			while(true) {
				if(this.isStopped()) return null;
				
				Direction dir = visits.get(p);
				if(dir == null) break;
				path.add(new AbstractMap.SimpleEntry<Position, Direction>(p, dir));
				p = labyrinth.getNeighbour(p, dir, dir);
			}

			return path;
		}
	}
}