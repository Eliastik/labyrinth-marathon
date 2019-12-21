package model.solvingAlgorithm;

import model.util.Position;

/**
 * The Manhattan heuristic distance
 * @author Eliastik
 * @version 1.1
 * @since 16/12/2019
 *
 */
public class AStarHeuristicManhattan implements AStarHeuristic {
	@Override
	public int distance(Position position, Position other) {
		return Math.abs(position.getX() - other.getX()) + Math.abs(position.getY() - other.getY());
	}
}