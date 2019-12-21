package model.solvingAlgorithm;

import model.util.Position;

/**
 * The Dijkstra Shortest Path solving algorithm<br />
 * This algorithm is a special case for the A* algorithm where the heuristic function always return 0
 * @author Eliastik
 * @version 1.1
 * @since 16/12/2019
 * @see <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm">https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm</a>
 */
public class Dijkstra extends AStar {
	public Dijkstra(boolean stepByStepSolve) {
		super(new AStarHeuristic() {
			@Override
			public int distance(Position position, Position other) {
				return 0;
			}
		}, stepByStepSolve);
	}

	public Dijkstra() {
		this(false);
	}
}