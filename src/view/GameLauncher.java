package view;

import java.util.Locale;
import java.util.ResourceBundle;

import controller.GameController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.GenerationAlgorithmStrategy;
import model.Labyrinth;
import model.algorithms.AldousBroder;
import model.algorithms.BinaryTree;
import model.algorithms.GrowingTree;
import model.algorithms.HuntAndKill;
import model.algorithms.RecursiveBacktracking;
import model.algorithms.SideWinder;

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
	private GenerationAlgorithmStrategy algorithm = new AldousBroder();
	private long seed = System.currentTimeMillis();
	private Image spritePlayer;
	private int level = 1;
	private boolean displayInfoStart = true;
	private boolean stepByStep = false;
	private Thread stepByStepThread;
	
	public GameLauncher(int gameMode) {
		this.gameMode = gameMode;
	}
	
	@Override
	public void start(Stage stage) throws Exception {
		ResourceBundle locales = ResourceBundle.getBundle("locales.gameLauncher", Locale.getDefault()); // Locale

		VBox root = new VBox();
		Scene scene = new Scene(root);
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
		algorithmsList.addAll("Growing Tree", "Binary Tree", "Side Winder", "Recursive Backtracking", "Aldous Broder", "Hunt and Kill");
		algorithms.setItems(algorithmsList);
		algorithms.getSelectionModel().select(0);
		hboxAlgorithm.getChildren().addAll(algorithms);
		
		HBox hboxSeedLbl = new HBox();
		Label seedLbl = new Label(locales.getString("seed"));
		seedLbl.setFont(new Font(20));
		HBox.setMargin(seedLbl, new Insets(5, 5, 5, 5));
		hboxSeedLbl.getChildren().addAll(seedLbl);
		
		HBox hboxStepByStep = new HBox();
		CheckBox checkStepByStep = new CheckBox();
		Label stepByStepLbl = new Label(locales.getString("stepByStep"));
		HBox.setMargin(checkStepByStep, new Insets(5, 5, 5, 5));
		HBox.setMargin(stepByStepLbl, new Insets(5, 5, 5, 5));
		hboxStepByStep.getChildren().addAll(checkStepByStep, stepByStepLbl);
		
		stepByStepLbl.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if(checkStepByStep.isSelected()) {
				checkStepByStep.setSelected(false);
			} else {
				checkStepByStep.setSelected(true);
			}
		});
		
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
			
			if(checkStepByStep.isSelected()) {
				this.stepByStep = true;
				this.displayInfoStart = false;
			}
			
			switch(algorithms.getValue()) {
				case "Aldous Broder":
					this.algorithm = new AldousBroder();
					break;
				case "Binary Tree":
					this.algorithm = new BinaryTree();
					break;
				case "Side Winder":
					this.algorithm = new SideWinder();
					break;
				case "Recursive Backtracking":
					this.algorithm = new RecursiveBacktracking();
					break;
				case "Growing Tree":
					this.algorithm = new GrowingTree();
					break;
				case "Hunt and Kill":
					this.algorithm = new HuntAndKill();
					break;
				default:
					this.algorithm = new GrowingTree();
					break;
			}
			
			this.launchGame();
			stage.close();
		});
		
		root.getChildren().addAll(labelTitle, hboxSizeLbl, hboxSize, hboxAlgorithmLbl, hboxAlgorithm, hboxStepByStep, hboxSeedLbl, hboxSeed, hboxButtons);
		
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
			game = new GameGraphicalView(this, displayInfoStart, 0);
		} else {
			labyrinth = new Labyrinth(this.width, this.height, this.algorithm, false);
			game = new GameGraphicalView(this, displayInfoStart, this.level);
		}
		
		if(this.spritePlayer == null) {
			this.spritePlayer = labyrinth.getPlayer().getSprite();
		} else {
			labyrinth.getPlayer().setSprite(this.spritePlayer);
		}
		
		if(stepByStep) {
			this.stepByStepThread = new Thread(() -> {
				labyrinth.generate(this.seed, true);
			});
			
			this.stepByStepThread.start();
		} else {
			labyrinth.generate(this.seed, false);
		}
		
		game.setController(new GameController(labyrinth, game));
		game.run();
	}
	
	public void retry() {
		stopStepByStep();
		this.displayInfoStart = false;
		if(this.gameMode == 1) this.seed++;
		this.launchGame();
	}
	
	public void exit() {
		stopStepByStep();
		// TODO: highscore ladder
	}
	
	public void progress() {
		stopStepByStep();
		this.displayInfoStart = false;
		
		if(this.gameMode == 1) {
			this.width++;
			this.height++;
			this.seed++;
			this.level++;
			this.launchGame();
		}
	}
	
	public void stopStepByStep() {
		if(this.stepByStepThread != null) {
			try {
				this.stepByStepThread.join(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}