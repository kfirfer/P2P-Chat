package client.main;

import client.Main;
import client.block.Block;
import client.chat.Chat;
import client.model.Friend;
import client.model.User;
import client.p2p.ClientHandler;
import client.p2p.Constants;
import client.preferences.Preferences;
import client.remote.client.Client;
import client.search.Search;
import client.server.Message;
import client.server.Server;
import client.server.Util;
import client.updater.Updater;
import client.utils.UtilDialogs;
import client.utils.UtilMusic;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

public class StartScreenController extends AnchorPane implements Initializable {

    private static ObservableList<Friend> data = FXCollections.observableArrayList();
    @FXML
    private Button buttonFriendRequests;
    @FXML
    private Button buttonAddFriend;
    @FXML
    private TableColumn<Friend, String> nick;
    @FXML
    private TableColumn<Friend, ImageView> status;
    @FXML
    private TableView<Friend> table;
    @FXML
    private HBox searchBox;
    private client.preferences.Gui preferences;
    private ObservableSet<String> blockList = FXCollections.observableSet();
    private ObservableSet<String> requestList = FXCollections.observableSet();

    private Image imageOn = new Image("images/on.png");
    private Image imageOff = new Image("images/off.png");

    private Main application;

    public static ObservableList<Friend> getFriends() {
        return data;
    }

    public void setApp(Main application) {
        this.application = application;
    }

    public void initialize(URL location, ResourceBundle resources) {
        ImageView imageView = new ImageView(new Image("images/friendrequest.png"));
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        buttonFriendRequests.setGraphic(imageView);

        imageView = new ImageView(new Image("images/search.png"));
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        buttonAddFriend.setGraphic(imageView);

        initTable();
        autoRefreshing();

        String[] blocks = Server.getBlockList();
        for (String string : blocks) {
            blockList.add(string);
        }

    }

    private void initTable() {
        table.setPlaceholder(new Label("No friends"));
        nick.setCellValueFactory(cellData -> cellData.getValue().getNickProperty());
        status.setCellValueFactory(new PropertyValueFactory<>("image"));
        nick.setCellFactory(col -> {
            final TableCell<Friend, String> cell = new TableCell<>();
            cell.itemProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null) {
                    final ContextMenu cellMenu = new ContextMenu();
                    final TableRow<?> row = cell.getTableRow();
                    final ContextMenu rowMenu;
                    if (row != null) {
                        rowMenu = cell.getTableRow().getContextMenu();
                        if (rowMenu != null) {
                            cellMenu.getItems().addAll(rowMenu.getItems());
                            cellMenu.getItems().add(new SeparatorMenuItem());
                        } else {
                            final ContextMenu tableMenu = cell.getTableView().getContextMenu();
                            if (tableMenu != null) {
                                cellMenu.getItems().addAll(tableMenu.getItems());
                                cellMenu.getItems().add(new SeparatorMenuItem());
                            }
                        }
                    }
                    final MenuItem sendMessage = new MenuItem("Send Message");
                    final MenuItem remoteControl = new MenuItem("Remote Control");
                    final MenuItem deleteFriend = new MenuItem("Delete friend");
                    final MenuItem block = new MenuItem("Block");
                    final MenuItem unblock = new MenuItem("UnBlock");

                    remoteControl.setOnAction((ActionEvent event) -> {
                        startRemote((Friend) cell.getTableRow().getItem());
                    });

                    sendMessage.setOnAction((ActionEvent event) -> {
                        startChat((Friend) cell.getTableRow().getItem());
                    });

                    deleteFriend.setOnAction((ActionEvent event) -> {
                        deleteFriend((Friend) cell.getTableRow().getItem());
                    });

                    block.setOnAction((ActionEvent event) -> {
                        addBlock(cell.getItem());
                        cellMenu.getItems().remove(block);
                        cellMenu.getItems().add(unblock);
                    });
                    unblock.setOnAction((ActionEvent event) -> {
                        removeBlock(cell.getItem());
                        cellMenu.getItems().remove(unblock);
                        cellMenu.getItems().add(block);
                    });

                    cellMenu.getItems().add(sendMessage);
                    cellMenu.getItems().add(remoteControl);
                    cellMenu.getItems().add(deleteFriend);
                    if (!blockList.contains(cell.getItem()))
                        cellMenu.getItems().add(block);
                    else
                        cellMenu.getItems().add(unblock);

                    cell.setContextMenu(cellMenu);
                } else {
                    cell.setContextMenu(null);
                }
            });
            cell.textProperty().bind(cell.itemProperty());
            return cell;
        });

        table.setRowFactory(tv -> {
            TableRow<Friend> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Friend rowData = row.getItem();
                    startChat(rowData);
                }
            });
            return row;
        });

        String[] friends = Server.getFriends();
        for (String string : friends) {
            data.add(new Friend(string, new ImageView(imageOff)));
        }

        table.setItems(data);

        table.getColumns().addListener(new ListChangeListener<Object>() {

            @SuppressWarnings("unchecked")
            @Override
            public void onChanged(Change<?> change) {
                change.next();
                if (change.wasReplaced()) {
                    table.getColumns().clear();
                    table.getColumns().addAll(nick, status);
                }
            }
        });

    }

    @FXML
    public void close(ActionEvent event) {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void about(ActionEvent event) {
        UtilDialogs.about();
    }

    @FXML
    public void friendRequests(ActionEvent event) {
        String friend = UtilDialogs.friendRequests(requestList);
        if (friend != null) {
            addFriend(friend);
            updateFriendRequests();
        }
    }

    @FXML
    public void checkUpdates(ActionEvent event) {
        Updater.checkUpdate();
    }

    @FXML
    public void blockList(ActionEvent event) {
        new Block(this);
    }

    @FXML
    public void properties(ActionEvent event) {
        preferences = new client.preferences.Gui();
        preferences.show();
    }

    @FXML
    public void logout(ActionEvent event) {
        User.getUser().setLogged(false);
        Search.setStage(null);
        Block.setStage(null);
        Server.logout();
        data.clear();
        blockList.clear();
        requestList.clear();
        application.gotoLogin();

    }

    @FXML
    public void searchFriend(ActionEvent event) {
        new Search(this);
    }

    private void autoRefreshing() {
        Runnable updateOn = () -> {
            while (User.getUser().isLogged()) {
                WhoIsOnline();
                updateFriendRequests();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(updateOn).start();

    }

    public void updateFriendRequests() {
        new Thread(() -> {
            String[] requests = Server.getRequests();
            if (requests.length > 0) {
                if (requests.length > requestList.size())
                    UtilMusic.play(UtilMusic.newFriendRequest);

                Platform.runLater(() -> {
                    buttonFriendRequests.setText(String.valueOf(requests.length));
                    buttonFriendRequests.setDisable(false);
                });
                for (String string : requests) {
                    requestList.add(string);
                }
            } else {
                Platform.runLater(() -> buttonFriendRequests.setText(""));
                buttonFriendRequests.setDisable(true);
            }
        }).start();

    }

    public void addBlock(String block) {
        new Thread(() -> {
            Server.addBlock(block);
        }).start();
        blockList.add(block);
    }

    public void removeBlock(String unblock) {
        new Thread(() -> {
            Server.removeBlock(unblock);
        }).start();
        blockList.remove(unblock);
    }

    public void addFriend(String friend) {
        new Thread(() -> {
            if (Server.addFriend(friend)) {
                data.add(new Friend(friend, new ImageView(new Image("images/off.png"))));
                WhoIsOnline();
            } else {
                System.out.println("Cant add yourself as a friend or existsing friends");
            }

        }).start();
    }

    public void deleteFriend(Friend friend) {
        new Thread(() -> {
            Server.deleteFriend(friend.getNick());
            if (Preferences.getPreferences().isAcceptMessageFromNotFriends()) {
                data.remove(friend);
            } else {
                User.getUser().deleteFriend(friend);
                friend.close();
            }
            data.remove(friend);
        }).start();
    }

    private void WhoIsOnline() {
        new Thread(() -> {
            String[] onlines = Server.whoIsOnline();

            for (int i = 0; i < data.size(); i++) {
                Friend friend = data.get(i);
                if (friend.getImage().getImage().equals(Chat.imageUnread))
                    continue;
                Platform.runLater(() -> {
                    friend.setImage(imageOff);
                });
                for (int j = 0; j < onlines.length; j++) {
                    if (friend.getNick().equals(onlines[j])) {
                        Platform.runLater(() -> {
                            friend.setImage(imageOn);
                        });
                    }
                }
            }
        }).start();
    }

    private boolean startConnectFriend(Friend friend) {
        if (!friend.isConnected()) {
            String[] address = Server.getIp(friend.getNick());
            if (address.length == 0) {
                UtilDialogs
                        .information("Cant reach friend, there are 3 possible reasons \n 1) Friend offline. \n 2) You in his client.block list \n "
                                + "3) His Chat port blocked by firewall or router");
                return false;
            } else {
                if (friend.startConnection(address[0], address[1])) {
                    new ClientHandler(friend).start();
                    return true;
                } else {
                    UtilDialogs
                            .information("Cant reach friend, there are 3 possible reasons \n 1) Friend offline. \n 2) You in his client.block list \n "
                                    + "3) His Chat port blocked by firewall or router");
                    return false;
                }
            }
        }
        return true;
    }

    public ObservableSet<String> getBlockList() {
        return this.blockList;
    }

    private void startChat(Friend friend) {
        new Thread(() -> {
            friend.getChat().show();

            if (friend.getImage().getImage().equals(Chat.imageUnread)) {
                friend.setImage(imageOn);
                WhoIsOnline();
            }
        }).start();
    }

    private void startRemote(Friend friend) {
        if (!startConnectFriend(friend))
            return;

        new Thread(() -> {
            new Thread(() -> {
                Client.startRemote(Preferences.getPreferences().getRemotePort());
            }).start();

            Message message = new Message();
            message.setMessage(Constants.REMOTE, User.getUser().getIp() + " "
                    + Preferences.getPreferences().getRemotePort());

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Util.sendBytes(message.toString(), friend.getOutput());
        }).start();

    }

}
