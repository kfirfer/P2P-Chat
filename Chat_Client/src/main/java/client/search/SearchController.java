package client.search;

import client.main.StartScreenController;
import client.server.Server;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Callback;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchController implements Initializable {

    private final Image fruitImage = new Image("file:resources/add_friend.png");
    @FXML
    public TableView<Result> table;
    @FXML
    public TableColumn<Result, String> nameCol;
    @FXML
    public TableColumn<Result, Result> addFriendCol;
    @FXML
    private HBox searchBox;
    private ObservableList<Result> list = FXCollections.observableArrayList();
    private SearchBox searchingBox;
    private StartScreenController controller;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        searchingBox = new SearchBox();
        searchBox.getChildren().add(searchingBox);

        table.setPlaceholder(new Label(""));
        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameProperty());

        addFriendCol.setCellValueFactory(new Callback<CellDataFeatures<Result, Result>, ObservableValue<Result>>() {
            @SuppressWarnings({"unchecked", "rawtypes"})
            @Override
            public ObservableValue<Result> call(CellDataFeatures<Result, Result> features) {
                return new ReadOnlyObjectWrapper(features.getValue());
            }
        });

        addFriendCol.setCellFactory(new Callback<TableColumn<Result, Result>, TableCell<Result, Result>>() {
            @Override
            public TableCell<Result, Result> call(TableColumn<Result, Result> btnCol) {
                return new TableCell<Result, Result>() {
                    final ImageView buttonGraphic = new ImageView();
                    final Button button = new Button();

                    {
                        button.setGraphic(buttonGraphic);
                    }

                    @Override
                    public void updateItem(final Result person, boolean empty) {
                        super.updateItem(person, empty);
                        if (person != null) {
                            switch ("fruit") {
                                case "fruit":
                                    // button.setText("Add");
                                    buttonGraphic.setImage(fruitImage);
                                    buttonGraphic.setFitHeight(30);
                                    buttonGraphic.setFitWidth(30);
                                    break;

                                default:
                                    button.setText("Buy coffee");
                                    break;
                            }

                            setGraphic(button);
                            button.setOnAction((ActionEvent event) -> {
                                controller.addFriend(person.getName());
                            });
                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
        table.setItems(list);

    }

    private void updateResults(String newValue) {
        ExecutorService executer = Executors.newSingleThreadExecutor();

        Runnable worker = new Thread(() -> {
            list.clear();
            if (newValue.length() > 0) {
                String[] strings = Server.search(newValue);
                if (strings != null) {
                    for (String string : strings) {
                        list.add(new Result(string));
                    }
                    refreshTable();
                }
            }
        });

        executer.execute(worker);
    }

    void refreshTable() {
        final List<Result> items = table.getItems();
        if (items == null || items.size() == 0)
            return;

        final Result item = table.getItems().get(0);
        items.remove(0);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                items.add(0, item);
            }
        });
    }

    public void setStartScreenController(StartScreenController controller) {
        this.controller = controller;

    }

    public static class Result {

        private final SimpleStringProperty name;
        private final SimpleBooleanProperty add;

        private Result(String name) {
            this.name = new SimpleStringProperty(name);
            this.add = new SimpleBooleanProperty(true);
        }

        public SimpleBooleanProperty getAddProperty() {
            return add;
        }

        public SimpleStringProperty getNameProperty() {
            return name;
        }

        public String getName() {
            return this.name.get();
        }

        public void setName(String name) {
            this.name.set(name);
        }

    }

    public class SearchBox extends Region {

        public TextField textBox;
        public Button clearButton;

        public SearchBox() {
            setId("SearchBox");
            getStyleClass().add("client.search-box");
            setMinHeight(24);
            setPrefSize(200, 24);
            setMaxSize(Control.USE_PREF_SIZE, Control.USE_PREF_SIZE);
            textBox = new TextField();
            textBox.setPromptText("Search new friend");
            clearButton = new Button();
            clearButton.setVisible(false);
            getChildren().addAll(textBox, clearButton);
            clearButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    textBox.setText("");
                    textBox.requestFocus();
                }
            });
            textBox.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    clearButton.setVisible(textBox.getText().length() != 0);

                    updateResults(newValue);
                }

            });
        }

        @Override
        protected void layoutChildren() {
            textBox.resize(getWidth(), getHeight());
            clearButton.resizeRelocate(getWidth() - 18, 6, 12, 13);
        }

        public String getText() {
            return this.textBox.getText();
        }
    }

}
