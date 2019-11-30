package game;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * The game menu
 * @author Eliastik
 * @version 1.0
 * @since 30/11/2019
 */
public class MenuLauncher extends Application {
	@Override
	public void start(Stage stage) throws Exception {
			ResourceBundle locales = ResourceBundle.getBundle("locales.menu", Locale.getDefault()); // Locale
		
			BorderPane root = new BorderPane();
			VBox vbox = new VBox();
			Scene scene = new Scene(root, 450, 450);
			scene.getStylesheets().add("/styles/style.css");
			
			MenuBar menuB = new MenuBar();
			Menu fileBar = new Menu(locales.getString("file"));
			Menu helpBar = new Menu(locales.getString("help"));
			MenuItem exitItem = new MenuItem(locales.getString("exit"));
			MenuItem helpItem = new MenuItem(locales.getString("help"));
			
			menuB.getMenus().addAll(fileBar, helpBar);
			fileBar.getItems().addAll(exitItem);
			helpBar.getItems().addAll(helpItem);
			
			exitItem.setOnAction(e -> {
				stage.close();
				System.exit(0);
			});
			
			ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.png")));
			logo.setFitWidth(400);
			logo.setFitHeight(108);
			
			Button marathon = new Button(locales.getString("marathon"));
			Button labyrinth = new Button(locales.getString("labyrinth"));
			Button exit = new Button(locales.getString("exit"));
			
			vbox.getChildren().addAll(logo, marathon, labyrinth, exit);
			vbox.setAlignment(Pos.BASELINE_CENTER);
			
			exit.setOnAction(e -> {
				stage.close();
				System.exit(0);
			});
			
			HBox hbox = new HBox();
			Hyperlink update = new Hyperlink(locales.getString("update"));
			update.setFont(new Font(15));
			hbox.getChildren().addAll(update);
			
			root.setTop(menuB);
			root.setCenter(vbox);
			root.setBottom(hbox);
			
			VBox.setMargin(logo, new Insets(50, 3, 3, 3));
			VBox.setMargin(marathon, new Insets(5, 3, 3, 3));
			VBox.setMargin(labyrinth, new Insets(5, 3, 3, 3));
			VBox.setMargin(exit, new Insets(5, 3, 3, 3));
			
			stage.setTitle(locales.getString("title"));
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
			stage.setScene(scene);
			stage.show();
			stage.setResizable(false);
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}