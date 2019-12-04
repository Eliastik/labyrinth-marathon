package view;

import java.util.Locale;
import java.util.ResourceBundle;

import algorithms.AldousBroder;
import algorithms.BinaryTree;
import controller.GameController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.GenerationAlgorithm;
import model.Labyrinth;

/**
 * The launcher with settings
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public class GameLauncher extends Application {
	private int gameMode = 1; // 1 = marathon mode / 2 = labyrinth mode
	private int width = 15;
	private int height = 15;
	private GenerationAlgorithm algorithm = new AldousBroder();
	private long seed = System.currentTimeMillis();
	private Image spritePlayer;
	private int level = 1;
	private boolean displayInfoStart = true;
	
	public GameLauncher(int gameMode) {
		this.gameMode = gameMode;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		ResourceBundle locales = ResourceBundle.getBundle("locales.gameLauncher", Locale.getDefault()); // Locale

		VBox root = new VBox();
		Scene scene = new Scene(root, 450, 340);
		scene.getStylesheets().add("/styles/style.css");
		
		Label labelTitle = new Label(locales.getString("labelTitle"));
		labelTitle.setFont(new Font(30));
		VBox.setMargin(labelTitle, new Insets(5, 5, 5, 5));
		
		HBox hboxSizeLbl = new HBox();
		Label sizeLabel;
		
		if(this.gameMode == 2) {
			sizeLabel = new Label(locales.getString("size"));
		} else {
			sizeLabel = new Label(locales.getString("initialSize"));
		}
		
		sizeLabel.setFont(new Font(20));
		HBox.setMargin(sizeLabel, new Insets(5, 5, 5, 5));
		hboxSizeLbl.getChildren().addAll(sizeLabel);
		
		HBox hboxSize = new HBox();
		sizeLabel.setAlignment(Pos.CENTER);
		Spinner<Integer> spinnerWidth;
		Spinner<Integer> spinnerHeight;
		
		if(this.gameMode == 2) {
			spinnerWidth = this.createSpinner(2, 200, 15);
			spinnerHeight = this.createSpinner(2, 200, 15);
		} else {
			spinnerWidth = this.createSpinner(2, 200, 2);
			spinnerHeight = this.createSpinner(2, 200, 2);
		}
		
		HBox.setMargin(spinnerWidth, new Insets(5, 5, 5, 5));
		HBox.setMargin(spinnerHeight, new Insets(5, 5, 5, 5));
		Label times = new Label(" × ");
		times.setAlignment(Pos.CENTER);
		HBox.setMargin(times, new Insets(10, 5, 5, 5));
		hboxSize.getChildren().addAll(spinnerWidth, times, spinnerHeight);
		
		HBox hboxAlgorithmLbl = new HBox();
		Label algorithmLbl = new Label(locales.getString("algorithm"));
		algorithmLbl.setFont(new Font(20));
		HBox.setMargin(algorithmLbl, new Insets(5, 5, 5, 5));
		hboxAlgorithmLbl.getChildren().addAll(algorithmLbl);
		
		HBox hboxAlgorithm = new HBox();
		ComboBox<String> algorithms = new ComboBox<>();
		HBox.setMargin(algorithms, new Insets(5, 5, 5, 5));
		ObservableList<String> algorithmsList = FXCollections.observableArrayList();
		algorithmsList.addAll("Aldous Broder", "Binary Tree");
		algorithms.setItems(algorithmsList);
		algorithms.getSelectionModel().select(0);
		hboxAlgorithm.getChildren().addAll(algorithms);
		
		HBox hboxSeedLbl = new HBox();
		Label seedLbl = new Label(locales.getString("seed"));
		seedLbl.setFont(new Font(20));
		HBox.setMargin(seedLbl, new Insets(5, 5, 5, 5));
		hboxSeedLbl.getChildren().addAll(seedLbl);
		
		HBox hboxSeed = new HBox();
		TextField seedField = new TextField("" + this.seed);
		HBox.setMargin(seedField, new Insets(5, 5, 5, 5));
		hboxSeed.getChildren().addAll(seedField);
		
		HBox hboxButtons = new HBox();
		Button buttonValidate = new Button(locales.getString("validate"));
		HBox.setMargin(buttonValidate, new Insets(10, 5, 5, 5));
		Button buttonCancel = new Button(locales.getString("cancel"));
		HBox.setMargin(buttonCancel, new Insets(10, 5, 5, 5));
		hboxButtons.setAlignment(Pos.CENTER);
		hboxButtons.getChildren().addAll(buttonValidate, buttonCancel);
		
		buttonCancel.setOnAction(e -> {
			stage.close();
		});
		
		buttonValidate.setOnAction(e -> {
			this.width = spinnerWidth.getValue();
			this.height = spinnerHeight.getValue();
			
			try {
				this.seed = Long.parseLong(seedField.getText());
			} catch(Exception e1) {
				this.seed = System.currentTimeMillis();
			}
			
			switch(algorithms.getValue()) {
				case "Aldous Broder":
					this.algorithm = new AldousBroder();
					break;
				case "Binary Tree":
					this.algorithm = new BinaryTree();
					break;
				default:
					this.algorithm = new AldousBroder();
					break;
			}
			
			this.launchGame();
			stage.close();
		});
		
		root.getChildren().addAll(labelTitle, hboxSizeLbl, hboxSize, hboxAlgorithmLbl, hboxAlgorithm, hboxSeedLbl, hboxSeed, hboxButtons);
		
		stage.setTitle(locales.getString("title"));
		stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon_flat.png")));
		stage.setScene(scene);
		stage.show();
		stage.setResizable(false);
	}
	
	private Spinner<Integer> createSpinner(int minValue, int maxValue, int defaultValue) {
		Spinner<Integer> spinner = new Spinner<Integer>();
		spinner.setEditable(true);
		spinner.getEditor().setAlignment(Pos.CENTER);
		
		spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(minValue, maxValue, defaultValue));
		
		spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue) {
				spinner.increment(0);
			}
		});
		
		return spinner;
	}
	
	private void launchGame() {
		Labyrinth labyrinth;
		GameView game;
		
		if(this.gameMode == 2) {
			labyrinth = new Labyrinth(this.width, this.height, this.algorithm, true);
			
			if(this.spritePlayer == null) {
				this.spritePlayer = labyrinth.getPlayer().getSprite();
			} else {
				labyrinth.getPlayer().setSprite(this.spritePlayer);
			}
			
			labyrinth.generate(this.seed);
			game = new GameGraphicalMode(this, displayInfoStart, 0);
		} else {
			labyrinth = new Labyrinth(this.width, this.height, this.algorithm, false);
			
			if(this.spritePlayer == null) {
				this.spritePlayer = labyrinth.getPlayer().getSprite();
			} else {
				labyrinth.getPlayer().setSprite(this.spritePlayer);
			}
			
			labyrinth.generate(this.seed);
			game = new GameGraphicalMode(this, displayInfoStart, this.level);
		}
		
		game.setController(new GameController(labyrinth, game));
		game.run();
	}
	
	public void retry() {
		this.displayInfoStart = false;
		if(this.gameMode == 1) this.seed++;
		this.launchGame();
	}
	
	public void exit() {
		// TODO: highscore ladder
	}
	
	public void progress() {
		this.displayInfoStart = false;
		
		if(this.gameMode == 1) {
			this.width++;
			this.height++;
			this.seed++;
			this.level++;
			this.launchGame();
		}
	}
}