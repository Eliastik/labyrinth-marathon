package model.solvingAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import model.Cell;
import model.CellValue;
import model.Labyrinth;
import model.SolvingAlgorithmStrategy;
import model.util.Direction;
import model.util.Position;

public class BreadthFirstSearch extends SolvingAlgorithmStrategy {
	public BreadthFirstSearch(boolean stepByStepSolve) {
		super(stepByStepSolve);
	}

	public BreadthFirstSearch() {
		super(false);
	}

	@Override
	public Queue<Position> getPath(Labyrinth labyrinth) {
		if(!labyrinth.isAutoPlayer()) {
			this.searchingPath = false;
			return null;
		}
		
		if(this.searchingPath) return null;
		if(labyrinth.getPlayer().getPosition().equals(labyrinth.getEndPosition())) return null;
		
		this.searchingPath = true;
		Queue<List<Position>> queue = new LinkedList<>();
		List<Position> explore = new ArrayList<>();
		List<Position> pathToEnd = new ArrayList<>();
		Position current = null;
		
		pathToEnd.add(labyrinth.getPlayer().getPosition());
		queue.add(pathToEnd);
		
		while(!queue.isEmpty()) {
			if(!labyrinth.isAutoPlayer()) {
				this.searchingPath = false;
				return null;
			}
			
			pathToEnd = queue.poll();
			current = pathToEnd.get(pathToEnd.size() - 1);
			
			if(this.isStepByStep() && !current.equals(labyrinth.getEndPosition())) {
				try {
					labyrinth.getCell(current).setValue(CellValue.CURRENT);
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			if(current.equals(labyrinth.getEndPosition())) {
				this.searchingPath = false;
				if(this.isStepByStep()) this.cleanStepByStep(labyrinth);
				return new LinkedList<>(pathToEnd);
			} else {
				ArrayList<Position> neightbours = new ArrayList<>();
				List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
				
				for(int i = 0; i < directions.size(); i++) {
					Position posNeighbour = labyrinth.getNeighbour(current, directions.get(i), directions.get(i));
					Cell neighbour = labyrinth.getCell(posNeighbour);
					
					if(this.isStepByStep()) {
						try {
							if(neighbour != null && !posNeighbour.equals(labyrinth.getEndPosition())) neighbour.setValue(CellValue.FRONTIER);
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					if(posNeighbour != null && !explore.contains(posNeighbour) && !posNeighbour.equals(current) && labyrinth.canMoveTo(labyrinth.getCell(current), neighbour, directions.get(i))) {
						List<Position> path = new ArrayList<>(pathToEnd);
						path.add(posNeighbour);
						queue.add(path);
						
						neightbours.add(posNeighbour);
						explore.add(posNeighbour);
					}
				}
				
				if(neightbours.isEmpty() && queue.isEmpty()) {
					this.searchingPath = false;
					if(this.isStepByStep()) this.cleanStepByStep(labyrinth);
					return null;
				}
			}
	    }
	
		this.searchingPath = false;
		if(this.isStepByStep()) this.cleanStepByStep(labyrinth);
		return null;
	}
}