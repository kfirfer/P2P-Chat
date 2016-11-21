package client.server;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Connect {

    private static Connect connect;

    private SSLSocket socket;
    private BufferedReader input;
    private PrintStream output;

    private Connect() throws IOException {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = (SSLSocket) sslSocketFactory.createSocket("127.0.0.1", 5555);

        final String[] enabledCipherSuites = {"TLS_DH_anon_WITH_AES_128_GCM_SHA256"};
        socket.setEnabledCipherSuites(enabledCipherSuites);

        output = new PrintStream(socket.getOutputStream());
        input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public static Connect getConnection() {
        if (connect == null) {
            try {
                connect = new Connect();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return connect;
    }

    public void close() {
        if (socket != null)
            if (!socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    public BufferedReader getInput() {
        return input;
    }

    public PrintStream getOutput() {
        return output;
    }

}
