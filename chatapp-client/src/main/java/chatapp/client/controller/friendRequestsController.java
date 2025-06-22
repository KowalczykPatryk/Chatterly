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
import java.util.List;
import chatapp.client.service.FriendServiceClient;

public class friendRequestsController
{
    @FXML private VBox personList;

    private List<String> people = new ArrayList<>();
    private FriendServiceClient friendServiceClient = new FriendServiceClient();

    private void loadWindow(ActionEvent event, String window, double width, double height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(window));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        scene.getStylesheets().add("/chatapp/client/styles/style.css");
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chatterly");
        stage.setScene(scene);
        stage.show();

    }

    private final EventHandler<ActionEvent> invitationRequestHandler = event -> {
        Button btn = (Button) event.getSource();
        String username = btn.getId();
        String action = (String) btn.getUserData();

        HBox h = (HBox)(btn.getParent());
        List<Node> toRemove = new ArrayList<>();
        for (Node node : h.getChildren()) {
            if (node instanceof Button) {
                ((Button) node).setDisable(true);
                if (node != btn)
                {
                    toRemove.add(node);
                }
            }
        }
        h.getChildren().removeAll(toRemove);

        if ("accept".equals(action)) {
            handleAccept(username, btn);
        } else {
            handleReject(username, btn);
        }
    };
    private void handleAccept(String username, Button accept) {
        if (friendServiceClient.respondToFriendRequest(username, true)) {
            accept.setText("Accepted");
        }
    }
    private void handleReject(String username, Button reject) {
        if (friendServiceClient.respondToFriendRequest(username, false)) {
            reject.setText("Rejected");
        }
    }

    private void loadPeople() {
        for (String username : people) {
            HBox h = new HBox();
            h.setSpacing(20);
            h.getChildren().add(new Label(username));
            Button accept = new Button("Accept");
            Button reject = new Button("Reject");
            accept.setId(username);
            accept.setUserData("accept");
            reject.setId(username);
            reject.setUserData("reject");
            accept.setOnAction(invitationRequestHandler);
            reject.setOnAction(invitationRequestHandler);
            h.getChildren().addAll(accept, reject);
            personList.getChildren().add(h);
        }
    }

    @FXML
    public void initialize() {
        people = friendServiceClient.getFriendshipRequests();
        loadPeople();
    }

    @FXML
    protected void goBack(ActionEvent event) throws IOException
    {
        loadWindow(event, "/chatapp/client/views/hello-view.fxml", 400, 300);
    }
}
