package client.remote.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Client {

    private static Client instance;
    private JDesktopPane desktop = new JDesktopPane();
    private ServerSocket serverSocket;
    private Handler client;

    public static void startRemote(int port) {
        if (instance == null) {
            instance = new Client();
            instance.initialize(port);
        }

    }

    public void initialize(int port) {

        try {
            serverSocket = new ServerSocket(port);
            drawGUI();

            Socket socket = serverSocket.accept();
            client = new Handler(socket, desktop);
        } catch (IOException ex) {
            close();
        }
    }

    public void drawGUI() {
        JFrame frame = new JFrame();
        frame.add(desktop, BorderLayout.CENTER);

        WindowListener exitListener = new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                close();
                frame.dispose();
            }
        };
        frame.addWindowListener(exitListener);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        frame.setVisible(true);
    }

    private void close() {
        instance = null;
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
        if (client != null)
            client.stopRunning();
    }
}
