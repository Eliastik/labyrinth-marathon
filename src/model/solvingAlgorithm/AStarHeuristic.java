package model.solvingAlgorithm;

import model.util.Position;

/**
 * The interface to be implemented by an heuristic function for the A* algorithm
 * @author Eliastik
 * @version 1.1
 * @since 16/12/2019
 *
 */
public interface AStarHeuristic {
	public int distance(Position position, Position other);
}