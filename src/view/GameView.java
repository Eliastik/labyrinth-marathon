package view;

import controller.GameController;

/**
 * This interface represents a view according to the MVC pattern
 * @author Eliastik
 * @version 1.1
 * @since 05/12/2019
 *
 */
public interface GameView {
	/**
	 * Update the view<br>
	 * The controller uses this method to warn the view about a change in the model
	 * @param moveSucceeded (boolean) The player moved successfully
	 */
	public void update(boolean moveSucceeded);
	/**
	 * Set the controller in the view
	 * @param controller (GameController) The controller
	 */
	public void setController(GameController controller);
	/**
	 * Run the view
	 */
	public void run();
	/**
	 * Inform if the view is exited
	 * @return (boolean)
	 */
	public boolean isExited();
}