package client.p2p;

import client.chat.Chat;
import client.main.StartScreenController;
import client.model.Friend;
import client.model.User;
import client.preferences.Preferences;
import client.remote.server.Server;
import client.server.Message;
import client.server.Util;
import client.utils.UtilDialogs;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.image.ImageView;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class ServerHandler implements Runnable {

    private SSLSocket socket;
    private BufferedReader input;
    private PrintStream output;
    private Message message;
    private Friend friend;

    public ServerHandler(SSLSocket socket) {
        this.socket = socket;
        this.message = new Message();
        try {
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new PrintStream(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String string;
        if ((string = Util.readBytes(input)) == null) {
            close();
            return;
        }
        if (!message.decodeMessage(string)) {
            close();
            return;
        }
        if (message.getType() != Constants.WHOIM) {
            close();
            return;
        }
        if (openChat(message.getMessage()) == false) {
            close();
            return;
        }
        while (!socket.isClosed() && User.getUser().isLogged()) {
            if ((string = Util.readBytes(input)) == null)
                break;
            if (User.getUser().isLogged() == false)
                break;
            if (!message.decodeMessage(string))
                break;
            switch (message.getType()) {
                case Constants.MESSAGE:
                    handleMessage(message.getMessage());
                    break;
                case Constants.REMOTE:
                    handleRemote(message.getMessage());
                    break;
                case Constants.TYPING:
                    handleTyping();
                    break;
                default:
                    System.err.println("Unkown type");
                    break;
            }
        }
        close();
    }

    private void handleTyping() {
        friend.getChat().typingFromFriend();
    }

    private void handleMessage(String message) {
        friend.getChat().append(message);
    }

    private void handleRemote(String ip) {
        String strings[] = ip.split(" ");
        Thread remote = new Thread(() -> {
            Server.startRemote(strings[0], Integer.parseInt(strings[1]));
        });

        Platform.runLater(() -> {
            if (UtilDialogs.friendWantRemote(friend.getNick()))
                remote.start();
        });
    }

    private boolean openChat(String name) {

        boolean flag = false;

        ObservableList<Friend> friends = StartScreenController.getFriends();
        for (int i = 0; i < friends.size(); i++) {
            if (name.equals(friends.get(i).getNick())) {
                this.friend = friends.get(i);
                friend.setOutput(output);
                friend.setInput(input);
                friend.setSocket(socket);
                flag = true;
            }
        }

        if (flag == false && Preferences.getPreferences().isAcceptMessageFromNotFriends()) {
            friend = new Friend(name, new ImageView(Chat.imageUnread));
            friend.setOutput(output);
            friend.setInput(input);
            friend.setSocket(socket);
            friend.getChat();
            flag = true;
        }

        return flag;
    }

    private void close() {
        try {
            if (input != null)
                input.close();
            if (output != null)
                output.close();
            if (socket != null)
                socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
