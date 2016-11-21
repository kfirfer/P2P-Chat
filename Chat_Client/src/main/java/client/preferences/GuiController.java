package client.preferences;

import client.utils.UtilDialogs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class GuiController extends AnchorPane implements Initializable {

    @FXML
    private TextField userName;

    @FXML
    private TextField port;

    @FXML
    private CheckBox ignoreMessages;

    @FXML
    private CheckBox trayIcon;

    @FXML
    private Button cancel;

    @FXML
    private Button ok;

    private Stage stage;

    private Preferences preference = Preferences.getPreferences();

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    public void handleOK(ActionEvent e) {
        if (preference.getPort() != Integer.parseInt(port.getText())
                || !preference.getUser().equals(userName.getText()) || preference.isTrayIcon() != trayIcon.isSelected()
                || preference.isAcceptMessageFromNotFriends() != ignoreMessages.isSelected()) {
            try {
                preference.setPort(Integer.parseInt(port.getText()));
                preference.setUser(userName.getText());
                preference.setTrayIcon(trayIcon.isSelected());
                preference.setAcceptMessageFromNotFriends(ignoreMessages.isSelected());
                preference.savePreferences();
            } catch (Exception e1) {
                e1.printStackTrace();
                UtilDialogs.exception(e1);
                return;
            }

            UtilDialogs.information("All client.preferences will be applied in the next launch");
        }
        stage.hide();

    }

    @FXML
    public void handleCancel(ActionEvent e) {
        stage.hide();
    }

    public void initPreferences() {
        userName.setText(preference.getUser());
        port.setText(String.valueOf(preference.getPort()));
        ignoreMessages.setSelected(preference.isAcceptMessageFromNotFriends());
        trayIcon.setSelected(preference.isTrayIcon());
    }

}
