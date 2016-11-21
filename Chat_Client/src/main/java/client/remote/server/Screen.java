package client.remote.server;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Screen extends Thread {

    private Socket socket = null;
    private Robot robot = null;
    private Rectangle rectangle = null;
    private boolean running = true;

    public Screen(Socket socket, Robot robot, Rectangle rect) {
        this.socket = socket;
        this.robot = robot;
        rectangle = rect;
        start();
    }

    public void run() {
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            output.writeObject(rectangle);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        while (running) {
            BufferedImage image = robot.createScreenCapture(rectangle);
            ImageIcon imageIcon = new ImageIcon(image);
            try {
                output.writeObject(imageIcon);
                output.reset();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void stopRunning() {
        this.running = false;
    }

}
