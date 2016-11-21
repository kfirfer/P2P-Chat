package client.block;

import client.main.StartScreenController;
import client.server.Server;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class BlockController implements Initializable {

    @FXML
    public ListView<String> list;

    private ObservableList<String> data = FXCollections.observableArrayList();

    private StartScreenController controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        String[] blocks = Server.getBlockList();
        for (String string : blocks) {
            data.add(string);
        }

        list.setItems(data);
    }

    public void setStartScreenController(StartScreenController controller) {
        this.controller = controller;
    }

    @FXML
    public void handleRemove(ActionEvent event) {
        String selected = list.getSelectionModel().getSelectedItem();
        if (selected != null) {
            list.getSelectionModel().clearSelection();
            data.remove(selected);
            controller.removeBlock(selected);

        }

    }

    @FXML
    public void handleClose(ActionEvent event) {
        Block.getStage().close();
        Block.setStage(null);
    }

}
