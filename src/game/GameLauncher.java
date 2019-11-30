package game;

import algorithms.AldousBroder;
import javafx.application.Application;
import javafx.stage.Stage;
import model.GenerationAlgorithm;

/**
 * The launcher with settings
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public class GameLauncher extends Application {
	public int gameMode = 1; // 1 = marathon mode / 2 = labyrinth mode
	public int width = 15;
	public int height = 15;
	public long seed = System.currentTimeMillis();
	public GenerationAlgorithm algorithm = new AldousBroder();
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
	}
}