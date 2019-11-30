package algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import model.Cell;
import model.CellValue;
import model.GenerationAlgorithm;
import model.Labyrinth;
import util.Direction;
import util.Position;

/**
 * The Aldous Broder algorithm<br />
 * Reference: http://weblog.jamisbuck.org/2011/1/17/maze-generation-aldous-broder-algorithm.html
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public class AldousBroder implements GenerationAlgorithm {
	@Override
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end) {
		int remaining = labyrinth.getWidth() * labyrinth.getHeight() - 1;
		Position currentPos = start;
		
		while(remaining > 0) {
			List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
			Collections.shuffle(directions, random);
			
			for(Direction dir : directions) {
				Position pos = labyrinth.getNeighbour(currentPos, dir, dir);
				Cell c = labyrinth.getCase(pos);
				
				if(pos.getX() >= 0 && pos.getX() < labyrinth.getWidth() && pos.getY() >= 0 && pos.getY() < labyrinth.getHeight()) {
					if(c.getValue() == CellValue.WALL) {
						labyrinth.getCase(currentPos).setEdgeToDirection(dir, CellValue.EMPTY);
						c.setOppositeEdge(dir, CellValue.EMPTY);
						labyrinth.getCase(currentPos).setValue(CellValue.EMPTY);
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