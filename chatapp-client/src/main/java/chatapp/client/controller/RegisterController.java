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

import java.io.IOException;

public class RegisterController {
    @FXML private TextField usernameTextField;
    @FXML private TextField passwordTextField;
    @FXML private Label infoLabel;

    @FXML
    protected void createAccount(ActionEvent event) throws IOException {
        if(usernameTextField.getText().isEmpty() || passwordTextField.getText().isEmpty()) {
            infoLabel.setText("Uzupełnij wszystkie pola!");
            infoLabel.setTextFill(Color.ORANGE);
        }
        else {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/chatapp/client/views/hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 300);
            scene.getStylesheets().add("/chatapp/client/styles/style.css");
            Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Chatterly");
            stage.setScene(scene);
            stage.show();
        }
    }
}