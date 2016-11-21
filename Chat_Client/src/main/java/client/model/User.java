package client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

// This class represent single connected user,  this is singleton class
public class User {

    private volatile static User intance = new User();

    private String name;
    private String password;
    private String email;
    private boolean agreement;
    private String about;
    private String ip;
    private int port;
    private boolean logged;

    private Set<Friend> friends = new HashSet<>();

    private User() {
    }

    public static User getUser() {
        return intance;
    }

    public void updateIp() {
        String ip = "127.0.0.1";
        setIp(ip);
    }

    public void updateIp2() {

        String ip = "127.0.0.1";
        try {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
            ip = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setIp(ip);
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {

        if (logged == false) {
            Iterator<Friend> friends = this.friends.iterator();
            while (friends.hasNext()) {
                Friend friend = friends.next();
                if (friend.isChatOpen()) {
                    friend.getChat().getStage().close();
                    friend.setChat(null);
                }
                friend.close();
            }
            this.friends.clear();
        }
        this.logged = logged;

    }

    public void addFriend(Friend friend) {
        friends.add(friend);
    }

    public void deleteFriend(Friend friend) {
        friends.remove(friend);
    }

    public String getName() {
        return name;
    }

    public void setName(String user) {
        this.name = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAgreement() {
        return agreement;
    }

    public void setAgreement(boolean agreement) {
        this.agreement = agreement;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

}
