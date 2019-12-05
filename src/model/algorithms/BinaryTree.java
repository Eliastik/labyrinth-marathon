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
 * The Binary Tree algorithm
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 * @see <a href="http://weblog.jamisbuck.org/2011/2/1/maze-generation-binary-tree-algorithm.html">http://weblog.jamisbuck.org/2011/2/1/maze-generation-binary-tree-algorithm.html</a>
 */
public class BinaryTree implements GenerationAlgorithmStrategy {
	@Override
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep) {
		for(int i = 0; i < labyrinth.getHeight(); i++) {
			for(int j = 0; j < labyrinth.getWidth(); j++) {
				Direction dir;
				List<Direction> directions = new ArrayList<>();
				if(i > 0) directions.add(Direction.NORTH);
				if(j > 0) directions.add(Direction.WEST);
				
				if(directions.size() > 0) {
					dir = directions.get(random.nextInt(directions.size()));
					
					Position pos = new Position(j, i);
					Cell c = labyrinth.getCell(pos);
					c.setValue(CellValue.CURRENT);
					
					Position posOther = labyrinth.getNeighbour(pos, dir, dir);
					Cell cOther = labyrinth.getCell(posOther);
					
					if(stepByStep) {
						try {
							Thread.sleep(150);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					c.setEdgeToDirection(dir, CellValue.EMPTY);
					cOther.setOppositeEdge(dir, CellValue.EMPTY);
					c.setValue(CellValue.EMPTY);
					cOther.setValue(CellValue.EMPTY);
				}
			}
		}
	}
}