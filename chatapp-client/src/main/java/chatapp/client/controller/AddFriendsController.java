package chatapp.client.controller;

import chatapp.client.model.Friend;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import chatapp.client.http.HttpService;
import chatapp.client.service.UserServiceClient;
import chatapp.client.dto.GetUsernamesRequest;
import chatapp.client.dto.GetUsernamesResponse;
import chatapp.client.model.ApiResponse;
import chatapp.client.model.MyUsername;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

import chatapp.client.HelloApplication;
import chatapp.client.storage.SQLiteManager;
import chatapp.client.service.FriendServiceClient;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class AddFriendsController
{
    @FXML private VBox personList;
    @FXML private TextField searchUsernamesTextField;

    private List<String> usernames = new ArrayList<>();
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

    private final EventHandler<ActionEvent> inviteHandler = event -> {
        Button btn = (Button) event.getSource();
        String userToInvite = btn.getId();
        sendInvitationTo(userToInvite, btn);
    };

    private void loadUsernames() {
        personList.getChildren().clear();
        for (String username : usernames) {
            HBox h = new HBox();
            h.setSpacing(20);
            h.getChildren().add(new Label(username));
            String status = friendServiceClient.getFriendshipStatus(username);
            Button b;
            if (status.equals("Not a friend")) {
                b = new Button("Send invitation");
            }
            else if(status.equals("accepted")){
                b = new Button("Your friend");
                b.setDisable(true);
            }
            else if(status.equals("pending")){
                b = new Button("Waiting for acceptation");
                b.setDisable(true);
            }
            else if(status.equals("rejected")){
                b = new Button("Your invitation was rejected");
            }
            else
            {
                b = new Button("Sth unpredicted happened");
                b.setDisable(true);
            }
            b.setId(username);
            b.setOnAction(inviteHandler);
            h.getChildren().add(b);
            personList.getChildren().add(h);
        }
    }
    private void sendInvitationTo(String username, Button btn) {
        if (friendServiceClient.sendInvitationTo(username)) {
            btn.setDisable(true);
            btn.setText("Wysłano zaproszenie");
        }
    }

    @FXML
    public void initialize() {
        loadUsernames();
    }

    @FXML
    protected void goBack(ActionEvent event) throws IOException
    {
        loadWindow(event, "/chatapp/client/views/hello-view.fxml", 400, 300);
    }
    @FXML
    protected void search(ActionEvent event) throws IOException
    {
        try {
            SQLiteManager manager = SQLiteManager.getInstance();
            String accessToken = manager.getAccessToken(MyUsername.getMyUsername());

            HttpService httpService = new HttpService();
            String baseUrl = "http://localhost:8081/api/users";
            UserServiceClient userClient = new UserServiceClient(httpService, baseUrl);
            GetUsernamesRequest namesReq = new GetUsernamesRequest(accessToken, searchUsernamesTextField.getText());
            ApiResponse<GetUsernamesResponse> resp = userClient.getUsernames(namesReq);
            if (resp.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode())
            {
                System.out.println("Access token has expired.");
            }
            else if (resp.getStatus() == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
            {
                System.out.println("Sth went wrong with accessing database.");
            }
            else {
                GetUsernamesResponse getResp = resp.getBody();
                usernames = getResp.getUsernames();
            }
        } catch (SQLException e) {}
        loadUsernames();
    }
}
