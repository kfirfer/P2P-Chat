package client.chat;

import client.Main;
import client.model.Friend;
import client.utils.UtilMusic;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.Notifications;

import java.io.InputStream;
import java.io.PrintStream;

public class Chat {

    public static final Image imageUnread = new Image("images/newmsg.png");
    private volatile Stage stage;
    private Friend friend;
    private PrintStream output;
    private volatile ChatController chatController;
    private Image imageOn = new Image("images/on.png");

    public Chat(Friend friend) {
        this.friend = friend;
        Platform.runLater(() -> {
            try {
                start(new Stage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void start(Stage chatStage) throws Exception {
        this.stage = chatStage;

        stage.setTitle(friend.getNick());
        stage.setOnCloseRequest((WindowEvent event) -> stage.hide());

        FXMLLoader loader = new FXMLLoader();
        InputStream in = Chat.class.getResourceAsStream("/server/gui/chat/Chat.fxml");
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(Chat.class.getResource("/server/gui/chat/Chat.fxml"));
        AnchorPane page;
        try {
            page = loader.load(in);
        } finally {
            in.close();
        }

        chatController = loader.getController();

        chatController.setFriend(this.friend);

        Scene scene = new Scene(page);
        scene.setCursor(new ImageCursor(new Image("images/curser.gif")));
        scene.getStylesheets().add(Chat.class.getResource("/server/gui/chat/Chat.css").toExternalForm());
        this.stage.getIcons().add(new Image("images/message-unread.png"));
        stage.setScene(scene);
        stage.sizeToScene();
    }

    public void show() {
        Platform.runLater(() -> {
            stage.show();
            stage.setIconified(false);
        });
    }

    public void append(String msg) {
        new Thread(() -> {
            while (chatController == null || stage == null) {
            }

            if (!stage.isShowing() || stage.isIconified()) {
                UtilMusic.play(UtilMusic.message);
                friend.setImage(imageUnread);
                String msg2 = msg;
                if (msg.length() > 35)
                    msg2 = msg2.substring(0, 34) + "...";
                showNotification(msg2);
                Platform.runLater(() -> {
                    if (!stage.isIconified() && !Main.getStage().isShowing()) {
                        stage.show();
                        stage.setIconified(true);
                    }
                });
            } else {
                UtilMusic.play(UtilMusic.messageWindowOn);
            }
            chatController.appendMessage("[" + friend.getNick() + "] " + msg);

        }).start();
    }

    public PrintStream getOutput() {
        return this.output;
    }

    public Stage getStage() {
        return stage;
    }

    public void showNotification(String msg) {
        Platform.runLater(() -> {
            try {
                Notifications.create().title(friend.getNick()).text(msg).onAction(event -> {
                    stage.show();
                    stage.setIconified(false);
                    friend.setImage(imageOn);
                }).show();
            } catch (Exception e) {
            }
        });
    }

    public void typingFromFriend() {
        while (chatController == null) {
        }
        chatController.typingFromFriend();
    }

}
