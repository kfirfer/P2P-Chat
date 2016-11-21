package client.updater;

import client.Main;
import client.utils.UtilDialogs;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Start extends JFrame {

    private static final long serialVersionUID = -1429706941117206677L;
    private final String root = "update/";

    private JTextArea outText;
    private JButton cancel;
    private JButton launch;
    private JScrollPane sp;
    private JPanel pan1;
    private JPanel pan2;

    public Start() {
        initComponents();
        outText.setText("Contacting Download Server...");
        download();
    }

    private void initComponents() {

        this.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setTitle("Updating");

        pan1 = new JPanel();
        pan1.setLayout(new BorderLayout());

        pan2 = new JPanel();
        pan2.setLayout(new FlowLayout());

        outText = new JTextArea();
        sp = new JScrollPane();
        sp.setViewportView(outText);

        launch = new JButton("Launch App");
        launch.setEnabled(false);
        launch.addActionListener((ActionEvent e) -> {
            launch();
        });
        pan2.add(launch);

        cancel = new JButton("Cancel Update");
        cancel.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });
        pan2.add(cancel);
        pan1.add(sp, BorderLayout.CENTER);
        pan1.add(pan2, BorderLayout.SOUTH);

        add(pan1);
        pack();
        this.setSize(400, 300);
    }

    private void download() {
        new Thread(() -> {
            try {
                downloadFile(getDownloadLinkFromHost());
                unzip();
                renameJarFile();
                copyFiles(new File(root), new File("").getAbsolutePath());
                cleanup();
                launch.setEnabled(true);
                outText.setText(outText.getText() + "\n" + "Update Finished!");
                cancel.setEnabled(false);
            } catch (Exception ex) {
                UtilDialogs.exception(ex);
                ex.printStackTrace();
            }
        }).start();
    }

    private void launch() {

        String[] run = {"java", "-jar", "new.jar"};
        File newName = null;
        try {
            newName = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        run[2] = newName.getName();

        try {
            Runtime.getRuntime().exec(run);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }

    private void cleanup() {
        outText.setText(outText.getText() + "\nPreforming clean up...");
        File f = new File("update.zip");
        f.delete();
        remove(new File(root));
        new File(root).delete();
    }

    private void remove(File f) {
        File[] files = f.listFiles();
        for (File ff : files) {
            if (ff.isDirectory()) {
                remove(ff);
                ff.delete();
            } else {
                ff.delete();
            }
        }
    }

    public void renameJarFile() {

        String currentFileName = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath())
                .getName();

        File oldName = new File(root + "new.jar");
        File newName = new File(root + currentFileName);
        oldName.renameTo(newName);

    }

    private void copyFiles(File f, String dir) throws IOException {
        File[] files = f.listFiles();
        for (File ff : files) {
            if (ff.isDirectory()) {
                new File(dir + "/" + ff.getName()).mkdir();
                copyFiles(ff, dir + "/" + ff.getName());
            } else {
                copy(ff.getAbsolutePath(), dir + "/" + ff.getName());
            }

        }
    }

    public void copy(String srFile, String dtFile) throws IOException {

        File f1 = new File(srFile);
        File f2 = new File(dtFile);

        InputStream in = new FileInputStream(f1);
        OutputStream out = new FileOutputStream(f2);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    @SuppressWarnings("resource")
    private void unzip() throws IOException {
        int BUFFER = 2048;
        BufferedOutputStream bufferedOutputStream;
        BufferedInputStream bufferedInputStream;
        ZipEntry entry;
        ZipFile zipfile = new ZipFile("update.zip");
        Enumeration<?> e = zipfile.entries();
        (new File(root)).mkdir();
        while (e.hasMoreElements()) {
            entry = (ZipEntry) e.nextElement();
            outText.setText(outText.getText() + "\nExtracting: " + entry);
            if (entry.isDirectory())
                (new File(root + entry.getName())).mkdir();
            else {
                (new File(root + entry.getName())).createNewFile();
                bufferedInputStream = new BufferedInputStream(zipfile.getInputStream(entry));
                int count;
                byte data[] = new byte[BUFFER];
                FileOutputStream fos = new FileOutputStream(root + entry.getName());
                bufferedOutputStream = new BufferedOutputStream(fos, BUFFER);
                while ((count = bufferedInputStream.read(data, 0, BUFFER)) != -1) {
                    bufferedOutputStream.write(data, 0, count);
                }
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                bufferedInputStream.close();
            }
        }

    }

    private void downloadFile(String link) throws IOException {
        URL url = new URL(link);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        long max = conn.getContentLength();
        outText.setText(outText.getText() + "\n" + "Downloding file...\nUpdate Size(compressed): " + max + " Bytes");
        BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(new File("update.zip")));
        byte[] buffer = new byte[32 * 1024];
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
            fOut.write(buffer, 0, bytesRead);
        }
        fOut.flush();
        fOut.close();
        is.close();
        outText.setText(outText.getText() + "\nDownload Complete!");

    }

    private String getDownloadLinkFromHost() throws IOException {
        String path = Updater.downloadURL;
        URL url = new URL(path);

        InputStream html = url.openStream();

        int c = 0;
        StringBuilder buffer = new StringBuilder("");

        while (c != -1) {
            c = html.read();
            buffer.append((char) c);

        }
        return buffer.substring(buffer.indexOf("[url]") + 5, buffer.indexOf("[/url]"));
    }

}
