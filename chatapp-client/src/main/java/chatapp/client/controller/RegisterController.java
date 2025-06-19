package chatapp.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import chatapp.client.HelloApplication;

import chatapp.client.http.HttpService;
import chatapp.client.service.UserServiceClient;
import chatapp.client.dto.RegisterRequest;
import chatapp.client.dto.RegisterResponse;
import chatapp.client.model.ApiResponse;
import jakarta.ws.rs.core.Response;

import java.io.IOException;

public class RegisterController {
    @FXML private TextField usernameTextField;
    @FXML private TextField passwordTextField;
    @FXML private Label infoLabel;

    private void loadWindow(ActionEvent event, String window) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(window));
        Scene scene = new Scene(fxmlLoader.load(), 400, 240);
        scene.getStylesheets().add("/chatapp/client/styles/style.css");
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chatterly");
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    protected void createAccount(ActionEvent event) throws IOException {
        if(usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
            infoLabel.setText("Uzupełnij wszystkie pola!");
            infoLabel.setTextFill(Color.ORANGE);
        }
        else {
            HttpService httpService = new HttpService();
            String baseUrl = "http://localhost:8081/api/users";
            UserServiceClient userClient = new UserServiceClient(httpService, baseUrl);
            RegisterRequest regReq = new RegisterRequest(usernameTextField.getText(), passwordTextField.getText(), "kluczPub");
            ApiResponse<RegisterResponse> resp = userClient.register(regReq);
            httpService.close();

            if (resp.getStatus() == Response.Status.CREATED.getStatusCode()) {
                loadWindow(event, "/chatapp/client/views/logging.fxml");
            }
            else if (resp.getStatus() == Response.Status.CONFLICT.getStatusCode()) {
                RegisterResponse regResp = resp.getBody();
                infoLabel.setText(regResp.getMessage());
                infoLabel.setTextFill(Color.RED);
            }
        }
    }
}