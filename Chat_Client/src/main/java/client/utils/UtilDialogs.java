package client.utils;

import javafx.application.Platform;
import javafx.collections.ObservableSet;
import javafx.scene.text.Font;
import org.controlsfx.control.action.Action;
import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UtilDialogs {

    public static boolean friendWantRemote(String friend) {
        Action response = Dialogs.create().title("Confirm").masthead("Remote control request from " + friend)
                .message("Do you want let him control ?").actions(Dialog.Actions.OK, Dialog.Actions.CANCEL)
                .showConfirm();

        if (response == Dialog.Actions.OK) {
            return true;
        } else {
            return false;
        }
    }

    public static void about() {
        Dialogs.create().title("Chat app").masthead("About").message("Author: Kfir Fersht\nEmail: kfirfer@gmail.com")
                .showInformation();

    }

    public static void exception(Throwable e) {
        Platform.runLater(() -> Dialogs.create().title("Exception").masthead("Error").message(e.toString()).showException(e));
    }

    public static <T> String friendRequests(ObservableSet<String> requestList) {
        List<String> list = new ArrayList<>();
        list.addAll(requestList);
        Optional<String> str = Dialogs.create().title("Requests").masthead("Requests list").message("")
                .showChoices(list);

        if (str.isPresent())
            return str.get();
        else
            return null;
    }

    public static Font font(Font curFont) {
        Optional<Font> font = Dialogs.create().title("Font chooser").masthead("Pick your favorite font ").message("")
                .showFontSelector(curFont);
        if (font.isPresent())
            return font.get();
        return null;
    }

    public static void information(String info) {
        Platform.runLater(() -> Dialogs.create().title("Information").masthead("Info").message(info).showInformation());
    }

    public static void error(int i) {
        Platform.runLater(() -> Dialogs.create().title("Error").masthead("Error code [" + i + "]")
                .message("Please contact at: kfirfer@gmail.com").showError());

    }

    public static void versionUpToDate() {
        Platform.runLater(() -> Dialogs.create().title("Up to date").masthead("Version Up to Date").message("No updates found")
                .showInformation());
    }

}
