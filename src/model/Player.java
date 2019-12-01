package model;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import javafx.scene.image.Image;
import util.Direction;
import util.Position;

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
	 * @param direction (Direction) the direction where to move the player
	 * @return (boolean) true if the player could have moved, false otherwise
	 */
	public boolean moveTo(Direction direction) {
		boolean peuxSeDeplacer = this.labyrinth.canMoveTo(this.labyrinth.getCase(this.getPosition()), this.labyrinth.getCase(this.labyrinth.getNeighbour(this.getPosition(), direction, direction)), direction);
		
		if(peuxSeDeplacer) {
			this.labyrinth.getCase(this.getPosition()).setValue(CellValue.CROSSED);
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
	 * Uses a flood fill algorithm (see https://en.wikipedia.org/wiki/Flood_fill)
	 * @return (boolean) true if the player is blocked, false otherwise
	 */
	public boolean isBlocked() {
		Stack<Position> checkList = new Stack<>();
		checkList.add(this.labyrinth.getEndPosition());
		List<Position> complete = new ArrayList<>();
		
		while(!checkList.isEmpty()) {
		      Position currentPosition = checkList.pop();
		      Direction[] directions = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
	
		      for(int i = 0; i < directions.length; i++) {
		    	  Position pos = this.labyrinth.getNeighbour(currentPosition, directions[i], directions[i]);
		    	  Cell c = this.labyrinth.getCase(pos);
		    	  
		    	  if(!checkList.contains(pos) && !complete.contains(pos) && this.labyrinth.canMoveTo(this.labyrinth.getCase(currentPosition), c, directions[i])) {
				        if((c.getValue() == CellValue.EMPTY || pos.equals(this.getPosition()))) {
					        checkList.add(pos);
					        
					        if(pos.equals(this.getPosition())) {
					        	return false;
					        }
				        }
		    	  }
		      }
		      
		      complete.add(currentPosition);
	    }
	    
	    return true;
	}
	
	/**
	 * Calculate a path to the exit and return the next position where to move.
	 * @return (Position) the next position where to move
	 */
	public Position getNextDirectionIA() {
		if(this.getPosition().equals(this.labyrinth.getEndPosition())) return null;
		
		Queue<List<Position>> queue = new LinkedList<>();
		List<Position> explore = new ArrayList<>();
		List<Position> pathToEnd = new ArrayList<>();
		
		pathToEnd.add(this.getPosition());
		queue.add(pathToEnd);
		
		while(!queue.isEmpty()) {
			pathToEnd = queue.poll();
			Position current = pathToEnd.get(pathToEnd.size() - 1);
			
			if(current.equals(this.labyrinth.getEndPosition())) {
				return pathToEnd.get(1);
			} else {
				ArrayList<Position> neightbours = new ArrayList<>();
				List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST));
				
				for(int i = 0; i < directions.size(); i++) {
					Position posNeighbour = this.labyrinth.getNeighbour(current, directions.get(i), directions.get(i));
					Cell neighbour = this.labyrinth.getCase(posNeighbour);
					
					if(!explore.contains(posNeighbour) && !posNeighbour.equals(current) && this.labyrinth.canMoveTo(this.labyrinth.getCase(current), neighbour, directions.get(i))) {
						List<Position> path = new ArrayList<>(pathToEnd);
						path.add(posNeighbour);
						queue.add(path);
						
						neightbours.add(posNeighbour);
						explore.add(posNeighbour);
					}
				}
				
				if(neightbours.isEmpty() && queue.isEmpty()) {
					return null;
				}
			}
        }
		
		return null;
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Labyrinth getLabyrinth() {
		return labyrinth;
	}

	public void setLabyrinth(Labyrinth labyrinth) {
		this.labyrinth = labyrinth;
	}

	public Image getSprite() {
		return sprite;
	}

	public void setSprite(Image sprite) {
		this.sprite = sprite;
	}
}