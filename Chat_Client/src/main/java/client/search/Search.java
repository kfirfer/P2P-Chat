package client.search;

import client.main.StartScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Search extends Application {

    private static Stage stage;
    private StartScreenController controller;

    public Search(StartScreenController startScreenController) {
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

    public static void setStage(Stage stage) {
        Search.stage = stage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/server/gui/search/Search.fxml"));

        Parent page = loader.load();

        SearchController searchController = loader.getController();
        stage.setResizable(false);
        searchController.setStartScreenController(controller);
        Scene scene = new Scene(page);
        scene.getStylesheets().add("/server/gui/search/Search.css");
        Search.stage.getIcons().add(new Image("images/search.png"));
        stage.setOnCloseRequest((WindowEvent event) -> {
            stage.hide();
        });
        stage.setScene(scene);
        stage.setTitle("Search");
        stage.show();

    }
}
