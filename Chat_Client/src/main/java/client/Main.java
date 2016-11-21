package client;

import client.main.LoginController;
import client.main.RegisterController;
import client.main.StartScreenController;
import client.model.User;
import client.preferences.Preferences;
import client.updater.Updater;
import client.utils.UtilDialogs;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Main extends Application {

    private static Stage stage;

    private boolean firstTime = true;
    private TrayIcon trayIcon;
    private SystemTray tray;

    public static void main(String[] args) {
        // Handle uncaught exceptions
        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> UtilDialogs.exception(e));
        // Getting saved properties from Preferences.xml
        if (Preferences.getPreferences() == null) {
            System.out.println("error read xml");
        }
        // Checking update
        Updater.startupCheckUpdate();
        // Start the application
        Application.launch(Main.class, (java.lang.String[]) null);
    }

    public static Stage getStage() {
        return stage;

    }

    @Override
    public void start(Stage primaryStage) {

        try {
            stage = primaryStage;
            stage.setTitle("Chat");
            stage.setMinHeight(400);
            stage.setMinWidth(400);

            stage.getIcons().add(new Image("images/icon.png"));
            gotoLogin();

            // Setting the trayIcon
            if (Preferences.getPreferences().isTrayIcon())
                createTrayIcon();
            else {
                stage.setOnCloseRequest((WindowEvent t) -> System.exit(0));
            }

            stage.show();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private void createTrayIcon() {
        if (!SystemTray.isSupported())
            return;

        Platform.setImplicitExit(false);
        tray = SystemTray.getSystemTray();

        java.awt.Image image = null;
        try {
            image = ImageIO.read(new URL("images/on.png"));
        } catch (IOException ex) {
            System.out.println(ex);
            System.exit(1);
        }

        stage.setOnCloseRequest((WindowEvent t) -> hide());

        PopupMenu popup = new PopupMenu();

        MenuItem showItem = new MenuItem("Show");
        showItem.addActionListener((ActionEvent e) -> show());

        MenuItem closeItem = new MenuItem("Close");
        closeItem.addActionListener((ActionEvent e) -> System.exit(0));

        popup.add(showItem);
        popup.add(closeItem);

        trayIcon = new TrayIcon(image, "P2P Chat", popup);
        trayIcon.addActionListener((ActionEvent e) -> show());

    }

    public void showProgramIsMinimizedMsg() {
        if (firstTime) {
            trayIcon.displayMessage("Online", "Window still open", TrayIcon.MessageType.INFO);
            firstTime = false;
        }
    }

    private void show() {
        Platform.runLater(() -> stage.show());
        tray.remove(trayIcon);
    }

    private void hide() {
        if (!User.getUser().isLogged())
            System.exit(0);

        Platform.runLater(() -> stage.hide());
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
        showProgramIsMinimizedMsg();
    }

    public void gotoRegister() {
        try {
            RegisterController register = (RegisterController) replaceSceneContent("/server/gui/main/Register.fxml");
            register.setApp(this);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void gotoLogin() {
        try {
            LoginController login = (LoginController) replaceSceneContent("/server/gui/main/Login.fxml");
            login.setApp(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gotoStartScreen() {
        try {
            StartScreenController startScreen = (StartScreenController) replaceSceneContent("/server/gui/main/StartScreen.fxml");
            startScreen.setApp(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Initializable replaceSceneContent(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Main.class.getResourceAsStream(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Main.class.getResource(fxml));
        AnchorPane page;
        try {
            page = loader.load(in);
        } finally {
            in.close();
        }
        int duration = 600;
        if (fxml.contains("StartScreen"))
            duration = 1000;

        FadeTransition ft = new FadeTransition(Duration.millis(duration), page);
        ft.setFromValue(0.1);
        ft.setToValue(1.0);
        ft.play();

        Scene scene = new Scene(page);
        scene.setCursor(new ImageCursor(new Image("images/curser.gif")));
        stage.setScene(scene);
        stage.sizeToScene();
        return (Initializable) loader.getController();
    }

}
