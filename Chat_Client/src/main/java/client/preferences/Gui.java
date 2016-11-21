package client.preferences;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.io.InputStream;

public class Gui {

    private Stage stage;
    private volatile GuiController controller;

    public Gui() {
        try {
            start(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start(Stage primeryStage) throws IOException {
        this.stage = primeryStage;
        stage.setTitle("Preferences");
        stage.setOnCloseRequest((WindowEvent event) -> stage.hide());
        FXMLLoader loader = new FXMLLoader();
        InputStream in = Gui.class.getResourceAsStream("/server/gui/preferences/Gui.fxml");
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Gui.class.getResource("/server/gui/preferences/Gui.fxml"));
        AnchorPane page;
        try {
            page = loader.load(in);
            controller = loader.getController();
        } finally {
            in.close();
        }

        Scene scene = new Scene(page);
        scene.setCursor(new ImageCursor(new Image("images/curser.gif")));
        stage.getIcons().add(new Image("images/preferences.png"));
        stage.setResizable(false);
        stage.setScene(scene);
        stage.sizeToScene();

    }

    public void show() {
        new Thread(() -> {
            while (controller == null || stage == null) {
            }

            Platform.runLater(() -> {
                controller.setStage(stage);
                controller.initPreferences();
                stage.show();
            });
        }).start();
    }

}
