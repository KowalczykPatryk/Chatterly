package chatapp.client.controller;

import chatapp.client.model.Friend;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import chatapp.client.HelloApplication;
import chatapp.client.service.FriendServiceClient;
import chatapp.client.storage.SQLiteManager;
import chatapp.client.dto.LogoutRequest;
import chatapp.client.dto.LogoutResponse;
import chatapp.client.model.MyUsername;

import java.util.ArrayList;
import java.util.List;

import chatapp.client.http.HttpService;
import chatapp.client.service.UserServiceClient;
import chatapp.client.dto.LoginRequest;
import chatapp.client.dto.LoginResponse;
import chatapp.client.model.ApiResponse;
import jakarta.ws.rs.core.Response;

public class HelloController {
    private FriendServiceClient friendServiceClient = new FriendServiceClient();
    private boolean startOfConversation = true;
    private int maxMessages = 5;
    private List<String> messages = new ArrayList<>();
    private List<Friend> friends = new ArrayList<>();

    @FXML private TextField messageTextField;
    @FXML Label messagesLabel;
    @FXML ComboBox friendComboBox;


    private void loadWindow(ActionEvent event, String window, double width, double height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(window));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        scene.getStylesheets().add("/chatapp/client/styles/style.css");
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chatterly");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void addFriends(ActionEvent event) throws IOException
    {
        loadWindow(event, "/chatapp/client/views/addFriends.fxml", 400, 300);
    }

    @FXML
    protected void friendRequests(ActionEvent event) throws IOException {
        loadWindow(event, "/chatapp/client/views/friendRequests.fxml", 400, 300);
    }
    @FXML
    protected void logout(ActionEvent event) throws IOException
    {
        try {
            HttpService httpService = new HttpService();
            String baseUrl = "http://localhost:8081/api/users";
            UserServiceClient userClient = new UserServiceClient(httpService, baseUrl);
            SQLiteManager dbManager = SQLiteManager.getInstance();
            LogoutRequest logReq = new LogoutRequest(dbManager.getRefreshToken(MyUsername.getMyUsername()));
            ApiResponse<LogoutResponse> resp = userClient.logout(logReq);
            LogoutResponse logResp = resp.getBody();
            if (logResp.getMessage().equals("You were properly logout.")) {
                loadWindow(event, "/chatapp/client/views/logging.fxml", 400, 300);
            }
        } catch(SQLException e) {}
    }

    @FXML
    public void initialize() {
        try {
            friends = friendServiceClient.getFriends();
            for(Friend f : friends) {
                friendComboBox.getItems().add(f.getUsername());
            }
        }
        catch (Exception e) {

        }
    }

    private int max(int a, int b) { return (a > b) ? a : b; }
    @FXML
    protected void sendMessage() {
        if(startOfConversation) {
            messagesLabel.setText("");
            startOfConversation = false;
        }
        String message = messageTextField.getText();
        messages.add(message);

        String msgLabelText = "";
        for(int i = max(messages.size() - maxMessages, 0); i < messages.size(); i++) {
            msgLabelText += messages.get(i) + "\n";
        }
        messagesLabel.setText(msgLabelText);
        messageTextField.setText("");
    }
    @FXML
    protected void onTextFieldClick() {  }
}