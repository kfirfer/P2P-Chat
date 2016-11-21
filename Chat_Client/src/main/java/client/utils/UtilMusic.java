package client.utils;

import client.preferences.Preferences;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class UtilMusic {

    public static final int message = 1, connection = 2, messageWindowOn = 3, newFriendRequest = 4;
    private static final String messagePath = "/sound/Message.mp3";
    private static final String connectionPath = "/sound/Connection.mp3";
    private static final String messageWindowOnPath = "/sound/MessageWindowOn.mp3";
    private static final String newFriendRequestPath = "/sound/newRequest.mp3";

    public static void play(int choice) {
        new Thread(() -> {
            String path = null;

            switch (choice) {
                case message:
                    path = messagePath;
                    break;
                case connection:
                    path = connectionPath;
                    break;
                case messageWindowOn:
                    path = messageWindowOnPath;
                    break;
                case newFriendRequest:
                    path = newFriendRequestPath;
                    break;
            }

            URL urlFile = Preferences.class.getResource(path);
            Media media = null;
            try {
                media = new Media(new File(urlFile.toURI()).toURI().toURL().toExternalForm());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }).start();
    }
}
