package view;


import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Scanner;

import model.Labyrinth;
import model.Player;
import util.Direction;

/**
 * The game (text mode)<br />
 * Only used for debug
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public class GameTextMode {
	public static void main(String[] args) {
		Labyrinth labyrinth = new Labyrinth(10, 10);
		Player player = labyrinth.getPlayer();
		labyrinth.generate(System.currentTimeMillis());
		ResourceBundle locales = ResourceBundle.getBundle("locales.text", Locale.getDefault()); // Locale
		
		String choice = "";
		boolean moveSucceeded = true;
		boolean validEntry = false;
		Scanner scanner = new Scanner(System.in);
		
		while(!player.goalAchieved() && !player.isBlocked()) {
			for(int i = 0; i < 25; i++) System.out.print("\n");
			
			System.out.println(labyrinth);
			System.out.println(MessageFormat.format(locales.getString("infos"), labyrinth.getEndPosition().getX() + " - " + labyrinth.getEndPosition().getY()));
			
			if(!moveSucceeded) {
				System.out.println(locales.getString("moveFailed"));
			}
			
			System.out.println(locales.getString("move"));
			
			do {
				validEntry = false;
				choice = scanner.nextLine().trim().toUpperCase();
				
				if(choice.equals("") || (choice.charAt(0) != 'H' && choice.charAt(0) != 'B' && choice.charAt(0) != 'G' && choice.charAt(0) != 'D')) {
					System.out.println(locales.getString("invalidChoice"));
				} else {
					validEntry = true;
				}
			} while(!validEntry);
			
			switch(choice.charAt(0)) {
				case 'H':
					moveSucceeded = player.moveTo(Direction.NORTH);
					break;
				case 'B':
					moveSucceeded = player.moveTo(Direction.SOUTH);
					break;
				case 'G':
					moveSucceeded = player.moveTo(Direction.WEST);
					break;
				case 'D':
					moveSucceeded = player.moveTo(Direction.EAST);
					break;
			}
		}
		
		for(int i = 0; i < 25; i++) System.out.print("\n");
		System.out.println(labyrinth);
		
		if(player.goalAchieved()) {
			System.out.println(locales.getString("exitFound"));
		} else if(player.isBlocked()) {
			System.out.println(locales.getString("blocked"));
		}
		
		scanner.close();
	}
}