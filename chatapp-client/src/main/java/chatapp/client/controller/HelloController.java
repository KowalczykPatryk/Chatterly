package chatapp.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HelloController {
    @FXML
    private TextField messageTextField;
    @FXML Label messagesLabel;

    @FXML
    protected void sendMessage() {
        messageTextField.setText("");
    }
    @FXML
    protected void onTextFieldClick() {  }
}