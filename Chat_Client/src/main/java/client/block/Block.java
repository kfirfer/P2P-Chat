package client.block;

import client.main.StartScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Block extends Application {

    private static Stage stage;
    private StartScreenController controller;

    public Block(StartScreenController startScreenController) {
        if (stage == null) {
            controller = startScreenController;
            try {
                start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            stage.show();
        }
    }

    public static Stage getStage() {
        return stage;
    }

    public static void setStage(Stage stage) {
        Block.stage = stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/server/gui/block/Block.fxml"));

        Parent page = loader.load();

        BlockController blockController = loader.getController();
        blockController.setStartScreenController(controller);
        Scene scene = new Scene(page);
        scene.getStylesheets().add("/server/gui/block/Block.css");
        stage.getIcons().add(new Image("file:resources/client.search.png"));
        stage.setOnCloseRequest((WindowEvent event) -> {
            stage.close();
            stage = null;
        });
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Search");
        stage.show();

    }
}
