package model.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import model.Cell;
import model.CellValue;
import model.GenerationAlgorithm;
import model.Labyrinth;
import model.util.Direction;
import model.util.Position;

/**
 * The Recursive Backtracking algorithm<br />
 * Reference: http://weblog.jamisbuck.org/2010/12/27/maze-generation-recursive-backtracking
 * @author Eliastik
 * @version 1.0
 * @since 05/12/2019
 */
public class RecursiveBacktracking implements GenerationAlgorithm {
	@Override
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep) {
		Stack<Position> s = new Stack<>();
		s.push(start);
		
		while(!s.isEmpty()) {
			List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
			Collections.shuffle(directions, random);
			
			Position current = s.pop();
			
			for(Direction d : directions) {
				if(stepByStep) {
					try {
						Thread.sleep(150);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				Cell cCurrent = labyrinth.getCell(current);
				
				Position pNew = labyrinth.getNeighbour(current, d, d);
				Cell cNew = labyrinth.getCell(pNew);
				
				if(pNew.getY() >= 0 && pNew.getY() < labyrinth.getHeight() && pNew.getX() >= 0 && pNew.getX() < labyrinth.getWidth() && cNew.getValue() == CellValue.WALL) {
					cCurrent.setValue(CellValue.EMPTY);
					cCurrent.setEdgeToDirection(d, CellValue.EMPTY);
					cNew.setOppositeEdge(d, CellValue.EMPTY);
					cNew.setValue(CellValue.EMPTY);
					s.push(pNew);
				}
			}
		}
	}
}
