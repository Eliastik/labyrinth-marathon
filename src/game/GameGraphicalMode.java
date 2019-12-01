package game;


import java.util.Locale;
import java.util.ResourceBundle;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Cell;
import model.CellValue;
import model.Labyrinth;
import model.Player;
import util.Direction;
import util.Position;

/**
 * Graphical game
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public class GameGraphicalMode extends Application {
	private Labyrinth labyrinth;
	private GameLauncher launcher;
	private int level = 0;
	private Canvas canvas;
	private boolean joueurMove = false;
	private boolean exited = false;
	private boolean win = false;
	private boolean displayInfoStart = true;
	private Timeline timelineWin;
	private Timeline timelineAuto;
	private AnimationTimer timerDraw;
	protected Stage stage;
	
	/**
	 * Construct a new graphical game
	 * @param labyrinth (Labyrinth) A generated labyrinth (run generate())
	 */
	public GameGraphicalMode(Labyrinth labyrinth, GameLauncher launcher, boolean displayInfoStart, int level) {
		this.labyrinth = labyrinth;
		this.launcher = launcher;
		this.level = level;
		this.displayInfoStart = displayInfoStart;
	}
	
	/**
	 * Construct a new graphical game
	 */
	public GameGraphicalMode() {
		Labyrinth labyrinth = new Labyrinth();
		labyrinth.generate(System.currentTimeMillis());
		this.labyrinth = labyrinth;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.exited = false;
		this.stage = primaryStage;
		this.stage.setMaximized(true);
		
		// Assets
		Image brick = new Image(getClass().getResourceAsStream("/images/brick.png"));
		Image visite = new Image(getClass().getResourceAsStream("/images/crossed.png"));
		Image debut = new Image(getClass().getResourceAsStream("/images/start.png"));
		Image fond = new Image(getClass().getResourceAsStream("/images/back.png"));
		ResourceBundle locales = ResourceBundle.getBundle("locales.graphical", Locale.getDefault()); // Locale
		
		Player player = this.labyrinth.getPlayer();
		
		BorderPane root = new BorderPane();
		CanvasPane pane = new CanvasPane(800, 600);
		this.canvas = pane.getCanvas();
		root.setCenter(pane);
		
		HBox hbox = new HBox();
		Label levelLabel = new Label();
		
		if(this.level > 0) {
			levelLabel.setText(locales.getString("level") + " " + locales.getString("num") + this.level);
		}
		
		levelLabel.setFont(new Font(35));
		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);
		hbox.setAlignment(Pos.CENTER_RIGHT);
		Button solution = new Button(locales.getString("viewSolution"));
		Button retry = new Button(locales.getString("retry"));
		Button quit = new Button(locales.getString("exit"));

		HBox.setMargin(levelLabel, new Insets(5, 5, 5, 5));
		HBox.setMargin(solution, new Insets(5, 5, 5, 5));
		HBox.setMargin(retry, new Insets(5, 5, 5, 5));
		HBox.setMargin(quit, new Insets(5, 5, 5, 5));
		
		if(labyrinth.isAutoPlayerEnabled()) {
			hbox.getChildren().addAll(levelLabel, region, solution, retry, quit);
		} else {
			hbox.getChildren().addAll(levelLabel, region, retry, quit);
		}
		
		root.setTop(hbox);
		
		retry.setOnAction(e -> {
			exited = true;
			timerDraw.stop();
			primaryStage.close();
			if(this.launcher != null) this.launcher.retry();
		});
		
		quit.setOnAction(e -> {
			exited = true;
			timerDraw.stop();
			primaryStage.close();
			if(this.launcher != null) this.launcher.exit();
		});
		
		solution.setOnAction(e -> {
			enableJoueurAuto(player);
			labyrinth.setAutoPlayer(true);
		});
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add("/styles/style.css");
		
		primaryStage.setTitle(locales.getString("title"));
		primaryStage.setMinWidth(400);
		primaryStage.setMinHeight(400);
		primaryStage.setWidth(800);
		primaryStage.setHeight(600);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setOnCloseRequest(e -> {
			exited = true;
			timerDraw.stop();
			primaryStage.close();
			if(this.launcher != null) this.launcher.exit();
		});
		
		if(!labyrinth.isAutoPlayer()) {
			scene.setOnKeyPressed(e -> {
				if(!exited && !labyrinth.isAutoPlayer() && !player.goalAchieved()) {
					switch(e.getCode()) {
						case UP:
							player.moveTo(Direction.NORTH);
							joueurMove = true;
							break;
						case DOWN:
							player.moveTo(Direction.SOUTH);
							joueurMove = true;
							break;
						case RIGHT:
							player.moveTo(Direction.EAST);
							joueurMove = true;
							break;
						case LEFT:
							player.moveTo(Direction.WEST);
							joueurMove = true;
							break;
						case Z:
							player.moveTo(Direction.NORTH);
							joueurMove = true;
							break;
						case Q:
							player.moveTo(Direction.WEST);
							joueurMove = true;
							break;
						case S:
							player.moveTo(Direction.SOUTH);
							joueurMove = true;
							break;
						case D:
							player.moveTo(Direction.EAST);
							joueurMove = true;
							break;
						default:
							break;
					}
					
					tick(player);
				}
			});
		} else {
			enableJoueurAuto(player);
		}
    	
    	this.timerDraw = new AnimationTimer() {
			@Override
			public void handle(long time) {
				if(!exited) {
					draw(labyrinth, player, brick, visite, debut, fond, locales);
				} else {
					this.stop();
				}
			}
		};
		
		this.timerDraw.start();
	}
	
	public void tick(Player joueur) {
		if(joueur.goalAchieved()) {
			if(timelineWin != null) timelineWin.stop();
			
			this.timelineWin = new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
				this.timelineWin.stop();
				exited = true;
				timerDraw.stop();
				stage.close();
				if(this.launcher != null) this.launcher.progress();
				this.win = true;
			}));
			
			this.timelineWin.setDelay(new Duration(1));
			this.timelineWin.setCycleCount(1);
			this.timelineWin.play();
		}
	}
	
	public void enableJoueurAuto(Player joueur) {
		if(this.timelineAuto != null) this.timelineAuto.stop();
		
		this.timelineAuto = new Timeline(new KeyFrame(Duration.seconds(0.25), ev -> {
			Position next = joueur.getNextDirectionIA();
			Position current = joueur.getPosition();
			
			if(next != null) {
				if(next.getX() == current.getX() - 1) {
					joueur.moveTo(Direction.WEST);
					joueurMove = true;
				} else if(next.getX() == current.getX() + 1) {
					joueur.moveTo(Direction.EAST);
					joueurMove = true;
				} else if(next.getY() == current.getY() - 1) {
					joueur.moveTo(Direction.NORTH);
					joueurMove = true;
				} else if(next.getY() == current.getY() + 1) {
					joueur.moveTo(Direction.SOUTH);
					joueurMove = true;
				}
			} else {
				this.timelineAuto.stop();
			}
		}));
		
		this.timelineAuto.setCycleCount(Animation.INDEFINITE);
		this.timelineAuto.play();
	}
	
	public void draw(Labyrinth labyrinthe, Player joueur, Image brick, Image visite, Image debut, Image fond, ResourceBundle locales) {
		int widthCase = (int) (this.canvas.getWidth() / ((labyrinthe.getWidth() * 2) + 1));
		int heightCase = (int) (this.canvas.getHeight() / ((labyrinthe.getHeight() * 2) + 1));
		heightCase = heightCase > widthCase ? widthCase : heightCase;
		widthCase = widthCase > heightCase ? heightCase : widthCase;
		
		int widthGrid = widthCase * (labyrinthe.getWidth() * 2);
		int heightGrid = heightCase * (labyrinthe.getHeight() * 2);
		
		int debutX = (int) ((this.canvas.getWidth() - widthCase * ((labyrinthe.getWidth() * 2) + 1)) / 2);
		int debutY = (int) ((this.canvas.getHeight() - heightCase * ((labyrinthe.getHeight() * 2) + 1)) / 2);
		
		GraphicsContext gc = this.canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
		
		for(int i = 0; i < heightGrid; i += fond.getHeight()) {
			for(int j = 0; j < widthGrid; j += fond.getWidth()) {
				double widthImage = fond.getWidth();
				double heightImage = fond.getHeight();
				
				if(j + widthImage >= widthGrid) {
					widthImage = widthGrid - j;
				}
				
				if(i + heightImage >= heightGrid) {
					heightImage = heightGrid - i;
				}
				
				gc.drawImage(fond, 0, 0, widthImage, heightImage, j + debutX, i + debutY, widthImage, heightImage);
			}
		}
		
		for(int i = 0; i < labyrinthe.getHeight() * 2 + 1; i++) {
			for(int j = 0; j < labyrinthe.getWidth() * 2 + 1; j++) {
				if(j == labyrinthe.getWidth() * 2 || i == labyrinthe.getHeight() * 2) {
					gc.drawImage(brick, (double) widthCase * j + debutX, heightCase * i + debutY, widthCase, heightCase);
				} else {
					Position pos = new Position(j / 2, i / 2);
					Cell c = labyrinthe.getCase(pos);
					Position posOuest = labyrinthe.getNeighbour(pos, Direction.WEST, Direction.WEST);
					Cell cOuest = labyrinthe.getCase(posOuest);
					Position posNord = labyrinthe.getNeighbour(pos, Direction.NORTH, Direction.NORTH);
					Cell cNord = labyrinthe.getCase(posNord);
					
					if(i == 0 || j == 0 || ((i + 1) % 2 == 0 && j % 2 == 0 && cOuest.getEast() == CellValue.WALL && c.getWest() == CellValue.WALL) || ((i % 2 == 0 && j % 2 == 0) || (i % 2 == 0 && cNord.getSouth() == CellValue.WALL && c.getNorth() == CellValue.WALL))) {
						gc.drawImage(brick, (double) widthCase * j + debutX, heightCase * i + debutY, widthCase, heightCase);
					} else if((i + 1) % 2 == 0 && (j + 1) % 2 == 0) {
						if(pos.equals(joueur.getPosition())) {
							int numImageY = 1;
							
							switch(joueur.getDirection()) {
								case NORTH:
									numImageY = 4;
									break;
								case SOUTH:
									numImageY = 1;
									break;
								case EAST:
									numImageY = 3;
									break;
								case WEST:
									numImageY = 2;
									break;
							}
							
							gc.drawImage(joueur.getSprite(), 1, 10 * (numImageY) + 55 * (numImageY - 1), 55, 55, (double) widthCase * j + debutX, heightCase * i + debutY, widthCase, heightCase);
						} else if(pos.equals(labyrinthe.getEndPosition())) {
							gc.drawImage(debut, (double) widthCase * j + debutX, heightCase * i + debutY, widthCase, heightCase);
						} else {
							if(c.getValue() == CellValue.WALL) {
								gc.drawImage(brick, (double) widthCase * j + debutX, heightCase * i + debutY, widthCase, heightCase);
							}
							
							if(c.getValue() == CellValue.CROSSED) {
								gc.drawImage(visite, (double) widthCase * j + debutX, heightCase * i + debutY, widthCase, heightCase);
							}
						}
					}
				}
			}
		}
		
		Text text = new Text("");
		text.setFont(new Font(45));
		gc.setFont(new Font(45));
		gc.setTextBaseline(VPos.CENTER);
		
		if(!joueurMove && displayInfoStart) {
			text.setText(locales.getString("infos"));
			text.setFont(new Font(30));
			gc.setFont(new Font(30));
		} else if(joueur.goalAchieved()) {
			text.setText(locales.getString("exitFound"));
		} else if(joueur.isBlocked()) {
			text.setText(locales.getString("blocked"));
		}
		
		if(!text.getText().trim().equals("")) {
			int longueurTexte = (int) text.getLayoutBounds().getWidth();
			int largeurTexte = (int) text.getLayoutBounds().getHeight();
			
			gc.setFill(Color.rgb(125, 125, 125, 0.85));
			gc.fillRoundRect((this.canvas.getWidth() - longueurTexte * 1.25) / 2, (this.canvas.getHeight() - largeurTexte * 1.25) / 2, longueurTexte * 1.25, largeurTexte * 1.25, 5, 5);
			
			gc.setFill(Color.WHITE);
			gc.fillText(text.getText(), (this.canvas.getWidth() - longueurTexte) / 2, this.canvas.getHeight() / 2);
		}
	}
	
	public void run() {
		try {
			new JFXPanel();
			
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					try {
						start(new Stage());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean isWin() {
		return win;
	}

	public void setDisplayInfoStart(boolean displayInfoStart) {
		this.displayInfoStart = displayInfoStart;
	}

	public static void main(String[] args) {
		try {
			new GameGraphicalMode().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static class CanvasPane extends Pane {
	    private final Canvas canvas;

	    public CanvasPane(double width, double height) {
	    	this.setWidth(width);
	    	this.setHeight(height);
	    	this.canvas = new Canvas(width, height);
	    	this.getChildren().addAll(this.canvas);
	    	
	    	this.widthProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					canvas.setWidth((double) newValue);
				}
	    	});
	    	
	    	this.heightProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
					canvas.setHeight((double) newValue);
				}
	    	});
	    }

		public Canvas getCanvas() {
			return canvas;
		}
	}
}