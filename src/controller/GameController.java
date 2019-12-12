package controller;

import java.util.Queue;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import model.Cell;
import model.CellValue;
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
	 * @param position ({@link Position})
	 */
	public Cell getCell(Position position) {
		return (position == null) ? null : labyrinth.getCell(position);
	}
	
	/**
	 * {@link model.Labyrinth#getNeighbour(Position cellPosition, Direction directionX, Direction directionY)}
	 * @param cellPosition ({@link Position})
	 * @param directionX ({@link Direction})
	 * @param directionY ({@link Direction})
	 */
	public Position getNeighbour(Position cellPosition, Direction directionX, Direction directionY) {
		return labyrinth.getNeighbour(cellPosition, directionX, directionY);
	}
	
	/**
	 * {@link model.Labyrinth#getNeighbour(Position cellPosition, Position neighbourPosition)}
	 * @param cellPosition ({@link Position})
	 * @param neighbourPosition ({@link Position})
	 * @return ({@link Direction})
	 */
	public Direction getNeighbour(Position cellPosition, Position neighbourPosition) {
		return labyrinth.getNeighbour(cellPosition, neighbourPosition);
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
		
		view.update(moved);
		this.createThreadCheckBlocked();
		
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
		view.update(moved);
		this.createThreadCheckBlocked();
		return moved;
	}
	
	private void createThreadCheckBlocked() {
		this.stopThreadCheckBlocked();
		
		this.threadCheckBlocked = new Thread(() -> {
			this.labyrinth.getPlayer().checkBlocked();
		});
		
		this.threadCheckBlocked.setDaemon(true);
		this.threadCheckBlocked.start();
	}
	
	private void stopThreadCheckBlocked() {
		if(this.threadCheckBlocked != null) {
			try {
				this.labyrinth.getPlayer().stopCheckBlocked();
				threadCheckBlocked.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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
	
	/**
	 * {@link model.Player#isSearchingPath()}
	 */
	public boolean searchingPath() {
		return labyrinth.getPlayer().isSearchingPath();
	}
	
	/**
	 * Exit properly the controller (end threads, etc.)
	 */
	public void exit() {
		this.stopThreadCheckBlocked();
	}
	
	/**
	 * {@link model.Labyrinth#getCellAround(Position pos)}
	 * @param pos ({@link Position}) The position of the cell
	 */
	public CellValue[] getCellAround(Position pos) {
		return this.labyrinth.getCellAround(pos);
	}
}