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
 * @version 1.1
 * @since 16/12/2019
 * @see <a href="https://en.wikipedia.org/wiki/A*_search_algorithm">https://en.wikipedia.org/wiki/A*_search_algorithm</a>
 */
public class AStar extends SolvingAlgorithmStrategy {
	private AStarHeuristic heuristic = new AStarHeuristicManhattan();
	
	public AStar(AStarHeuristic heuristic, boolean stepByStepSolve) {
		super(stepByStepSolve);
		this.heuristic = heuristic;
	}
	
	public AStar(boolean stepByStepSolve) {
		this(new AStarHeuristicManhattan(), stepByStepSolve);
	}

	public AStar() {
		this(false);
	}
	
	@Override
	public Queue<Position> getPath(Labyrinth labyrinth) {
		if(!labyrinth.isAutoPlayer()) {
			this.searchingPath = false;
			return null;
		}
		
		if(this.searchingPath) return null;
		if(labyrinth.getPlayer().getPosition().equals(labyrinth.getEndPosition())) return null;
		if(!labyrinth.isGenerationFinished()) return null;
		
		this.searchingPath = true;
		Queue<Node> open = new PriorityQueue<>();
		open.add(new Node(null, labyrinth.getPlayer().getPosition(), labyrinth.getEndPosition()));
		List<Node> closed = new ArrayList<>();
		
		Node current = null;
		
		while(!open.isEmpty()) {
			// Stop algorithm if game exited
			if(!labyrinth.isAutoPlayer()) {
				this.searchingPath = false;
				if(this.isStepByStep()) this.cleanStepByStep(labyrinth);
				return null;
			}
			
			current = open.poll();
			closed.add(current);
			
			if(current.getPosition().equals(labyrinth.getEndPosition())) {
				this.searchingPath = false;
				if(this.isStepByStep()) this.cleanStepByStep(labyrinth);
				return this.reconstructPath(closed);
			}
			
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
					if(this.isStepByStep()) this.cleanStepByStep(labyrinth);
					return null;
				}
				
				Position successorPosition = labyrinth.getNeighbour(current.getPosition(), directions.get(i), directions.get(i));
				Node successor = new Node(current, successorPosition, labyrinth.getEndPosition());
				
				if(successorPosition != null) {
					Cell currentCell = labyrinth.getCell(current.getPosition());
					Cell successorCell = labyrinth.getCell(successorPosition);
					
					if(labyrinth.canMoveTo(currentCell, successorCell, directions.get(i)) && closed.indexOf(successor) <= -1) {
						if(this.isStepByStep()) {
							try {
								if(successorPosition != null && !successorPosition.equals(labyrinth.getEndPosition())) labyrinth.getCell(successorPosition).setValue(CellValue.FRONTIER);
								Thread.sleep(50);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						Node samePositionInOpenList = this.samePosition(open, successor);
						
						if(current.getCost() > successor.getCost() || samePositionInOpenList == null) {
							if(samePositionInOpenList == null) {
								open.add(successor);
							}
						}
					}
				}
			}
		}

		this.searchingPath = false;
		if(this.isStepByStep()) this.cleanStepByStep(labyrinth);
		return null;
	}
	
	/**
	 * Reconstruct the path found with the A* algorithm<br />
	 * We begin with the last node found (usually the end position of the labyrinth), then we iterate through the parent of each node<br />
	 * Finally we reverse the constructed list into a Queue
	 * @param nodes ({@link List}&lt;{@link Node}&gt;) The list of the nodes found with the A* algorithm
	 * @return ({@link Queue}&lt;{@link Position}&gt;) The final path
	 */
	private Queue<Position> reconstructPath(List<Node> nodes) {
		List<Position> tmp = new ArrayList<>();
		Node current = nodes.get(nodes.size() - 1);
		
		while(current != null) {
			tmp.add(current.getPosition());
			current = current.parent;
		}

		Queue<Position> result = new LinkedList<>();
		
		for(int i = tmp.size() - 1; i >= 0; i--) {
			result.add(tmp.get(i));
		}
		
		return result;
	}
	
	/**
	 * Return the first node on the queue with the same position as the node passed in parameter
	 * @param queue ({@link Queue}&lt;{@link Node}&gt;) The queue
	 * @param node ({@link Node}) The node
	 * @return ({@link Node})
	 */
	private Node samePosition(Queue<Node> queue, Node node) {
		for(Node other : queue) {
			if(other.equals(node)) {
				return other;
			}
		}
		
		return null;
	}
	
	/**
	 * Data structure used for the A* algorithm
	 * @author Eliastik
	 * @since 16/12/2019
	 *
	 */
	private class Node implements Comparable<Node> {
		private Position position;
		private Node parent;
		private Position endPosition;
		
		Node(Node parentPosition, Position position, Position endPosition) {
			this.parent = parentPosition;
			this.position = position;
			this.endPosition = endPosition;
		}
		
		/**
		 * Process the distance between this node and the end node using the heuristic distance
		 * @return (int) The distance
		 */
		public int getHeuristicDistance() {
			return heuristic.distance(this.getPosition(), this.endPosition);
		}
		
		/**
		 * Process the distance between this node and the start node using the distance to parent
		 * @return (int) The distance
		 */
		public int getDistanceFromStart() {
			if(this.parent == null) return 0;
			return this.parent.getDistanceFromStart() + Math.abs(this.getPosition().getX() - this.parent.getPosition().getX()) + Math.abs(this.getPosition().getY() - this.parent.getPosition().getY());
		}

		/**
		 * Calculate and return the cost of this node
		 * @return (int) The cost
		 */
		public int getCost() {
			return this.getDistanceFromStart() + this.getHeuristicDistance();
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
			return "[Node] position = " + this.getPosition() + " ; startDistance = " + this.getDistanceFromStart() + " ; heuristicDistance = " + this.getHeuristicDistance() + " ; cost = " + this.getCost() + " ; parent = " + this.parent;
		}
	}
}