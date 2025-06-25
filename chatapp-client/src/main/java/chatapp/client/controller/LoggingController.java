package chatapp.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import chatapp.client.HelloApplication;
import chatapp.client.model.MyUsername;
import chatapp.client.storage.SQLiteManager;

import chatapp.client.http.HttpService;
import chatapp.client.service.UserServiceClient;
import chatapp.client.dto.LoginRequest;
import chatapp.client.dto.LoginResponse;
import chatapp.client.model.ApiResponse;
import chatapp.client.model.MyUsername;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.sql.SQLException;

public class LoggingController {
    @FXML private TextField loginTextField;
    @FXML private PasswordField passwordTextField;
    @FXML private Label infoLabel;

    private static final String BASE = "http://localhost:8081/api/users";

    //loading another window of an app
    private void loadWindow(ActionEvent event, String window) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(window));
        Scene scene = new Scene(fxmlLoader.load(), 400, 300);
        scene.getStylesheets().add("/chatapp/client/styles/style.css");
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chatterly");
        stage.setScene(scene);
        stage.show();
    }

    //logging in
    @FXML
    protected void logIn(ActionEvent event) throws IOException {
        if(loginTextField.getText().isEmpty()) {
            infoLabel.setText("Wpisz nazwę użytkownika!");
            infoLabel.setTextFill(Color.ORANGE);
        }
        else if (passwordTextField.getText().isEmpty()) {
            infoLabel.setText("Wpisz hasło!");
            infoLabel.setTextFill(Color.ORANGE);
        }
        else {
            HttpService httpService = new HttpService();
            String baseUrl = "http://localhost:8081/api/users";
            UserServiceClient userClient = new UserServiceClient(httpService, baseUrl);
            LoginRequest logReq = new LoginRequest(loginTextField.getText(), passwordTextField.getText());
            ApiResponse<LoginResponse> resp = userClient.login(logReq);
            LoginResponse logResp = resp.getBody();
            if (resp.getStatus() == Response.Status.OK.getStatusCode())
            {
                try {
                    SQLiteManager manager = SQLiteManager.getInstance();
                    manager.saveTokens(loginTextField.getText(), logResp.getAccessToken(), logResp.getRefreshToken());
                    MyUsername.setMyUsername(loginTextField.getText());
                    loadWindow(event, "/chatapp/client/views/hello-view.fxml");
                }
                catch(SQLException e)
                {
                    System.out.println(e.getMessage());
                    infoLabel.setText("Could not save tokens to the database.");
                    infoLabel.setTextFill(Color.RED);
                }
            }
            else if (resp.getStatus() == Response.Status.UNAUTHORIZED.getStatusCode())
            {
                infoLabel.setText(logResp.getMessage());
                infoLabel.setTextFill(Color.RED);
            }
        }
    }

    //load the window for registration
    @FXML
    protected void register(ActionEvent event) throws IOException {
        loadWindow(event, "/chatapp/client/views/register.fxml");
    }
}