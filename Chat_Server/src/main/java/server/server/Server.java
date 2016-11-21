package server.server;

import server.gui.GuiController;
import server.server.db.ConnectionManager;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private int port = 5555;
    private SSLServerSocket serverSocket = null;
    private boolean running;
    private List<ClientHandler> clients;
    private GuiController controller;
    SSLSocket sslSocket;

    public Server(GuiController controller) {
        this.controller = controller;
        ConnectionManager.getInstance().getConnection(controller);
        Thread.setDefaultUncaughtExceptionHandler((Thread t, Throwable e) -> controller.exception(e));
    }

    private void start() {

        SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        try {
            this.serverSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        final String[] enabledCipherSuites = {"TLS_DH_anon_WITH_AES_128_GCM_SHA256"};
        this.serverSocket.setEnabledCipherSuites(enabledCipherSuites);

        while (running) {
            try {
                sslSocket = (SSLSocket) serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            ClientHandler client = new ClientHandler(sslSocket, controller);
            new Thread(client).start();
            clients.add(client);
        }

    }

    public void stopServer() {
        controller.appendTextAreaConnections("Server stopped");
        running = false;
        if (clients != null) {
            for (int i = 0; i < clients.size(); i++) {
                clients.get(i).close();
            }
            clients.clear();
        }
        clients = null;
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        serverSocket = null;
    }

    public void startServer() {
        controller.appendTextAreaConnections("Server started");
        clients = new ArrayList<>();
        running = true;
        Thread startServerWorker = new Thread(() -> {
            start();
        });

        startServerWorker.setPriority(9);
        startServerWorker.start();

    }

    public boolean isRunning() {
        return this.running;
    }

}
