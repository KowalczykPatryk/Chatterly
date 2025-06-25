package chatapp.client.controller;

import chatapp.client.model.Friend;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;
import java.sql.SQLException;
import java.net.URI;
import java.util.stream.Collectors;

import chatapp.client.HelloApplication;
import chatapp.client.service.FriendServiceClient;
import chatapp.client.storage.SQLiteManager;
import chatapp.client.dto.LogoutRequest;
import chatapp.client.dto.LogoutResponse;
import chatapp.client.model.MyUsername;
import chatapp.client.http.HttpService;
import chatapp.client.service.UserServiceClient;
import chatapp.client.model.ApiResponse;
import chatapp.client.websocket.ChatClient;

public class HelloController {
    private static final int MAX_MESSAGES = 4;

    private final FriendServiceClient friendServiceClient = new FriendServiceClient();
    private ChatClient client;
    private String currentFriend;

    private final ObservableList<String> messageList = FXCollections.observableArrayList();
    private final ObservableList<String> friendList  = FXCollections.observableArrayList();

    @FXML private TextField messageTextField;
    @FXML private ListView<String> messagesListView;
    @FXML private ComboBox<String>  friendComboBox;

    @FXML
    public void initialize() {
        messagesListView.setItems(messageList);
        refreshFriendsList();
        friendComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldF, newF) -> {
            if (newF != null && !newF.equals(currentFriend)) {
                switchConversationTo(newF);
            }
        });
    }

    //refreshing the list of friends
    private void refreshFriendsList() {
        try {
            friendList.setAll(
                    friendServiceClient.getFriends()
                            .stream()
                            .map(Friend::getUsername)
                            .collect(Collectors.toList())
            );
            friendComboBox.setItems(friendList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //changing conversation partner
    private void switchConversationTo(String friend) {
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentFriend = friend;
        messageList.clear();
        messagesListView.scrollTo(0);

        try {
            client = new ChatClient(plainText -> {
                Platform.runLater(() -> {
                    if (messageList.size() == MAX_MESSAGES) {
                        messageList.remove(0);
                    }
                    messageList.add(plainText);
                    messagesListView.scrollTo(messageList.size() - 1);
                });
            });
            client.openChatWebSocket(friend);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //sending a message
    @FXML
    protected void sendMessage() {
        String text = messageTextField.getText();
        if (text == null || text.isBlank() || client == null) {
            return;
        }
        try {
            client.sendMessage(text);
            if (messageList.size() == MAX_MESSAGES) {
                messageList.remove(0);
            }
            messageList.add(text);
            messagesListView.scrollTo(messageList.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            messageTextField.clear();
        }
    }

    //adding new friend
    @FXML
    protected void addFriends(ActionEvent event) throws IOException {
        loadWindow(event, "/chatapp/client/views/addFriends.fxml", 400, 300);
    }

    //opening a window with friend requests
    @FXML
    protected void friendRequests(ActionEvent event) throws IOException {
        loadWindow(event, "/chatapp/client/views/friendRequests.fxml", 400, 300);
    }

    //logging out
    @FXML
    protected void logout(ActionEvent event) throws IOException {
        try {
            HttpService httpService = new HttpService();
            String baseUrl = "http://localhost:8081/api/users";
            UserServiceClient userClient = new UserServiceClient(httpService, baseUrl);
            SQLiteManager dbManager = SQLiteManager.getInstance();
            LogoutRequest logReq = new LogoutRequest(dbManager.getRefreshToken(MyUsername.getMyUsername()));
            ApiResponse<LogoutResponse> resp = userClient.logout(logReq);
            LogoutResponse logResp = resp.getBody();
            if ("You were properly logout.".equals(logResp.getMessage())) {
                loadWindow(event, "/chatapp/client/views/logging.fxml", 400, 300);
            }
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    //loading another window of an app
    private void loadWindow(ActionEvent event, String window, double width, double height) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(window));
        Scene scene = new Scene(loader.load(), width, height);
        scene.getStylesheets().add("/chatapp/client/styles/style.css");
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chatterly");
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    protected void onTextFieldClick(javafx.scene.input.MouseEvent event) {
        // can be used to clear placeholder or prepare UI
    }

    @FXML
    protected void handleKeyPress(javafx.scene.input.KeyEvent event) {
        switch (event.getCode()) {
            case ENTER:
                sendMessage();
                break;
            default:
                break;
        }
    }
}
