package server.server.db;

import server.gui.GuiController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton pattern for Connection database
 *
 * @author Kfir fersht
 */
public class ConnectionManager {

    private final String HOST = "jdbc:mysql://localhost/client.chat";
    private final String USERNAME = "chat_user";
    private final String PASSWORD = "123123";
    private GuiController controller;

    private static ConnectionManager instance = new ConnectionManager();

    private Connection conn;

    private ConnectionManager() {
    }

    public static ConnectionManager getInstance() {
        return instance;
    }

    public Connection getConnection(GuiController controller) {
        if (conn == null) {
            try {
                this.controller = controller;
                conn = DriverManager.getConnection(HOST, USERNAME, PASSWORD);
                controller.appendTextAreaConnections("DB connected");
            } catch (SQLException e) {
                controller.exception(e);
            }
        }
        return conn;
    }

    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                controller.exception(e);
            }
        }
    }
}