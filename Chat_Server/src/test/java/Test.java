import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.util.Arrays;

/**
 * Created by kfirf on 11/18/16.
 */
public class Test {

    public static void main(String[] args) {
        SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
        String[] cipherSuites = factory.getSupportedCipherSuites();
        System.out.println(Arrays.toString(cipherSuites));
    }
}
