package chatapp.client.controller;

import chatapp.client.HelloApplication;
import chatapp.client.model.Friend;

import chatapp.client.storage.SQLiteManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class friendRequestsController
{
    @FXML private VBox personList;

    private ArrayList<Friend> people = new ArrayList<>();

    private void loadWindow(ActionEvent event, String window, double width, double height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(window));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        scene.getStylesheets().add("/chatapp/client/styles/style.css");
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chatterly");
        stage.setScene(scene);
        stage.show();

    }

    EventHandler<ActionEvent> acceptRequestHandler = new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e)
        {
            Node node = (Node) e.getSource();
            String username = node.getId();
            VBox p = (VBox)(node.getParent().getParent());
            p.getChildren().remove(node.getParent());
            try {
                SQLiteManager s = SQLiteManager.getInstance();
                s.upsertFriend(username, "publicKey");
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    };

    private void loadPeople() {
        for (Friend friend : people) {
            HBox h = new HBox();
            h.setSpacing(20);
            h.getChildren().add(new Label(friend.getUsername()));
            Button b = new Button("Zaakceptuj zaproszenie");
            b.setId(friend.getUsername());
            b.setOnAction(acceptRequestHandler);
            h.getChildren().add(b);
            personList.getChildren().add(h);
        }
    }

    @FXML
    public void initialize() {
        //test
        people.add(new Friend("stachu" ,"publicKey"));
        people.add(new Friend("stachu2", "publicKey"));
        //
        loadPeople();
    }

    @FXML
    protected void goBack(ActionEvent event) throws IOException
    {
        loadWindow(event, "/chatapp/client/views/hello-view.fxml", 400, 300);
    }
}
