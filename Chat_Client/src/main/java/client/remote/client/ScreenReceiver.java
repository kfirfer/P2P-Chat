package client.remote.client;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ScreenReceiver extends Thread {

    public boolean running = true;
    private ObjectInputStream input = null;
    private JPanel cPanel = null;

    public ScreenReceiver(ObjectInputStream objectInputStream, JPanel jPanel) {
        input = objectInputStream;
        cPanel = jPanel;

        start();
    }

    public void run() {
        try {
            while (running) {
                ImageIcon imageIcon = (ImageIcon) input.readObject();
                Image image = imageIcon.getImage();
                image = image.getScaledInstance(cPanel.getWidth(), cPanel.getHeight(), Image.SCALE_FAST);
                Graphics graphics = cPanel.getGraphics();
                graphics.drawImage(image, 0, 0, cPanel.getWidth(), cPanel.getHeight(), cPanel);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void stopRunning() {
        running = false;
    }
}
