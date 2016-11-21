package client.model;

import client.chat.Chat;
import client.p2p.Constants;
import client.server.Message;
import client.server.Util;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

// This class represents the friends and manage the connection with them
public class Friend {
    private final SimpleStringProperty nick;
    private final ImageView image;

    private SSLSocket socket;
    private BufferedReader input;
    private PrintStream output;
    private Chat chat;

    public Friend(String nick, ImageView image) {
        User.getUser().addFriend(this);
        this.nick = new SimpleStringProperty(nick);
        this.image = image;
    }

    public boolean startConnection(String ip, String port) {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();

        try {
            socket = (SSLSocket) sslSocketFactory.createSocket(ip, Integer.parseInt(port));
            final String[] enabledCipherSuites = {"TLS_DH_anon_WITH_AES_128_GCM_SHA256"};
            socket.setEnabledCipherSuites(enabledCipherSuites);
            output = new PrintStream(socket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e1) {
            return false;
        } catch (NumberFormatException e2) {
            System.out.println(e2);
            return false;
        }
        Message message = new Message();
        message.setMessage(Constants.WHOIM, User.getUser().getName());
        Util.sendBytes(message.toString(), output);
        return true;
    }

    public boolean isConnected() {
        if (socket == null || socket.isClosed() || output == null || input == null || output.checkError())
            return false;
        return true;
    }

    public Chat getChat() {
        if (chat == null)
            chat = new Chat(this);
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public boolean isChatOpen() {
        if (chat != null)
            return true;
        return false;
    }

    public void close() {
        try {
            if (socket != null)
                socket.close();

            if (output != null)
                output.close();

            if (input != null)
                input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ImageView getImage() {
        return this.image;
    }

    public void setImage(Image value) {
        this.image.setImage(value);
    }

    public String getNick() {
        return nick.get();
    }

    public void setNick(String nick) {
        this.nick.set(nick);
    }

    public StringProperty getNickProperty() {
        return nick;
    }

    public BufferedReader getInput() {
        return input;
    }

    public void setInput(BufferedReader input) {
        this.input = input;
    }

    public PrintStream getOutput() {
        return output;
    }

    public void setOutput(PrintStream output) {
        this.output = output;
    }

    public SSLSocket getSocket() {
        return socket;
    }

    public void setSocket(SSLSocket socket) {
        this.socket = socket;
    }

}
