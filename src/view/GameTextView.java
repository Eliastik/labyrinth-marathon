package view;


import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import controller.GameController;
import model.Labyrinth;
import model.util.Direction;

/**
 * The game (text mode)<br />
 * Only used for debug
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public class GameTextView implements GameView {
	private GameLauncher launcher;
	private GameController controller;
	private int level = 0;
	private ResourceBundle locales = ResourceBundle.getBundle("locales.text", Locale.getDefault()); // Locale
	private Scanner scanner = new Scanner(System.in);
	
	/**
	 * Construct a new text game
	 * @param launcher (GameLauncher) the launcher
	 * @param displatInfoStart (boolean)
	 * @param level (int)
	 */
	public GameTextView(GameLauncher launcher, int level) {
		this.launcher = launcher;
		this.level = level;
	}
	
	/**
	 * Construct a new text game
	 */
	public GameTextView() {
		Labyrinth labyrinth = new Labyrinth();
		labyrinth.generate(System.currentTimeMillis(), false);
		this.controller = new GameController(labyrinth, this);
	}
	
	public void run() {
		this.controller.setUseThreadedCheckBlocked(false);
		this.update(true);
	}

	@Override
	public void update(boolean moveSucceeded) {
		String choice = "";
		boolean validEntry = false;
		
		for(int i = 0; i < 25; i++) System.out.print("\n");
		controller.displayTextLabyrinth();
		
		if(!controller.isGoalAchieved() && !controller.isPlayerBlocked()) {
			if(this.level > 0) System.out.println(locales.getString("level") + " " + locales.getString("num") + this.level);
			System.out.println(MessageFormat.format(locales.getString("infos"), controller.getEndPosition().getX() + " - " + controller.getEndPosition().getY()));
			
			if(controller.isGenerationFinished()) {
				if(!moveSucceeded) {
					System.out.println(locales.getString("moveFailed"));
				}
				
				System.out.println(locales.getString("move"));
				
				do {
					validEntry = false;
					
					if(scanner.hasNextLine()) {
						choice = scanner.nextLine().trim().toUpperCase();
					}
					
					if(choice.equals("") || (choice.charAt(0) != 'T' && choice.charAt(0) != 'B' && choice.charAt(0) != 'R' && choice.charAt(0) != 'L')) {
						System.out.println(locales.getString("invalidChoice"));
					} else {
						validEntry = true;
					}
				} while(!validEntry);
				
				switch(choice.charAt(0)) {
					case 'T':
						moveSucceeded = controller.movePlayer(Direction.NORTH);
						break;
					case 'B':
						moveSucceeded = controller.movePlayer(Direction.SOUTH);
						break;
					case 'L':
						moveSucceeded = controller.movePlayer(Direction.WEST);
						break;
					case 'R':
						moveSucceeded = controller.movePlayer(Direction.EAST);
						break;
				}
			}
		} else {
			if(controller.isGoalAchieved()) {
				System.out.println(locales.getString("exitFound"));
				if(this.launcher != null) this.launcher.progress();
			} else if(controller.isPlayerBlocked()) {
				System.out.println(locales.getString("blocked"));
				
				if(this.launcher != null) {
					System.out.println("\n" + locales.getString("retry"));
					
					do {
						validEntry = false;
						choice = scanner.nextLine().trim().toUpperCase();
						
						if(choice.equals("") || (choice.charAt(0) != 'Y' && choice.charAt(0) != 'N')) {
							System.out.println(locales.getString("invalidChoice"));
						} else {
							validEntry = true;
						}
					} while(!validEntry);
					
					if(choice.charAt(0) == 'Y') {
						if(this.launcher != null) this.launcher.retry();
					} else {
						if(this.launcher != null) this.launcher.exit();
					}
				}
			}
		}
	}

	@Override
	public void setController(GameController controller) {
		this.controller = controller;
	}
	
	public static void main(String[] args) {
		new GameTextView().run();
	}
}