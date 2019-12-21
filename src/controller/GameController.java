package controller;

import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import model.Cell;
import model.CellValue;
import model.Labyrinth;
import model.util.Direction;
import model.util.Position;
import view.GameView;

/**
 * This class represents the controller according to the MVC pattern
 * @author Eliastik
 * @version 1.1
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
	private Timer timerAuto;
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
	public String getSprite() {
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
	 * @param key (String) The key pressed
	 * @return (boolean) true if the player moved successfully, false otherwise
	 */
	public boolean movePlayer(String key) {
		switch(key) {
			case "UP":
				return this.movePlayer(Direction.NORTH);
			case "DOWN":
				return this.movePlayer(Direction.SOUTH);
			case "RIGHT":
				return this.movePlayer(Direction.EAST);
			case "LEFT":
				return this.movePlayer(Direction.WEST);
			case "Z":
				return this.movePlayer(Direction.NORTH);
			case "Q":
				return this.movePlayer(Direction.WEST);
			case "S":
				return this.movePlayer(Direction.SOUTH);
			case "D":
				return this.movePlayer(Direction.EAST);
			default:
				break;
		}
		
		return false;
	}
	
	/**
	 * Move the player according to the direction
	 * @param direction (Direction) The direction
	 * @return (boolean) true if the player moved successfully, false otherwise
	 */
	public boolean movePlayer(Direction direction) {
		if(this.isAutoPlayer() || !this.labyrinth.isGenerationFinished() || (!labyrinth.getPlayer().goalAchieved() && this.moveTo(direction))) {
			return true;
		}
		
		return false;
	}
	
	private boolean moveTo(Direction direction) {
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
			
				this.timerAuto = new Timer(true);
				
				this.timerAuto.scheduleAtFixedRate(new TimerTask() {
					@Override
					public void run() {
						if(!view.isExited()) {
							if(!isGoalAchieved() && pathAuto != null && !pathAuto.isEmpty()) {
								Position next = pathAuto.poll();
								Position current = getPlayerPosition();
								
								if(next != null) {
									if(next.getX() == current.getX() - 1) {
										moveTo(Direction.WEST);
									} else if(next.getX() == current.getX() + 1) {
										moveTo(Direction.EAST);
									} else if(next.getY() == current.getY() - 1) {
										moveTo(Direction.NORTH);
									} else if(next.getY() == current.getY() + 1) {
										moveTo(Direction.SOUTH);
									}
								} else {
									stopAutoPlayer();
								}
							}
						} else {
							stopAutoPlayer();
						}
					}
				}, 0, 250);
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
		
		if(this.timerAuto != null) {
			this.timerAuto.cancel();
			this.timerAuto.purge();
		}
		
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