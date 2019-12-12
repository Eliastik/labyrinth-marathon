package model;

import java.util.Queue;

import model.util.Position;

/**
 * The class that must be extended by a labyrinth solving algorithm
 * @author Eliastik
 * @version 1.0
 * @since 12/12/2019
 */
public abstract class SolvingAlgorithmStrategy {
	private boolean stopped = false;
	protected boolean searchingPath = false;
	
	/**
	 * Solve the labyrinth
	 * @return ({@link Queue}<{@link Position}>) The path to the end position, or null if no path was found
	 */
	public abstract Queue<Position> getPath(Labyrinth labyrinth);
	
	/**
	 * Stop the solving
	 */
	public void stop() {
		this.stopped = true;
	}

	/**
	 * Inform if the solving is stopped
	 * @return (boolean) true if the solving is stopped, false otherwise
	 */
	public boolean isStopped() {
		return stopped;
	}
	
	/**
	 * Reverse the stop flag
	 */
	public void restart() {
		this.stopped = false;
	}

	/**
	 * Inform if the path searching process is running
	 * @return (boolean)
	 */
	public boolean isSearchingPath() {
		return searchingPath;
	}
}