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
 * The Aldous Broder algorithm
 * @author Eliastik
 * @version 1.1
 * @since 30/11/2019
 * @see <a href="http://weblog.jamisbuck.org/2011/1/17/maze-generation-aldous-broder-algorithm.html">http://weblog.jamisbuck.org/2011/1/17/maze-generation-aldous-broder-algorithm.html</a>
 */
public class AldousBroder extends GenerationAlgorithmStrategy {
	@Override
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep) {
		int remaining = labyrinth.getWidth() * labyrinth.getHeight() - 1;
		Position currentPos = start;
		
		while(remaining > 0) {
			if(this.isStopped()) return;
			
			List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
			Collections.shuffle(directions, random);
			
			for(Direction dir : directions) {
				if(this.isStopped()) return;
				
				Position pos = labyrinth.getNeighbour(currentPos, dir, dir);
				Cell c = labyrinth.getCell(pos);
				
				if(pos != null) {
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
					
					if(c.getValue() == CellValue.WALL) {
						labyrinth.getCell(currentPos).setEdgeToDirection(dir, CellValue.EMPTY);
						c.setOppositeEdge(dir, CellValue.EMPTY);
						labyrinth.getCell(currentPos).setValue(CellValue.EMPTY);
						c.setValue(CellValue.EMPTY);
						remaining--;
					}
					
					currentPos = pos;
					break;
				}
			}
		}
	}
}