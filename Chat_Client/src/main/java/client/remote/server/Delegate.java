package client.remote.server;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Delegate extends Thread {

    private Socket socket = null;
    private Robot robot = null;
    private boolean running = true;

    public Delegate(Socket socket, Robot robot) {
        this.socket = socket;
        this.robot = robot;
        start();
    }

    public void run() {
        Scanner scanner;
        try {
            scanner = new Scanner(socket.getInputStream());
            while (running) {
                int command = scanner.nextInt();
                switch (command) {
                    case -1:
                        robot.mousePress(scanner.nextInt());
                        break;
                    case -2:
                        robot.mouseRelease(scanner.nextInt());
                        break;
                    case -3:
                        robot.keyPress(scanner.nextInt());
                        break;
                    case -4:
                        robot.keyRelease(scanner.nextInt());
                        break;
                    case -5:
                        robot.mouseMove(scanner.nextInt(), scanner.nextInt());
                        break;
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (NoSuchElementException ex2) {
            ex2.printStackTrace();
        }
    }

    public void stopRunning() {
        this.running = false;
    }

}
