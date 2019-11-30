package model;

import java.util.Random;

import util.Position;

/**
 * The interface that must be implemented by a labyrinth generation algorithm
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public interface GenerationAlgorithm {
	/**
	 * Generate a labyrinth
	 * @param labyrinth (Labyrinth)
	 * @param random (Random) A seedable Random
	 * @param start (Position) The start position of the player / some algorithms can use it as start position of the process
	 * @param end (Position) The end position where the player must go
	 */
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end);
}