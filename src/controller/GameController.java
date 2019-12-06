package controller;

import java.util.Queue;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import model.Cell;
import model.Labyrinth;
import model.util.Direction;
import model.util.Position;
import view.GameView;

/**
 * This class represents the controller according to the MVC pattern
 * @author Eliastik
 * @version 1.0
 * @since 05/12/2019
 *
 */
public class GameController {
	private Labyrinth labyrinth;
	private GameView view;
	private Thread threadCheckBlocked;
	
	public GameController(Labyrinth labyrinth, GameView view) {
		super();
		this.labyrinth = labyrinth;
		this.view = view;
	}
	
	/**
	 * {@link model.Labyrinth#getWidth()}
	 */
	public int getLabyrinthWidth() {
		return labyrinth.getWidth();
	}
	
	/**
	 * {@link model.Labyrinth#getHeight()}
	 */
	public int getLabyrinthHeight() {
		return labyrinth.getHeight();
	}
	
	/**
	 * {@link model.Player#getPosition()}
	 */
	public Position getPlayerPosition() {
		return labyrinth.getPlayer().getPosition();
	}
	
	/**
	 * {@link model.Player#getDirection()}
	 */
	public Direction getPlayerDirection() {
		return labyrinth.getPlayer().getDirection();
	}
	
	/**
	 * {@link model.Labyrinth#getCell(Position position)}
	 * @param position (Position)
	 */
	public Cell getCell(Position position) {
		return labyrinth.getCell(position);
	}
	
	/**
	 * {@link model.Labyrinth#getNeighbour(Position cellPosition, Direction directionX, Direction directionY)}
	 * @param cellPosition (Position)
	 * @param directionX (Direction)
	 * @param directionY (Direction)
	 */
	public Position getNeighbour(Position cellPosition, Direction directionX, Direction directionY) {
		return labyrinth.getNeighbour(cellPosition, directionX, directionY);
	}
	
	/**
	 * {@link model.Player#getSprite()}
	 */
	public Image getSprite() {
		return labyrinth.getPlayer().getSprite();
	}
	
	/**
	 * {@link model.Labyrinth#getEndPosition()}
	 */
	public Position getEndPosition() {
		return labyrinth.getEndPosition();
	}
	
	/**
	 * Move the player according to the key
	 * @param key (KeyCode) The key pressed
	 * @return (boolean) true if the player moved successfully, false otherwise
	 */
	public boolean movePlayer(KeyCode key) {
		if(this.isAutoPlayer() || !this.labyrinth.isGenerationFinished()) return true;
		boolean moved = false;
		
		if(!labyrinth.getPlayer().goalAchieved()) {
			switch(key) {
				case UP:
					labyrinth.getPlayer().moveTo(Direction.NORTH);
					moved = true;
					break;
				case DOWN:
					labyrinth.getPlayer().moveTo(Direction.SOUTH);
					moved = true;
					break;
				case RIGHT:
					labyrinth.getPlayer().moveTo(Direction.EAST);
					moved = true;
					break;
				case LEFT:
					labyrinth.getPlayer().moveTo(Direction.WEST);
					moved = true;
					break;
				case Z:
					labyrinth.getPlayer().moveTo(Direction.NORTH);
					moved = true;
					break;
				case Q:
					labyrinth.getPlayer().moveTo(Direction.WEST);
					moved = true;
					break;
				case S:
					labyrinth.getPlayer().moveTo(Direction.SOUTH);
					moved = true;
					break;
				case D:
					labyrinth.getPlayer().moveTo(Direction.EAST);
					moved = true;
					break;
				default:
					break;
			}
		}
		
		if(this.threadCheckBlocked != null) {
			try {
				threadCheckBlocked.join(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		this.threadCheckBlocked = new Thread(() -> {
			this.labyrinth.getPlayer().checkBlocked();
		});
		
		this.threadCheckBlocked.start();
		
		view.update(moved);
		return moved;
	}
	
	/**
	 * Move the player according to the direction
	 * @param direction (Direction) The direction
	 * @return (boolean) true if the player moved successfully, false otherwise
	 */
	public boolean movePlayer(Direction direction) {
		boolean moved = false;
		if(!labyrinth.getPlayer().goalAchieved()) moved = labyrinth.getPlayer().moveTo(direction);
		this.labyrinth.getPlayer().checkBlocked();
		view.update(moved);
		return moved;
	}
	
	/**
	 * {@link model.Labyrinth#isAutoPlayer()}
	 */
	public boolean isAutoPlayer() {
		return labyrinth.isAutoPlayer();
	}
	
	/**
	 * {@link model.Labyrinth#setAutoPlayer(boolean autoPlayer)}
	 * @param autoPlayer (boolean)
	 */
	public void setAutoPlayer(boolean autoPlayer) {
		labyrinth.setAutoPlayer(autoPlayer);
	}
	
	/**
	 * {@link model.Labyrinth#isAutoPlayerEnabled()}
	 */
	public boolean isAutoPlayerEnabled() {
		return labyrinth.isAutoPlayerEnabled();
	}
	
	/**
	 * {@link model.Player#getPathAI()}
	 */
	public Queue<Position> getPathAI() {
		return labyrinth.getPlayer().getPathAI();
	}
	
	/**
	 * {@link model.Player#goalAchieved()}
	 */
	public boolean isGoalAchieved() {
		return labyrinth.getPlayer().goalAchieved();
	}
	
	/**
	 * {@link model.Player#isBlocked()}
	 */
	public boolean isPlayerBlocked() {
		return labyrinth.getPlayer().isBlocked();
	}
	
	/**
	 * Display the labyrinth in the console
	 */
	public void displayTextLabyrinth() {
		System.out.println(labyrinth);
	}

	/**
	 * {@link model.Labyrinth#generate(long seed, boolean stepByStep)}
	 * @param seed (long)
	 * @param stepByStep (boolean)
	 */
	public void generateLabyrinth(long seed, boolean stepByStep) {
		labyrinth.generate(seed, stepByStep);
	}
	
	public boolean searchingPath() {
		return labyrinth.getPlayer().isSearchingPath();
	}
}