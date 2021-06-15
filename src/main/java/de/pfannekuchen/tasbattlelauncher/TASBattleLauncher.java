package de.pfannekuchen.tasbattlelauncher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;

import de.pfannekuchen.accountapi.MicrosoftAccount;
import de.pfannekuchen.accountapi.MojangAccount;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Entry Point for the TAS Battle Launcher (JavaFX Application)
 * @author Pancake
 */
public class TASBattleLauncher extends Application {

	public static final File accountsFile = new File("accounts");
	public static Object mcaccount;
	private static Label accountlabel;
	private static Stage stage;
	
	/**
	 * You cannot start JavaFX from the Class itself, so here is a static method to do that
	 */
	public static void start() {
		launch(); // Launch JavaFX in this Thread
	}
	
	/**
	 * Main of Gui
	 */
	@Override @SuppressWarnings("unchecked") public void start(Stage stage) throws Exception {
		/* Load FXML File and display it */
		FXMLLoader loader = new FXMLLoader(getClass().getResource("App.fxml"));
		stage.setScene(new Scene(loader.load()));
		stage.show();
		TASBattleLauncher.stage = stage;
		accountlabel = (Label) ((HBox) ((VBox) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(0)).getChildren().get(0)).getChildren().get(1);
		/* Load the Configuration File */
		ConfigUtils.init(new File("launcher.properties"));
		/* Thread for Loading an Account from the Accounts File */
		Thread accountLoader = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					/* Read Lines from File */
					accountsFile.createNewFile();
					List<String> lines = Files.readAllLines(accountsFile.toPath());
					if (lines.size() > 0) {
						/* Try to Log into the Account */
						try {
							String name;
							if (lines.get(0).equalsIgnoreCase("Microsoft Token")) {
								mcaccount = new MicrosoftAccount(lines.get(1));
								name = ((MicrosoftAccount) mcaccount).getUsername();
								// Write updated Stuff into the File
								PrintWriter writer = new PrintWriter(new FileOutputStream(accountsFile, false));
								writer.println("Microsoft Token");
								writer.println(((MicrosoftAccount) mcaccount).getAccountToken());
								writer.close();
							} else {
								mcaccount = new MojangAccount(lines.get(1), UUID.fromString(lines.get(2)));
								name = ((MojangAccount) mcaccount).getUsername();
								// Write updated Stuff into the File
								PrintWriter writer = new PrintWriter(new FileOutputStream(accountsFile, false));
								writer.println("Mojang Access Token");
								writer.println(((MojangAccount) mcaccount).getAccessToken());
								writer.println(((MojangAccount) mcaccount).getClientUuid().toString());
								writer.close();
							}
							Platform.runLater(() -> {
								accountlabel.setText(name);
							});
						} catch (Exception e) {
							System.err.println("Could not load MC Account from File");
							e.printStackTrace();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
				
		accountLoader.setName("Account-Loader Thread");
		accountLoader.setDaemon(true);
		accountLoader.start();
		/* Load All Available Game Modes into the List */
		((ComboBox<String>) ((BorderPane) ((AnchorPane) ((ScrollPane) ((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(0)).getChildren().get(1)).getContent()).getChildren().get(1)).getLeft()).getItems().addAll("FabricTAS 1.16.1", "LoTAS 1.12.2", "LoTAS 1.11.2", "LoTAS 1.9.4", "LoTAS 1.8.9", "TASTools 1.7.10");
		// Not released yet
		//((ComboBox<String>) ((BorderPane) ((AnchorPane) ((ScrollPane) ((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(1)).getChildren().get(2)).getContent()).getChildren().get(1)).getLeft()).getItems().addAll("TASmod 1.12.2", "TASmod-OG 1.0");
		//((ComboBox<String>) ((BorderPane) ((AnchorPane) ((ScrollPane) ((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(2)).getChildren().get(2)).getContent()).getChildren().get(1)).getLeft()).getItems().addAll("FFA 1.12.2", "FFA 1.16.5", "FFA 1.8.9", "Bedwars 1.8.9", "Bedwars 1.12.2", "Bedwars 1.16.5", "Skywars 1.8.9", "Skywars 1.12.2", "Skywars 1.16.5", "Cores 1.12.2", "Cores 1.8.9", "Cores 1.16.5");
		((ComboBox<String>) ((BorderPane) ((AnchorPane) ((ScrollPane) ((VBox) ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren().get(3)).getChildren().get(1)).getContent()).getChildren().get(1)).getLeft()).getItems().addAll("TASTickrateChanger 1.16.5", "TASTickrateChanger 1.15.2", "TASTickrateChanger 1.14.4", "TASTickrateChanger 1.12.2", "TickrateChanger 1.12.2", "TASTools 1.12.2", "TASTools 1.11.2", "TASTools 1.10.2", "TASTools 1.9.4", "TASTools 1.8.9", "TASmod-OG 1.0", "TASmod 1.12.2");
	
		// Show "TAS Old/Beta" only when "Show Experimental" is enabled
		Platform.runLater(() -> {
			if (!ConfigUtils.getBoolean("root", "showexperimental")) ((VBox) ((HBox) stage.getScene().getRoot().getChildrenUnmodifiable().get(0)).getChildren().get(0)).getChildren().get(4).setVisible(false);
			((CheckBox) ((VBox) ((HBox) stage.getScene().getRoot().getChildrenUnmodifiable().get(0)).getChildren().get(0)).getChildren().get(5)).setSelected(ConfigUtils.getBoolean("root", "showexperimental"));
		});
	}
	
	/* ================================== Events for Right Side of Main Menu Pane ================================== */
	
	/**
	 * Automatically called by the Gui once you click on "Start Client"
	 * TODO: Start the Minecraft Client and or Set it up
	 */
	@FXML private void startClient() {
		
	}
	
	/**
	 * Automatically called by the Gui once a new Gamemode has been selected in the Drop Down Menu
	 * TODO: Update the Pane and request new Information from the Server
	 */
	@FXML private void switchGamemode() {

	}
	
	/* ================================== Events for Left Side of Main Menu Pane ================================== */
	
	/* Following Methods are automatically being called from the System whenever they select a new Category on the left. */
	/* Clicking them will show the correct menu */
	@FXML private HBox tas;
	@FXML private HBox tasplayback;
	@FXML private HBox tasbattle;
	@FXML private HBox tasoldbeta;
	@FXML private CheckBox experimentalCheckbox;
	
	@FXML private void openTASMenu()  { tas.getStyleClass().set(1, "selected"); tasplayback.getStyleClass().set(1, null); tasbattle.getStyleClass().set(1, null); tasoldbeta.getStyleClass().set(1, null); showPane(0); }
	@FXML private void openTASPlaybackMenu()  { tas.getStyleClass().set(1, null); tasplayback.getStyleClass().set(1, "selected"); tasbattle.getStyleClass().set(1, null); tasoldbeta.getStyleClass().set(1, null); showPane(1); }
	@FXML private void openTASBattleMenu()  { tas.getStyleClass().set(1, null); tasplayback.getStyleClass().set(1, null); tasbattle.getStyleClass().set(1, "selected"); tasoldbeta.getStyleClass().set(1, null); showPane(2); }
	@FXML private void openTASOldBetaMenu()  { tas.getStyleClass().set(1, null); tasplayback.getStyleClass().set(1, null); tasbattle.getStyleClass().set(1, null); tasoldbeta.getStyleClass().set(1, "selected"); showPane(3); }
	
	@FXML private void toggleExperimental() {
		ConfigUtils.setBoolean("root", "showexperimental", experimentalCheckbox.isSelected());
		ConfigUtils.save();
		((VBox) ((HBox) stage.getScene().getRoot().getChildrenUnmodifiable().get(0)).getChildren().get(0)).getChildren().get(4).setVisible(ConfigUtils.getBoolean("root", "showexperimental"));
		openTASMenu();
	}
	
	/**
	 * This Method is not automatically being called! 
	 * This Hides all Panes but keeps the indexed one
	 */
	public void showPane(int index) {
		ObservableList<Node> items = ((AnchorPane) ((HBox) ((AnchorPane) stage.getScene().getRoot()).getChildren().get(0)).getChildren().get(1)).getChildren();
		for (int i = 0; i < items.size(); i++)  items.get(i).setVisible(i == index);
	}
	
	/**
	 * Automatically called by the Gui once you click on "Login". Opens a menu where you can easily Log Into your Account
	 */
	@FXML private void openLoginDialog() throws IOException {
		loginStage = new Stage();
		loginStage.setScene(new Scene(new FXMLLoader(getClass().getResource("Login.fxml")).load()));
		loginStage.show();
	}
	
	/* ================================== Events for Login Pane ================================== */
	
	private static Stage loginStage;
	@FXML private ImageView microsoftbtn;
	@FXML private ImageView mojangbtn;
	@FXML private ImageView signinbtn;
	@FXML private Label errorlabel;
	@FXML private TextField textField;
	@FXML private PasswordField passwordField;
	
	/**
	 * Automatically called by the Gui once you click on "Mojang Account" in the Login Dialog Pane
	 */
	@FXML
	private void login_mojang() {
		textField.setVisible(true);
		passwordField.setVisible(true);
		microsoftbtn.setVisible(false);
		mojangbtn.setVisible(false);
		errorlabel.setVisible(false);
		signinbtn.setVisible(true);
	}
	
	/**
	 * Automatically called by the Gui once you click on "Sign-in" in the Login Dialog Pane
	 */
	@FXML private void login_signin() {
		Thread login_thread = new Thread(() -> {
			try {
				MojangAccount account = new MojangAccount(textField.getText(), passwordField.getText());
				TASBattleLauncher.mcaccount = account;
				/* Write Account to File */
				PrintWriter writer = new PrintWriter(new FileOutputStream(accountsFile, false));
				writer.println("Mojang Access Token");
				writer.println(account.getAccessToken());
				writer.println(account.getClientUuid().toString());
				writer.close();
				/* Close Dialog */
				Platform.runLater(() -> {
					accountlabel.setText(account.getUsername());
					loginStage.close();
				});
			} catch (Exception e) {
				Platform.runLater(() -> {
					errorlabel.setText("Error: Invalid Credentials");
					errorlabel.setVisible(true);
				});
				e.printStackTrace();
			}
		});
		login_thread.setDaemon(true);
		login_thread.setName("Login Thread");
		login_thread.start();
	}
	
	/**
	 * Automatically called by the Gui once you click on "Microsoft Login" in the Login Dialog Pane
	 */
	@FXML private void login_microsoft() {
		Thread login_thread = new Thread(() -> {
			try {
				MicrosoftAccount account = new MicrosoftAccount();
				if (!account.ownsMinecraft()) throw new Exception("Account does not own the Game");
				TASBattleLauncher.mcaccount = account;
				/* Write Account to File */
				PrintWriter writer = new PrintWriter(new FileOutputStream(accountsFile, false));
				writer.println("Microsoft Token");
				writer.println(account.getAccountToken());
				writer.close();
				/* Close Dialog */
				Platform.runLater(() -> {
					accountlabel.setText(account.getUsername());
					loginStage.close();
				});
			} catch (Exception e) {
				Platform.runLater(() -> {
					errorlabel.setText("Error: " + (e.getMessage().isEmpty() ? "Check Console." : e.getMessage()));
					errorlabel.setVisible(true);
				});
				e.printStackTrace();
			}
		});
		login_thread.setDaemon(true);
		login_thread.setName("Login Thread");
		login_thread.start();
	}
	
}