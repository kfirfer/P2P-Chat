package server.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import server.server.Server;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

public class Gui extends Application {

    private boolean firstTime = true;
    private TrayIcon trayIcon;
    private SystemTray tray;
    private Stage stage;
    private Server server;
    private GuiController controller;

    public static void main(String str[]) {
        launch(str);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        this.stage = primaryStage;
        stage.getIcons().add(new Image("icon.png"));
        stage.setTitle("Server client.chat");
        createTrayIcon();

        FXMLLoader loader = new FXMLLoader();
        InputStream in = Gui.class.getResourceAsStream("/Gui.fxml");
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Gui.class.getResource("/Gui.fxml"));
        AnchorPane page;
        try {
            page = loader.load(in);
        } finally {
            in.close();
        }

        Scene scene = new Scene(page);
        controller = loader.getController();

        server = new Server(controller);
        controller.setServer(server);

        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();

    }

    private void createTrayIcon() {
        if (SystemTray.isSupported()) {
            Platform.setImplicitExit(false);
            tray = SystemTray.getSystemTray();
            java.awt.Image image = null;
            try {
                image = ImageIO.read(Gui.class.getResource("/trayicon.png"));
            } catch (IOException ex) {
                ex.printStackTrace();
                System.exit(1);
            }
            stage.setOnCloseRequest(t -> hide());
            final ActionListener closeListener = e -> System.exit(0);
            final ActionListener showListener = e -> show();
            PopupMenu popup = new PopupMenu();
            MenuItem showItem = new MenuItem("Show");
            showItem.addActionListener(showListener);
            popup.add(showItem);
            MenuItem closeItem = new MenuItem("Close");
            closeItem.addActionListener(closeListener);
            popup.add(closeItem);
            trayIcon = new TrayIcon(image, "P2P Chat", popup);
            trayIcon.addActionListener(showListener);
        }
    }

    public void showProgramIsMinimizedMsg() {
        if (firstTime) {
            trayIcon.displayMessage("Online", "Window still open", TrayIcon.MessageType.INFO);
            firstTime = false;
        }
    }

    private void show() {
        Platform.runLater(() -> {
            stage.show();
            tray.remove(trayIcon);
        });
    }

    private void hide() {
        Platform.runLater(() -> {
            if (server.isRunning()) {
                stage.hide();
                try {
                    tray.add(trayIcon);
                } catch (AWTException e) {
                    e.printStackTrace();
                }
                showProgramIsMinimizedMsg();
            } else {
                System.exit(0);
            }
        });
    }

}
