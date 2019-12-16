package model.generationAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import model.Cell;
import model.CellValue;
import model.GenerationAlgorithmStrategy;
import model.Labyrinth;
import model.util.Direction;
import model.util.Position;

/**
 * The Recursive Division algorithm
 * @author Eliastik
 * @version 1.0
 * @since 09/12/2019
 * @see <a href="http://weblog.jamisbuck.org/2011/1/12/maze-generation-recursive-division-algorithm.html">http://weblog.jamisbuck.org/2011/1/12/maze-generation-recursive-division-algorithm.html</a>
 */
public class RecursiveDivision extends GenerationAlgorithmStrategy {
	@Override
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep) {
		this.eraseGrid(labyrinth);
		this.divide(labyrinth, random, start, labyrinth.getWidth(), labyrinth.getHeight(), this.choose_orientation(labyrinth.getWidth(), labyrinth.getHeight(), random), stepByStep);
	}
	
	private void divide(Labyrinth labyrinth, Random random, Position pos, int width, int height, Orientation orientation, boolean stepByStep) {
		if(this.isStopped()) return;
		if(width <= 2 || height <= 2) return;
		
		if(stepByStep) {
			try {
				Thread.sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		int wx = pos.getX() + (orientation == Orientation.HORIZONTAL ? 0 : random.nextInt(width - 2));
		int wy = pos.getY() + (orientation == Orientation.HORIZONTAL ? random.nextInt(height - 2) : 0);
		
		int px = wx + (orientation == Orientation.HORIZONTAL ? random.nextInt(width) : 0);
		int py = wy + (orientation == Orientation.HORIZONTAL ? 0 : random.nextInt(height));
		
		int dx = orientation == Orientation.HORIZONTAL ? 1 : 0;
		int dy = orientation == Orientation.HORIZONTAL ? 0 : 1;
		
		int length = orientation == Orientation.HORIZONTAL ? width : height;
		
		Direction dir = orientation == Orientation.HORIZONTAL ? Direction.SOUTH : Direction.EAST;
		
		for(int i = 0; i < length; i++) {
			if(this.isStopped()) return;
			
			if(wx != px || wy != py) {
				labyrinth.getCell(new Position(wx, wy)).setEdgeToDirection(dir, CellValue.WALL);
				labyrinth.getCell(labyrinth.getNeighbour(new Position(wx, wy), dir, dir)).setOppositeEdge(dir, CellValue.WALL);
			}
			
			wx += dx;
			wy += dy;
		}
		
		int nx = pos.getX();
		int ny = pos.getY();
		
		int w = orientation == Orientation.HORIZONTAL ? width : (wx - pos.getX() + 1);
		int h = orientation == Orientation.HORIZONTAL ? (wy - pos.getY() + 1) : height;
		this.divide(labyrinth, random, new Position(nx, ny), w, h, this.choose_orientation(w, h, random), stepByStep);

		nx = orientation == Orientation.HORIZONTAL ? pos.getX() : (wx + 1);
		ny = orientation == Orientation.HORIZONTAL ? (wy + 1) : pos.getY();
		
		w = orientation == Orientation.HORIZONTAL ? width : (pos.getX() + width - wx - 1);
		h = orientation == Orientation.HORIZONTAL ? (pos.getY() + height - wy - 1) : height;
		this.divide(labyrinth, random, new Position(nx, ny), w, h, this.choose_orientation(w, h, random), stepByStep);
	}
	
	private Orientation choose_orientation(int width, int height, Random random) {
		if(width < height) {
			return Orientation.HORIZONTAL;
		} else if(height < width) {
			return Orientation.VERTICAL;
		} else {
			return random.nextInt(2) == 0 ? Orientation.HORIZONTAL : Orientation.VERTICAL;
		}
	}
	
	private enum Orientation {
		HORIZONTAL, VERTICAL;
	}
}
