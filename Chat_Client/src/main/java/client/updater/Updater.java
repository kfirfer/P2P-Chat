package client.updater;

import client.utils.UtilDialogs;

import java.io.InputStream;
import java.net.URL;

public class Updater {
    public final static String downloadURL = "http://globnet.co.il/client.updater/client.chat/url.html";
    public static final double VERSION = 0.77;
    private final static String versionURL = "http://globnet.co.il/client.updater/client.chat/version.html";
    private final static String historyURL = "http://globnet.co.il/client.updater/client.chat/overview.html";

    public static String getLatestVersion() throws Exception {
        String data = getData(versionURL);
        return data.substring(data.indexOf("[version]") + 9, data.indexOf("[/version]"));
    }

    public static String getWhatsNew() throws Exception {
        String data = getData(historyURL);
        return data.substring(data.indexOf("[overview]") + 10, data.indexOf("[/overview]"));
    }

    private static String getData(String address) throws Exception {
        URL url = new URL(address);
        InputStream html;
        html = url.openStream();
        int c = 0;
        StringBuffer buffer = new StringBuffer("");
        while (c != -1) {
            c = html.read();
            buffer.append((char) c);
        }
        return buffer.toString();
    }

    public static void startupCheckUpdate() {
        new Thread(() -> {
            try {
                if (Double.parseDouble(Updater.getLatestVersion()) > VERSION)
                    new WhatsNew(Updater.getWhatsNew());
            } catch (Exception ex) {
                System.err.println("Problem get update");
            }
        }).start();
    }

    public static void checkUpdate() {
        new Thread(() -> {
            try {
                if (Double.parseDouble(Updater.getLatestVersion()) > VERSION)
                    new WhatsNew(Updater.getWhatsNew());
                else
                    UtilDialogs.versionUpToDate();
            } catch (Exception ex) {
                System.err.println("Problem get update");
            }

        }).start();
    }
}