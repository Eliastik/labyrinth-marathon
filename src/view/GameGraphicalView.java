package view;


import java.util.Locale;
import java.util.Queue;
import java.util.ResourceBundle;

import controller.GameController;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
import model.util.Direction;
import model.util.Position;

/**
 * Graphical game
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public class GameGraphicalView extends Application implements GameView {
	protected Stage stage;
	private Canvas canvas;
	private GameLauncher launcher;
	private GameController controller;
	private AnimationTimer timerDraw;
	private Thread threadDraw;
	private Timeline timelineAuto;
	private Thread threadAuto;
	private Timeline timelineWin;
	private Queue<Position> pathAuto;
	private int level = 0;
	private boolean playerMoved = false;
	private long prevTime = 0;
	private int frameAnimate = 0;
	private Position prevPosition = new Position(0, 0);
	private double moveOffset = 1.0;
	private int xMove = 0;
	private int yMove = 0;
	private boolean exited = false;
	private boolean displayInfoStart = true;
	protected double[] camera = new double[]{1.0, 0.0, 0.0}; // zoom / posX / posY
	private double precXDrag = -1.0;
	private double precYDrag = -1.0;
	private boolean autoCamera = false;
	
	/**
	 * Construct a new graphical game
	 * @param launcher (GameLauncher) the launcher
	 * @param displatInfoStart (boolean)
	 * @param level (int)
	 */
	public GameGraphicalView(GameLauncher launcher, boolean displayInfoStart, int level) {
		this.launcher = launcher;
		this.level = level;
		this.displayInfoStart = displayInfoStart;
	}
	
	/**
	 * Construct a new graphical game
	 */
	public GameGraphicalView() {
		Labyrinth labyrinth = new Labyrinth();
		labyrinth.generate(System.currentTimeMillis(), false);
		this.controller = new GameController(labyrinth, this);
	}

	public void setDisplayInfoStart(boolean displayInfoStart) {
		this.displayInfoStart = displayInfoStart;
	}

	public void setController(GameController controller) {
		this.controller = controller;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		this.exited = false;
		this.stage = primaryStage;
		this.stage.setMaximized(true);
		
		// Assets
		Image brick = new Image(getClass().getResourceAsStream("/images/brick.png"));
		Image crossed = new Image(getClass().getResourceAsStream("/images/crossed.png"));
		Image start = new Image(getClass().getResourceAsStream("/images/start.png"));
		Image background = new Image(getClass().getResourceAsStream("/images/back.png"));
		Image current = new Image(getClass().getResourceAsStream("/images/current.png"));
		Image frontier = new Image(getClass().getResourceAsStream("/images/frontier.png"));
		ResourceBundle locales = ResourceBundle.getBundle("locales.graphical", Locale.getDefault()); // Locale
		
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
		
		if(controller.isAutoPlayerEnabled()) {
			hbox.getChildren().addAll(levelLabel, region, solution, retry, quit);
		} else {
			hbox.getChildren().addAll(levelLabel, region, retry, quit);
		}
		
		root.setTop(hbox);
		
		retry.setOnAction(e -> {
			if(!controller.isGoalAchieved() || controller.isAutoPlayer()) {
				stop();
				if(this.launcher != null) this.launcher.retry();
			}
		});
		
		quit.setOnAction(e -> {
			if(!controller.isGoalAchieved() || controller.isAutoPlayer()) {
				stop();
				if(this.launcher != null) this.launcher.exit();
			}
		});
		
		stage.setOnCloseRequest(e -> {
			stop();
			if(this.launcher != null) this.launcher.exit();
		});
		
		solution.setOnAction(e -> {
			enableAutoPlayer();
		});
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add("/styles/style.css");
		
		this.stage.setTitle(locales.getString("title"));
		this.stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon_flat.png")));
		this.stage.setMinWidth(400);
		this.stage.setMinHeight(400);
		this.stage.setWidth(800);
		this.stage.setHeight(600);
		this.stage.setScene(scene);
		this.stage.show();
		
		Button resetCamera = new Button(locales.getString("resetCamera"));
		resetCamera.setLayoutX(15);
		resetCamera.setLayoutY(scene.getHeight() - 110);
		
		resetCamera.setOnAction(e -> {
			this.camera[0] = 1.0;
			this.camera[1] = 0.0;
			this.camera[2] = 0.0;
		});
		
		CheckBox checkboxAutoCamera = new CheckBox();
		Label labelAutoCamera = new Label(locales.getString("autoCamera"));
		labelAutoCamera.setFont(new Font(15));
		checkboxAutoCamera.setLayoutX(18);
		checkboxAutoCamera.setLayoutY(scene.getHeight() - 140);
		labelAutoCamera.setLayoutX(45);
		labelAutoCamera.setLayoutY(scene.getHeight() - 140);
		
		labelAutoCamera.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
			if(checkboxAutoCamera.isSelected()) {
				checkboxAutoCamera.setSelected(false);
			} else {
				checkboxAutoCamera.setSelected(true);
			}
		});
		
		checkboxAutoCamera.selectedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
				autoCamera = newValue;
			}
		});
		
		scene.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				resetCamera.setLayoutY(scene.getHeight() - 110);
				checkboxAutoCamera.setLayoutY(scene.getHeight() - 140);
				labelAutoCamera.setLayoutY(scene.getHeight() - 140);
			}
		});
		
		pane.getChildren().addAll(resetCamera, checkboxAutoCamera, labelAutoCamera);
		
		if(!controller.isAutoPlayer()) {
			scene.setOnKeyPressed(e -> {
				controller.movePlayer(e.getCode());
			});
		} else {
			enableAutoPlayer();
		}
    	
		this.threadDraw = new Thread(() -> {
			this.timerDraw = new AnimationTimer() {
				@Override
				public void handle(long time) {
					if(!exited) {
						draw(time, (time - prevTime), brick, crossed, start, background, current, frontier, locales);
						prevTime = time;
					} else {
						this.stop();
					}
				}
			};
			
			this.timerDraw.start();
		});
		
		this.threadDraw.start();
		
		// Events camera
		this.canvas.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				if(precXDrag == -1.0)
					precXDrag = e.getSceneX() - canvas.getLayoutX();
				if(precYDrag == -1.0) precYDrag = e.getSceneY() - canvas.getLayoutY();

				double deltaXDrag = (e.getSceneX() - canvas.getLayoutX() - precXDrag);
				double deltaYDrag = (e.getSceneY() - canvas.getLayoutY() - precYDrag);

				camera[1] += deltaXDrag;
				camera[2] += deltaYDrag;

				precXDrag = e.getSceneX() - canvas.getLayoutX();
				precYDrag = e.getSceneY() - canvas.getLayoutY();
			}
		});

		this.canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				canvas.setCursor(Cursor.MOVE);
			}
		});

		this.canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent e) {
				precXDrag = -1.0;
				precYDrag = -1.0;
				canvas.setCursor(Cursor.DEFAULT);
			}
		});

		this.canvas.addEventHandler(ScrollEvent.SCROLL, new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent e) {
				if(e.getDeltaY() < 0 && camera[0] - 0.25 >= 0.25) {
					camera[0] -= 0.25;
				} else if(e.getDeltaY() > 0 && camera[0] + 0.25 <= 50.0) {
					camera[0] += 0.25;
				}
			}
		});
	}
	
	public void autoCamera(double x, double y) {
		this.camera[1] = -((getSizeCase()[0] * x + getStartX()) - this.canvas.getWidth() / 2 + getSizeCase()[0]);
		this.camera[2] = -((getSizeCase()[1] * y + getStartY()) - this.canvas.getHeight() / 2 + getSizeCase()[1]);
	}
	
	public int[] getSizeCase() {
		int[] size = new int[2];
		size[0] = (int) ((this.canvas.getWidth() / ((controller.getLabyrinthWidth() * 2) + 1)) * this.camera[0]);
		size[1] = (int) ((this.canvas.getHeight() / ((controller.getLabyrinthHeight() * 2) + 1)) * this.camera[0]);
		size[1] = size[1] > size[0] ? size[0] : size[1];
		size[0] = size[0] > size[1] ? size[1] : size[0];
		return size;
	}
	
	public int getStartX() {
		return (int) (((this.canvas.getWidth() - getSizeCase()[0] * ((controller.getLabyrinthWidth() * 2) + 1)) / 2));
	}
	
	public int getStartY() {
		return (int) (((this.canvas.getHeight() - getSizeCase()[1] * ((controller.getLabyrinthHeight() * 2) + 1)) / 2));
	}
	
	public void draw(long time, long timeOffset, Image brick, Image crossed, Image start, Image background, Image current, Image frontier, ResourceBundle locales) {
		int widthCase = getSizeCase()[0];
		int heightCase = getSizeCase()[1];

		double offsetXPlayer = 0;
		double offsetYPlayer = 0;
		
		if(this.moveOffset >= 0.0 && this.moveOffset <= 1.0) {
			offsetXPlayer = (controller.getPlayerPosition().getX() - this.xMove);
			offsetYPlayer = (controller.getPlayerPosition().getY() - this.yMove);
			
			if(offsetXPlayer > 0) {
				offsetXPlayer = offsetXPlayer - this.moveOffset;
			} else if(offsetXPlayer < 0) {
				offsetXPlayer = offsetXPlayer + this.moveOffset;
			} else {
				offsetXPlayer = 0;
			}
			
			if(offsetYPlayer > 0) {
				offsetYPlayer = offsetYPlayer - this.moveOffset;
			} else if(offsetYPlayer < 0) {
				offsetYPlayer = offsetYPlayer + this.moveOffset;
			} else {
				offsetYPlayer = 0;
			}
		}
		
		if(this.autoCamera) {
			this.autoCamera((controller.getPlayerPosition().getX() * 2 - offsetXPlayer), (controller.getPlayerPosition().getY() * 2 - offsetYPlayer));
		}
		
		int widthGrid = widthCase * (controller.getLabyrinthWidth() * 2);
		int heightGrid = heightCase * (controller.getLabyrinthHeight() * 2);
		
		int startX = (int) (getStartX() + this.camera[1]);
		int startY = (int) (getStartY()  + this.camera[2]);
		
		GraphicsContext gc = this.canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
		
		for(int i = 0; i < heightGrid; i += background.getHeight()) {
			for(int j = 0; j < widthGrid; j += background.getWidth()) {
				double widthImage = background.getWidth();
				double heightImage = background.getHeight();
				
				if(j + widthImage >= widthGrid) {
					widthImage = widthGrid - j;
				}
				
				if(i + heightImage >= heightGrid) {
					heightImage = heightGrid - i;
				}
				
				gc.drawImage(background, 0, 0, widthImage, heightImage, j + startX, i + startY, widthImage, heightImage);
			}
		}
		
		for(int i = 0; i < controller.getLabyrinthHeight() * 2 + 1; i++) {
			for(int j = 0; j < controller.getLabyrinthWidth() * 2 + 1; j++) {
				if(j == controller.getLabyrinthWidth() * 2 || i == controller.getLabyrinthHeight() * 2) {
					gc.drawImage(brick, (double) widthCase * j + startX, heightCase * i + startY, widthCase, heightCase);
				} else {
					Position pos = new Position(j / 2, i / 2);
					Cell c = controller.getCell(pos);
					Position posWest = controller.getNeighbour(pos, Direction.WEST, Direction.WEST);
					Cell cWest = controller.getCell(posWest);
					Position posNorth = controller.getNeighbour(pos, Direction.NORTH, Direction.NORTH);
					Cell cNorth = controller.getCell(posNorth);
					
					if(i == 0 || j == 0 || ((i + 1) % 2 == 0 && j % 2 == 0 && cWest.getEast() == CellValue.WALL && c.getWest() == CellValue.WALL) || ((i % 2 == 0 && j % 2 == 0) || (i % 2 == 0 && cNorth.getSouth() == CellValue.WALL && c.getNorth() == CellValue.WALL))) {
						gc.drawImage(brick, (double) widthCase * j + startX, heightCase * i + startY, widthCase, heightCase);
					} else if((i + 1) % 2 == 0 && (j + 1) % 2 == 0) {
						if(pos.equals(controller.getPlayerPosition())) {
							int numImageY = 1;
							int numImageX = 1;
							
							switch(controller.getPlayerDirection()) {
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
							
							if(this.moveOffset >= 0.0 && this.moveOffset <= 1.0) {
								if(this.frameAnimate >= 0 && this.frameAnimate <= 30) {
									numImageX = 4;
									this.frameAnimate++;
								} else if(this.frameAnimate > 30 && this.frameAnimate <= 60) {
									numImageX = 2;
									this.frameAnimate++;
								} else {
									this.frameAnimate = 0;
								}
							}
							
							gc.drawImage(controller.getSprite(), 48 * (numImageX - 1), 56 * (numImageY - 1) + 8 * numImageY, 48, 56, (double) widthCase * j + startX - offsetXPlayer * widthCase, heightCase * i + startY - offsetYPlayer * heightCase, widthCase, heightCase);
						} else if(pos.equals(controller.getEndPosition())) {
							gc.drawImage(start, (double) widthCase * j + startX, heightCase * i + startY, widthCase, heightCase);
						} else {
							if(c.getValue() == CellValue.WALL) {
								gc.drawImage(brick, (double) widthCase * j + startX, heightCase * i + startY, widthCase, heightCase);
							}
							
							if(c.getValue() == CellValue.CROSSED) {
								gc.drawImage(crossed, (double) widthCase * j + startX, heightCase * i + startY, widthCase, heightCase);
							}
							
							if(c.getValue() == CellValue.CURRENT) {
								gc.drawImage(current, (double) widthCase * j + startX, heightCase * i + startY, widthCase, heightCase);
							}
							
							if(c.getValue() == CellValue.FRONTIER) {
								gc.drawImage(frontier, (double) widthCase * j + startX, heightCase * i + startY, widthCase, heightCase);
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
		
		if(controller.searchingPath()) {
			text.setText(locales.getString("searchingPath"));
		} else if(!playerMoved && displayInfoStart) {
			text.setText(locales.getString("infos"));
			text.setFont(new Font(30));
			gc.setFont(new Font(30));
		} else if(controller.isGoalAchieved()) {
			text.setText(locales.getString("exitFound"));
		} else if(controller.isPlayerBlocked()) {
			text.setText(locales.getString("blocked"));
		}
		
		if(!text.getText().trim().equals("")) {
			int widthText = (int) text.getLayoutBounds().getWidth();
			int heightText = (int) text.getLayoutBounds().getHeight();
			
			gc.setFill(Color.rgb(125, 125, 125, 0.65));
			gc.fillRoundRect((this.canvas.getWidth() - widthText * 1.25) / 2, (this.canvas.getHeight() - heightText * 1.25) / 2, widthText * 1.25, heightText * 1.25, 5, 5);
			
			gc.setFill(Color.WHITE);
			gc.fillText(text.getText(), (this.canvas.getWidth() - widthText) / 2, this.canvas.getHeight() / 2);
		}
		
		this.moveOffset += (((double) timeOffset / 1000000) / 750);
	}
	
	public void stopDraw() {
		if(this.timerDraw != null) this.timerDraw.stop();
		
		if(this.threadDraw != null) {
			try {
				this.threadDraw.join(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void enableAutoPlayer() {
		stopAutoPlayer();
		controller.setAutoPlayer(true);

		this.threadAuto = new Thread(() -> {
			if(this.pathAuto == null) {
				this.pathAuto = controller.getPathAI();
				if(this.pathAuto != null && !this.pathAuto.isEmpty()) this.pathAuto.poll();
			}
		
			this.timelineAuto = new Timeline(new KeyFrame(Duration.seconds(0.7), ev -> {
				if(!controller.isGoalAchieved() && this.pathAuto != null && !this.pathAuto.isEmpty()) {
					Position next = this.pathAuto.poll();
					Position current = controller.getPlayerPosition();
					
					if(next != null) {
						if(next.getX() == current.getX() - 1) {
							controller.movePlayer(Direction.WEST);
						} else if(next.getX() == current.getX() + 1) {
							controller.movePlayer(Direction.EAST);
						} else if(next.getY() == current.getY() - 1) {
							controller.movePlayer(Direction.NORTH);
						} else if(next.getY() == current.getY() + 1) {
							controller.movePlayer(Direction.SOUTH);
						}
					} else {
						this.timelineAuto.stop();
					}
				}
			}));
			
			this.timelineAuto.setCycleCount(Animation.INDEFINITE);
			this.timelineAuto.play();
		});
		
		this.threadAuto.start();
	}
	
	public void stopAutoPlayer() {
		controller.setAutoPlayer(false);
		
		if(this.timelineAuto != null) this.timelineAuto.stop();
		
		if(this.threadAuto != null) {
			try {
				this.threadAuto.join(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void update(boolean moveSucceeded) {
		if(controller.isGoalAchieved() && !controller.isAutoPlayer()) {
			if(timelineWin != null) timelineWin.stop();
			
			this.timelineWin = new Timeline(new KeyFrame(Duration.seconds(2), ev -> {
				this.exited = true;
				this.timelineWin.stop();
				this.timerDraw.stop();
				this.stage.close();
				if(this.launcher != null) this.launcher.progress();
			}));
			
			this.timelineWin.setDelay(new Duration(1));
			this.timelineWin.setCycleCount(1);
			this.timelineWin.play();
		}
		
		if(moveSucceeded) {
			this.playerMoved = true;
			this.moveOffset = 0.0;
			this.xMove = prevPosition.getX();
			this.yMove = prevPosition.getY();
			this.prevPosition = controller.getPlayerPosition();
		}
	}
	
	public void stopWin() {
		if(this.timelineWin != null) this.timelineWin.stop();
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
	
	public void stop() {
		this.exited = true;
		this.stopDraw();
		this.stopAutoPlayer();
		this.stopWin();
		this.stage.close();
	}

	public static void main(String[] args) {
		try {
			new GameGraphicalView().run();
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