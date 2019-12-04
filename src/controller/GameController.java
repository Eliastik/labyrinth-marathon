package controller;

import java.util.Queue;

import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import model.Cell;
import model.Labyrinth;
import util.Direction;
import util.Position;
import view.GameView;

public class GameController {
	private Labyrinth labyrinth;
	private GameView view;
	
	public GameController(Labyrinth labyrinth, GameView view) {
		super();
		this.labyrinth = labyrinth;
		this.view = view;
	}
	
	public int getLabyrinthWidth() {
		return labyrinth.getWidth();
	}
	
	public int getLabyrinthHeight() {
		return labyrinth.getHeight();
	}
	
	public Position getPlayerPosition() {
		return labyrinth.getPlayer().getPosition();
	}
	
	public Direction getPlayerDirection() {
		return labyrinth.getPlayer().getDirection();
	}
	
	public Cell getCell(Position position) {
		return labyrinth.getCell(position);
	}
	
	public Position getNeighbour(Position cellPosition, Direction directionX, Direction directionY) {
		return labyrinth.getNeighbour(cellPosition, directionX, directionY);
	}
	
	public Image getSprite() {
		return labyrinth.getPlayer().getSprite();
	}
	
	public Position getEndPosition() {
		return labyrinth.getEndPosition();
	}
	
	public boolean movePlayer(KeyCode key) {
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
		return moved;
	}
	
	public boolean movePlayer(Direction direction) {
		boolean moved = false;
		if(!labyrinth.getPlayer().goalAchieved()) moved = labyrinth.getPlayer().moveTo(direction);
		view.update(moved);
		return moved;
	}
	
	public boolean isAutoPlayer() {
		return labyrinth.isAutoPlayer();
	}
	
	public void setAutoPlayer(boolean autoPlayer) {
		labyrinth.setAutoPlayer(autoPlayer);
	}
	
	public boolean isAutoPlayerEnabled() {
		return labyrinth.isAutoPlayerEnabled();
	}
	
	public Queue<Position> getPathAI() {
		return labyrinth.getPlayer().getPathAI();
	}
	
	public boolean isGoalAchieved() {
		return labyrinth.getPlayer().goalAchieved();
	}
	
	public boolean isPlayerBlocked() {
		return labyrinth.getPlayer().isBlocked();
	}
	
	public void displayTextLabyrinth() {
		System.out.println(labyrinth);
	}
}