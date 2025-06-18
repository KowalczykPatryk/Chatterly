package chatapp.client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import chatapp.client.model.User;

import chatapp.client.HelloApplication;

import java.io.IOException;

public class LoggingController {
    @FXML private TextField loginTextField;
    @FXML private PasswordField passwordTextField;

    private static final String BASE = "http://localhost:8081/api/users";

    @FXML
    protected void logIn(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/chatapp/client/views/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 240);
        scene.getStylesheets().add("/chatapp/client/styles/style.css");
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chatterly");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void register(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/chatapp/client/views/register.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 240);
        scene.getStylesheets().add("/chatapp/client/styles/style.css");
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chatterly");
        stage.setScene(scene);
        stage.show();

    }
}