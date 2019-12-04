package view;

import controller.GameController;

public interface GameView {
	public void update(boolean moveSucceeded);
	public void setController(GameController controller);
	public void run();
}
