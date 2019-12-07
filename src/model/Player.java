package model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import javafx.scene.image.Image;
import model.util.Direction;
import model.util.Position;

/**
 * The class representing the player
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public class Player {
	private Position position;
	private Direction direction;
	private Labyrinth labyrinth;
	private Image sprite;
	private boolean isAutoPlayer = false;
	private boolean blocked = false;
	private boolean searchingPath = false;
	private boolean checkBlocked = false;
	
	public Player(Position position, Direction direction, Labyrinth labyrinth) {
		this.labyrinth = labyrinth;
		this.position = position;
		this.direction = direction;
		this.setSprite(new Image(getClass().getResourceAsStream("/images/" + (1 + new Random().nextInt(25)) + ".png")));
	}
	
	public Player(Position position, Direction direction) {
		this(position, direction, new Labyrinth());
	}
	
	public Player(Position position) {
		this(position, Direction.SOUTH);
	}
	
	public Player() {
		this(new Position(0, 0), Direction.SOUTH);
	}
	
	/**
	 * Move the player to the given direction if possible
	 * @param direction ({@link Direction}) the direction where to move the player
	 * @return (boolean) true if the player could have moved, false otherwise
	 */
	public boolean moveTo(Direction direction) {
		boolean peuxSeDeplacer = this.labyrinth.canMoveTo(this.labyrinth.getCell(this.getPosition()), this.labyrinth.getCell(this.labyrinth.getNeighbour(this.getPosition(), direction, direction)), direction);
		
		if(peuxSeDeplacer) {
			this.labyrinth.getCell(this.getPosition()).setValue(CellValue.CROSSED);
			this.setPosition(labyrinth.getNeighbour(this.getPosition(), direction, direction));
		}
		
		this.setDirection(direction);
		
		return peuxSeDeplacer;
	}
	
	/**
	 * Inform if the player reached the exit point
	 * @return (boolean)
	 */
	public boolean goalAchieved() {
		return this.getPosition().equals(this.labyrinth.getEndPosition());
	}
	
	/**
	 * Detect if the player is blocked (if no path leads him to the exit)<br />
	 * Uses a flood fill algorithm
	 * @return (boolean) true if the player is blocked, false otherwise
	 * @see <a href="https://en.wikipedia.org/wiki/Flood_fill">https://en.wikipedia.org/wiki/Flood_fill</a>
	 */
	public boolean isBlocked() {
		if(!this.labyrinth.isGenerationFinished()) return false;
		if(this.blocked) return true;
		return false;
	}
	
	/**
	 * Detect if the player is blocked (if no path leads him to the exit)<br />
	 * Uses a flood fill algorithm
	 * @return (boolean) true if the player is blocked, false otherwise
	 * @see <a href="https://en.wikipedia.org/wiki/Flood_fill">https://en.wikipedia.org/wiki/Flood_fill</a>
	 */
	public void checkBlocked() {
		if(this.labyrinth.isGenerationFinished() && !this.blocked) {
			this.checkBlocked = true;
			
			Stack<Position> checkList = new Stack<>();
			checkList.add(this.labyrinth.getEndPosition());
			List<Position> complete = new ArrayList<>();
			
			while(!checkList.isEmpty()) {
		    	  if(this.blocked || !this.checkBlocked) return;
		    	  
			      Position currentPosition = checkList.pop();
			      Direction[] directions = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
		
			      for(int i = 0; i < directions.length; i++) {
			    	  if(this.blocked || !this.checkBlocked) return;
			    	  
			    	  Position pos = this.labyrinth.getNeighbour(currentPosition, directions[i], directions[i]);
			    	  Cell c = this.labyrinth.getCell(pos);
			    	  
			    	  if(!checkList.contains(pos) && !complete.contains(pos) && this.labyrinth.canMoveTo(this.labyrinth.getCell(currentPosition), c, directions[i])) {
					        if((c.getValue() == CellValue.EMPTY || pos.equals(this.getPosition()))) {
						        checkList.add(pos);
						        
						        if(pos.equals(this.getPosition())) {
						    		this.blocked = false;
						    		return;
						        }
					        }
			    	  }
			      }
			      
			      complete.add(currentPosition);
		    }
			
			this.blocked = true;
		}
	}
	
	/**
	 * Stop the previous launched {@link #checkBlocked()} method
	 */
	public void stopCheckBlocked() {
		this.checkBlocked = false;
	}
	
	/**
	 * Calculate a path to the exit and return this path.<br />
	 * Return null if no path was found.
	 * @return ({@link Queue}<{@link Position}>) The path
	 */
	public Queue<Position> getPathAI() {
		if(!this.labyrinth.isAutoPlayer()) return null;
		if(this.searchingPath) return null;
		if(this.getPosition().equals(this.labyrinth.getEndPosition())) return null;
		
		this.searchingPath = true;
		Queue<List<Position>> queue = new LinkedList<>();
		List<Position> explore = new ArrayList<>();
		List<Position> pathToEnd = new ArrayList<>();
		
		pathToEnd.add(this.getPosition());
		queue.add(pathToEnd);
		
		while(!queue.isEmpty()) {
			if(!this.labyrinth.isAutoPlayer()) return null;
			
			pathToEnd = queue.poll();
			Position current = pathToEnd.get(pathToEnd.size() - 1);
			
			if(current.equals(this.labyrinth.getEndPosition())) {
				this.searchingPath = false;
				return new LinkedList<>(pathToEnd);
			} else {
				ArrayList<Position> neightbours = new ArrayList<>();
				List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
				
				for(int i = 0; i < directions.size(); i++) {
					Position posNeighbour = this.labyrinth.getNeighbour(current, directions.get(i), directions.get(i));
					Cell neighbour = this.labyrinth.getCell(posNeighbour);
					
					if(posNeighbour != null && !explore.contains(posNeighbour) && !posNeighbour.equals(current) && this.labyrinth.canMoveTo(this.labyrinth.getCell(current), neighbour, directions.get(i))) {
						List<Position> path = new ArrayList<>(pathToEnd);
						path.add(posNeighbour);
						queue.add(path);
						
						neightbours.add(posNeighbour);
						explore.add(posNeighbour);
					}
				}
				
				if(neightbours.isEmpty() && queue.isEmpty()) {
					this.searchingPath = false;
					return null;
				}
			}
        }

		this.searchingPath = false;
		return null;
	}

	/**
	 * Get the current position of the player
	 * @return ({@link Position}) The current position of the player
	 */
	public Position getPosition() {
		return position;
	}

	/**
	 * Set the position of the player
	 * @param position ({@link Position}) The new position of the player
	 */
	public void setPosition(Position position) {
		this.position = position;
	}

	/**
	 * Get the current direction of the player
	 * @return ({@link Direction}) The current direction of the player
	 */
	public Direction getDirection() {
		return direction;
	}

	/**
	 * Set the direction of the player
	 * @param direction ({@link Direction}) The new direction of the player
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	/**
	 * Get the labyrinth where the player is
	 * @return ({@link Labyrinth}) The labyrinth
	 */
	public Labyrinth getLabyrinth() {
		return labyrinth;
	}

	/**
	 * Set the labyrinth where the player is
	 * @param labyrinth ({@link Labyrinth}) The new labyrinth
	 */
	public void setLabyrinth(Labyrinth labyrinth) {
		this.labyrinth = labyrinth;
	}

	/**
	 * Get the sprite of the player
	 * @return ({@link Image}) The sprite of the player
	 */
	public Image getSprite() {
		return sprite;
	}

	/**
	 * Set the sprite of the player
	 * @param sprite ({@link Image}) The new sprite of the player
	 */
	public void setSprite(Image sprite) {
		this.sprite = sprite;
	}

	/**
	 * Inform is the computer is still searching a path (when auto player is enabled)
	 * @return (boolean) true if the computer search a path, false otherwise
	 */
	public boolean isSearchingPath() {
		return searchingPath;
	}

	/**
	 * Get the auto player setting
	 * @return (boolean) true if the player move automatically according to the path to the end position, false otherwise
	 */
	public boolean isAutoPlayer() {
		return isAutoPlayer;
	}

	/**
	 * Set the auto player setting
	 * @param autoPlayer (boolean) true if the player must move automatically according to the path to the end position, false otherwise
	 */
	public void setAutoPlayer(boolean isAutoPlayer) {
		this.isAutoPlayer = isAutoPlayer;
	}
}