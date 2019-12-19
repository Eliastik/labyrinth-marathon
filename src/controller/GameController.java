package controller;

import java.util.Queue;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
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
	private boolean useThreadedCheckBlocked = true;
	// Autoplayer
	private Queue<Position> pathAuto;
	private Timeline timelineAuto;
	private Thread threadAuto;
	
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
		
		if(this.useThreadedCheckBlocked) {
			this.createThreadCheckBlocked();
		} else {
			this.labyrinth.getPlayer().checkBlocked();
		}
		
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
		
		if(this.useThreadedCheckBlocked) {
			this.createThreadCheckBlocked();
		} else {
			this.labyrinth.getPlayer().checkBlocked();
		}
		
		view.update(moved);
		
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
	 * {@link model.Labyrinth#getPath()}
	 */
	public Queue<Position> getPath() {
		return labyrinth.getPath();
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
	 * {@link model.Labyrinth#isSearchingPath()}
	 */
	public boolean searchingPath() {
		return labyrinth.isSearchingPath();
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

	/**
	 * Inform if the blocked player check is using a {@link Thread}
	 * @return (boolean)
	 */
	public boolean isUseThreadedCheckBlocked() {
		return useThreadedCheckBlocked;
	}
	
	/**
	 * Set if the blocked player check must use a {@link Thread}<br />
	 * Defaults to true
	 * @param useThreadedCheckBlocked (boolean) true to use a {@link Thread}, false otherwise
	 */
	public void setUseThreadedCheckBlocked(boolean useThreadedCheckBlocked) {
		this.useThreadedCheckBlocked = useThreadedCheckBlocked;
	}
	
	/**
	 * {@link model.Labyrinth#isGenerationFinished()}
	 * @return (boolean)
	 */
	public boolean isGenerationFinished() {
		return this.labyrinth.isGenerationFinished();
	}
	
	/**
	 * {@link model.Labyrinth#getAllCellsAround()}
	 * @return (boolean)
	 */
	public CellValue[][][] getAllCellsAround() {
		return this.labyrinth.getAllCellsAround();
	}
	
	/**
	 * Start the auto-player mode (using chosen pathfinding/solving algorithm)<br />
	 * Fails if the generation of the labyrinth is still in process, if the auto-player is disabled for this labyrinth or if the player is blocked
	 */
	public void enableAutoPlayer() {
		if(!this.searchingPath() && !this.isPlayerBlocked() && this.isGenerationFinished() && this.isAutoPlayerEnabled() && this.threadAuto == null) {
			stopAutoPlayer();
			this.setAutoPlayer(true);
	
			this.threadAuto = new Thread(() -> {
				if(this.pathAuto == null) {
					this.pathAuto = this.getPath();
					if(this.pathAuto != null && !this.pathAuto.isEmpty()) this.pathAuto.poll();
				}
			
				this.timelineAuto = new Timeline(new KeyFrame(Duration.seconds(0.25), ev -> {
					if(!view.isExited()) {
						if(!this.isGoalAchieved() && this.pathAuto != null && !this.pathAuto.isEmpty()) {
							Position next = this.pathAuto.poll();
							Position current = this.getPlayerPosition();
							
							if(next != null) {
								if(next.getX() == current.getX() - 1) {
									this.movePlayer(Direction.WEST);
								} else if(next.getX() == current.getX() + 1) {
									this.movePlayer(Direction.EAST);
								} else if(next.getY() == current.getY() - 1) {
									this.movePlayer(Direction.NORTH);
								} else if(next.getY() == current.getY() + 1) {
									this.movePlayer(Direction.SOUTH);
								}
							} else {
								this.stopAutoPlayer();
							}
						}
					} else {
						this.stopAutoPlayer();
					}
				}));
				
				this.timelineAuto.setCycleCount(Animation.INDEFINITE);
				this.timelineAuto.play();
			});

			this.threadAuto.setDaemon(true);
			this.threadAuto.start();
		}
	}
	
	/**
	 * Stop the auto-player mode
	 */
	public void stopAutoPlayer() {
		this.setAutoPlayer(false);
		
		if(this.timelineAuto != null) this.timelineAuto.stop();
		
		if(this.threadAuto != null) {
			try {
				this.setAutoPlayer(false);
				this.threadAuto.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}