package client.p2p;

import client.model.Friend;
import client.model.User;
import client.remote.server.Server;
import client.server.Message;
import client.server.Util;
import client.utils.UtilDialogs;
import javafx.application.Platform;

import java.io.IOException;

public class ClientHandler extends Thread {

    private Friend friend;
    private Message message = new Message();

    public ClientHandler(Friend friend) {
        this.friend = friend;
    }

    public void run() {
        String string;
        while (friend.isConnected() && User.getUser().isLogged()) {
            if ((string = Util.readBytes(friend.getInput())) == null)
                break;
            if (!friend.isConnected())
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
        String[] strings = ip.split(" ");
        Thread remote = new Thread(() -> {
            Server.startRemote(strings[0], Integer.parseInt(strings[1]));
        });

        Platform.runLater(() -> {
            if (UtilDialogs.friendWantRemote(friend.getNick())) {
                remote.start();
            }
            ;
        });

    }

    private void close() {
        try {
            if (friend.getInput() != null)
                friend.getInput().close();
            if (friend.getOutput() != null)
                friend.getOutput().close();
            if (friend.getSocket() != null)
                friend.getSocket().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
