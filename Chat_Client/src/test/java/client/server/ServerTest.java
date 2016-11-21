package client.server;

import org.junit.Test;

/**
 * Created by tech on 6/17/16.
 */
public class ServerTest {

    @Test
    public void test() throws InterruptedException {
        String str = "something";
        Connect connect = Connect.getConnection();

        Util.sendBytes(str, connect.getOutput());

    }
}
