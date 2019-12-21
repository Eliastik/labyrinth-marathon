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
 * The Growing Tree algorithm
 * @author Eliastik
 * @version 1.1
 * @since 05/12/2019
 * @see <a href="http://weblog.jamisbuck.org/2011/1/27/maze-generation-growing-tree-algorithm.html">http://weblog.jamisbuck.org/2011/1/27/maze-generation-growing-tree-algorithm.html</a>
 */
public class GrowingTree extends GenerationAlgorithmStrategy {
	@Override
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep) {
		List<Position> cells = new ArrayList<>();
		cells.add(start);
		int index = 0;
		
		while(!cells.isEmpty()) {
			if(this.isStopped()) return;
			
			index = random.nextInt(cells.size());
			Position p = cells.get(index);
			Cell c = labyrinth.getCell(p);
			
			if(stepByStep) {
				CellValue initialValue = c.getValue();
				c.setValue(CellValue.CURRENT);
				
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				c.setValue(initialValue);
			}
			
			List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
			Collections.shuffle(directions, random);
			
			for(Direction dir : directions) {
				if(this.isStopped()) return;
				
				Position pNext = labyrinth.getNeighbour(p, dir, dir);
				
				if(pNext != null) {
					Cell cNext = labyrinth.getCell(pNext);
					
					if(cNext.getValue() == CellValue.WALL) {
						c.setValue(CellValue.EMPTY);
						c.setEdgeToDirection(dir, CellValue.EMPTY);
						cNext.setValue(CellValue.EMPTY);
						cNext.setOppositeEdge(dir, CellValue.EMPTY);
						cells.add(pNext);
						index = -1;
						break;
					}
				}
			}
			
			if(index > -1) cells.remove(index);
		}
	}
}