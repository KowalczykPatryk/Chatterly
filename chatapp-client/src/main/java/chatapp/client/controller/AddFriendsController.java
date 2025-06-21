package chatapp.client.controller;

import chatapp.client.model.Friend;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

import chatapp.client.HelloApplication;
import chatapp.client.storage.SQLiteManager;

public class AddFriendsController
{
    @FXML private VBox personList;
    @FXML private TextField searchUsernamesTextField;

    private List<String> usernames = new ArrayList<>();

    private void loadWindow(ActionEvent event, String window, double width, double height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(window));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        scene.getStylesheets().add("/chatapp/client/styles/style.css");
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chatterly");
        stage.setScene(scene);
        stage.show();

    }

    private void loadUsernames() {
        personList.getChildren().clear();
        for (String username : usernames) {
            HBox h = new HBox();
            h.setSpacing(20);
            h.getChildren().add(new Label(username));
            h.getChildren().add(new Button("Wyślij zaproszenie"));
            personList.getChildren().add(h);
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
            String accessToken = manager.getAccessToken();

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
