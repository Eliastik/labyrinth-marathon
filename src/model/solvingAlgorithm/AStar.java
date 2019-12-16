package model.solvingAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import model.Cell;
import model.CellValue;
import model.Labyrinth;
import model.SolvingAlgorithmStrategy;
import model.util.Direction;
import model.util.Position;

/**
 * The A* Shortest Path solving algorithm
 * @author Eliastik
 * @version 1.0
 * @since 16/12/2019
 * @see <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">https://en.wikipedia.org/wiki/A*_search_algorithm</a>
 */
public class AStar extends SolvingAlgorithmStrategy {
	public AStar(boolean stepByStepSolve) {
		super(stepByStepSolve);
	}

	public AStar() {
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
		
		Queue<Node> open = new PriorityQueue<>();
		open.add(new Node(labyrinth.getStartPosition(), labyrinth.getPlayer().getPosition(), labyrinth.getEndPosition()));
		List<Node> closed = new ArrayList<>();
		
		Node current = null;
		
		while(!open.isEmpty()) {
			// Stop algorithm if game exited
			if(!labyrinth.isAutoPlayer()) {
				this.searchingPath = false;
				return null;
			}
			
			current = open.poll();
			
			if(this.isStepByStep()) {
				try {
					if(current != null && !current.getPosition().equals(labyrinth.getEndPosition())) labyrinth.getCell(current.getPosition()).setValue(CellValue.CURRENT);
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
			
			for(int i = 0; i < directions.size(); i++) {
				// Stop algorithm if game exited
				if(!labyrinth.isAutoPlayer()) {
					this.searchingPath = false;
					return null;
				}
				
				Position successorPosition = labyrinth.getNeighbour(current.getPosition(), directions.get(i), directions.get(i));
				
				if(successorPosition != null) {
					Cell currentCell = labyrinth.getCell(current.getPosition());
					Cell successorCell = labyrinth.getCell(successorPosition);
					
					if(this.isStepByStep()) {
						try {
							if(successorPosition != null && !successorPosition.equals(labyrinth.getEndPosition())) labyrinth.getCell(successorPosition).setValue(CellValue.FRONTIER);
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					if(labyrinth.canMoveTo(currentCell, successorCell, directions.get(i))) {
						if(successorPosition.equals(labyrinth.getEndPosition())) {
							this.searchingPath = false;
							return this.reconstructPath(closed);
						}
						
						Node successor = new Node(labyrinth.getStartPosition(), successorPosition, labyrinth.getEndPosition());
						Node samePositionInOpenList = this.samePosition(open, successor);
						
						if(samePositionInOpenList != null && samePositionInOpenList.getCost() < successor.getCost()) {
							System.out.println(samePositionInOpenList.getPosition() + " " + successor.getPosition());
							continue;
						}
						
						if(closed.indexOf(successor) > -1) {
							Node samePositionInClosedList = closed.get(closed.indexOf(successor));
							
							if(samePositionInClosedList.getCost() < successor.getCost()) {
								continue;
							}
						}
						
						open.add(successor);
					}
				}
			}
			
			closed.add(current);
		}
		
		return null;
	}
	
	public Queue<Position> reconstructPath(List<Node> nodes) {
		Queue<Position> result = new LinkedList<>();
		
		for(int i = 0; i < nodes.size(); i++) {
			result.add(nodes.get(i).getPosition());
		}
		
		System.out.println(result);
		
		return result;
	}
	
	public Node samePosition(Queue<Node> queue, Node node) {
		for(Node other : queue) {
			if(other.equals(node)) {
				return other;
			}
		}
		
		return null;
	}
	
	private class Node implements Comparable<Node> {
		private Position position;
		private Position startPosition;
		private Position endPosition;
		
		Node(Position startPosition, Position position, Position endPosition) {
			this.startPosition = startPosition;
			this.position = position;
			this.endPosition = endPosition;
		}
		
		/**
		 * Process the distance between this node and the end node using Manhattan distance
		 * @return (int) The distance
		 */
		public int getHeuristicDistance() {
			return Math.abs(this.getPosition().getX() - this.endPosition.getX()) + Math.abs(this.getPosition().getY() - this.endPosition.getY());
		}
		
		/**
		 * Process the distance between this node and its parent
		 * @return (int) The distance
		 */
		public int getDistanceFromParent() {
			if(this.startPosition == null) {
				return 0;
			}
			
			return Math.abs(this.getPosition().getX() - this.startPosition.getX()) + Math.abs(this.getPosition().getY() - this.startPosition.getY());
		}

		/**
		 * Process the heuristic and parent distance then calculate the cost of this node
		 * @return (int) The cost
		 */
		public int getCost() {
			return this.getDistanceFromParent() + this.getHeuristicDistance();
		}
		
		/**
		 * Get the position of this node
		 * @return ({@link Position}) The position
		 */
		public Position getPosition() {
			return this.position;
		}

		@Override
		public int compareTo(Node other) {
			int c = this.getCost() - other.getCost();
			return (c > 0) ? 1 : (c < 0) ? -1 : 0;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((position == null) ? 0 : position.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Node other = (Node) obj;
			if (position == null) {
				if (other.position != null)
					return false;
			} else if (!position.equals(other.position))
				return false;
			return true;
		}
		
		@Override
		public String toString() {
			return "[Node] position = " + this.getPosition() + " ; parentDistance = " + this.getDistanceFromParent() + " ; heuristicDistance = " + this.getHeuristicDistance() + " ; cost = " + this.getCost();
		}
	}
}