package model.algorithms;

import java.util.Random;

import model.Cell;
import model.CellValue;
import model.GenerationAlgorithm;
import model.Labyrinth;
import model.util.Direction;
import model.util.Position;

/**
 * The Side Winder algorithm<br />
 * Reference: http://weblog.jamisbuck.org/2011/2/3/maze-generation-sidewinder-algorithm.html
 * @author Eliastik
 * @version 1.0
 * @since 05/12/2019
 */
public class SideWinder implements GenerationAlgorithm {
	@Override
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep) {
		for(int i = 0; i < labyrinth.getHeight(); i++) {
			int run_start = 0;
			
			for(int j = 0; j < labyrinth.getWidth(); j++) {
				if(stepByStep) {
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
				if(i > 0 && (j + 1 == labyrinth.getWidth() || random.nextInt(2) == 0)) {
					int x = run_start + random.nextInt(j - run_start + 1);
					Position p = new Position(x, i);
					Cell c = labyrinth.getCell(p);
					Position pNorth = labyrinth.getNeighbour(p, Direction.NORTH, Direction.NORTH);
					Cell cNorth = labyrinth.getCell(pNorth);
					
					c.setValue(CellValue.EMPTY);
					c.setNorth(CellValue.EMPTY);
					cNorth.setValue(CellValue.EMPTY);
					cNorth.setSouth(CellValue.EMPTY);
					run_start = j + 1;
				} else {
					Position p = new Position(j, i);
					Cell c = labyrinth.getCell(p);
					Position pEast = labyrinth.getNeighbour(p, Direction.EAST, Direction.EAST);
					Cell cEast = labyrinth.getCell(pEast);
					
					c.setValue(CellValue.EMPTY);
					c.setEast(CellValue.EMPTY);
					cEast.setValue(CellValue.EMPTY);
					cEast.setWest(CellValue.EMPTY);
				}
			}
		}
	}
}