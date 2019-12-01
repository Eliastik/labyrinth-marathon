package game;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
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
			MenuItem aboutItem = new MenuItem(locales.getString("about"));
			
			menuB.getMenus().addAll(fileBar, helpBar);
			fileBar.getItems().addAll(exitItem);
			helpBar.getItems().addAll(aboutItem);
			
			exitItem.setOnAction(e -> {
				stage.close();
				System.exit(0);
			});
			
			aboutItem.setOnAction(e -> {
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle(locales.getString("about"));
				alert.setHeaderText(locales.getString("aboutApp"));
				alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/styleAlert.css").toExternalForm());
				ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/images/icon_flat.png")));
				icon.setFitHeight(64);
				icon.setFitWidth(64);
				alert.setGraphic(icon);
				
				VBox vboxAbout = new VBox();
				
				HBox hboxCopy = new HBox();
				Label labelCopy = new Label("© 2019 Eliastik (eliastiksofts.com)");
				hboxCopy.getChildren().add(labelCopy);
				
				HBox hboxLicense = new HBox();
				Label labelLicense = new Label(locales.getString("license"));
				hboxLicense.getChildren().add(labelLicense);
				
				HBox buttonsAbout = new HBox();
				buttonsAbout.setAlignment(Pos.CENTER);
				Button buttonWebsite = new Button(locales.getString("website"));
				Button buttonLicense = new Button(locales.getString("licenseButton"));
				Button buttonReadme = new Button(locales.getString("readme"));
				HBox.setMargin(buttonWebsite, new Insets(15, 5, 0, 5));
				HBox.setMargin(buttonLicense, new Insets(15, 5, 0, 5));
				HBox.setMargin(buttonReadme, new Insets(15, 5, 0, 5));
				buttonsAbout.getChildren().addAll(buttonWebsite, buttonLicense, buttonReadme);
				
				buttonWebsite.setOnAction(e2 -> {
					try {
						new ProcessBuilder("x-www-browser", "https://www.eliastiksofts.com").start();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				});
				
				buttonLicense.setOnAction(e2 -> {
					this.displayTextFileDialog(locales.getString("licenseButton"), "license.txt");
				});
				
				buttonReadme.setOnAction(e2 -> {
					this.displayTextFileDialog(locales.getString("readme"), "README.md");
				});
				
				vboxAbout.getChildren().addAll(hboxCopy, hboxLicense, buttonsAbout);
				
				alert.getDialogPane().setContent(vboxAbout);
				alert.show();
			});
			
			ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/logo.png")));
			logo.setFitWidth(400);
			logo.setFitHeight(108);
			
			Button marathon = new Button(locales.getString("marathon"));
			Button labyrinth = new Button(locales.getString("labyrinth"));
			Button exit = new Button(locales.getString("exit"));
			
			vbox.getChildren().addAll(logo, marathon, labyrinth, exit);
			vbox.setAlignment(Pos.BASELINE_CENTER);
			
			marathon.setOnAction(e -> {
				try {
					Stage s = new Stage();
					s.initOwner(stage);
					s.initModality(Modality.WINDOW_MODAL);
					new GameLauncher(1).start(s);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});
			
			labyrinth.setOnAction(e -> {
				try {
					Stage s = new Stage();
					s.initOwner(stage);
					s.initModality(Modality.WINDOW_MODAL);
					new GameLauncher(2).start(s);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			});
			
			exit.setOnAction(e -> {
				stage.close();
				System.exit(0);
			});
			
			HBox hbox = new HBox();
			Hyperlink update = new Hyperlink(locales.getString("update"));
			update.setFont(new Font(15));
			hbox.getChildren().addAll(update);
			
			update.setOnAction(e -> {
				UpdateChecker checker = new UpdateChecker();
				
				Alert alert = new Alert(Alert.AlertType.INFORMATION);
				alert.setTitle(locales.getString("updateManager"));
				alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/styleAlert.css").toExternalForm());
				
				try {
					if(checker.checkUpdate()) {
						alert.setHeaderText(locales.getString("updateAvailable"));
						VBox vboxAlert = new VBox();
						
						HBox hboxCurrentVersion = new HBox();
						Label currentVersionLabel = new Label(locales.getString("currentVersion") + " ");
						currentVersionLabel.setStyle("-fx-font-weight: bold");
						Label currentVersionNumber = new Label(checker.getCurrentVersion());
						hboxCurrentVersion.getChildren().addAll(currentVersionLabel, currentVersionNumber);
						
						HBox hboxNewVersion = new HBox();
						Label newVersionLabel = new Label(locales.getString("newVersion") + " ");
						newVersionLabel.setStyle("-fx-font-weight: bold");
						Label newVersionNumber = new Label(checker.getVersionUpdate());
						hboxNewVersion.getChildren().addAll(newVersionLabel, newVersionNumber);
						
						HBox hboxDateVersion = new HBox();
						Label dateVersionLabel = new Label(locales.getString("dateVersion") + " ");
						dateVersionLabel.setStyle("-fx-font-weight: bold");
						Label dateVersion = new Label(DateFormat.getDateInstance().format(checker.getDateUpdate()));
						hboxDateVersion.getChildren().addAll(dateVersionLabel, dateVersion);
						
						HBox hboxChangesLabel = new HBox();
						Label changesLabel = new Label(locales.getString("changesVersion") + " ");
						changesLabel.setStyle("-fx-font-weight: bold");
						hboxChangesLabel.getChildren().addAll(changesLabel);
						
						HBox hboxChangesTextarea = new HBox();
						TextArea textareaChanges = new TextArea(checker.getChangesUpdate());
						textareaChanges.setEditable(false);
						textareaChanges.setPrefWidth(450);
						HBox.setMargin(textareaChanges, new Insets(5, 0, 0, 0));
						hboxChangesTextarea.getChildren().addAll(textareaChanges);
						
						HBox hboxDowload = new HBox();
						Button downloadButton = new Button(locales.getString("downloadUpdate"));
						HBox.setMargin(downloadButton, new Insets(15, 0, 0, 0));
						hboxDowload.setAlignment(Pos.CENTER);
						hboxDowload.getChildren().addAll(downloadButton);
						
						vboxAlert.getChildren().addAll(hboxCurrentVersion, hboxNewVersion, hboxDateVersion, hboxChangesLabel, hboxChangesTextarea, hboxDowload);
						alert.getDialogPane().setContent(vboxAlert);
						alert.getButtonTypes().remove(ButtonType.OK);
						alert.getButtonTypes().add(ButtonType.CLOSE);
						
						downloadButton.setOnAction(e2 -> {
							try {
								new ProcessBuilder("x-www-browser", checker.getUrlUpdate()).start();
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						});
					} else {
						alert.setHeaderText(locales.getString("noUpdate"));
					}
				} catch (Exception e1) {
					alert.setAlertType(Alert.AlertType.ERROR);
					alert.setHeaderText(locales.getString("errorUpdate"));
					Label labelError = new Label(locales.getString("errorUpdateInfos"));
					alert.getDialogPane().setContent(labelError);
				}
				
				alert.show();
			});
			
			root.setTop(menuB);
			root.setCenter(vbox);
			root.setBottom(hbox);
			
			VBox.setMargin(logo, new Insets(50, 3, 3, 3));
			VBox.setMargin(marathon, new Insets(5, 3, 3, 3));
			VBox.setMargin(labyrinth, new Insets(5, 3, 3, 3));
			VBox.setMargin(exit, new Insets(5, 3, 3, 3));
			
			stage.setTitle(locales.getString("title"));
			stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon_flat.png")));
			stage.setScene(scene);
			stage.show();
			stage.setResizable(false);
	}
	
	public void displayTextFileDialog(String title, String file) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(title);
		alert.getDialogPane().getStylesheets().add(getClass().getResource("/styles/styleAlert.css").toExternalForm());
		TextArea text = new TextArea();
		text.setWrapText(true);
		text.setEditable(false);
		int caretPosition = text.caretPositionProperty().get();
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + file), Charset.forName("UTF-8")))) {
			String line = br.readLine();
			
			while (line != null) {
				text.appendText(line + "\n");
				line = br.readLine();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		text.positionCaret(caretPosition);
		
		alert.getDialogPane().setContent(text);
		alert.show();
	}

	public static void main(String[] args) {
		Application.launch(args);
	}
}