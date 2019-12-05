package model;

import java.util.Random;

import model.util.Position;

/**
 * The interface that must be implemented by a labyrinth generation algorithm
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public interface GenerationAlgorithm {
	/**
	 * Generate a labyrinth
	 * @param labyrinth ({@link Labyrinth})
	 * @param random ({@link Random}) A seedable Random
	 * @param start ({@link Position}) The start position of the player / some algorithms can use it as start position in their process
	 * @param end ({@link Position}) The end position where the player must go
	 * @param stepByStep (boolean) The algorithm sleeps between iterations to demonstrate how it works (to be run by an independent Thread)
	 */
	public void generate(Labyrinth labyrinth, Random random, Position start, Position end, boolean stepByStep);
}