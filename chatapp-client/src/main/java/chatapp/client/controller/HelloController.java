package chatapp.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HelloController {
    @FXML
    private TextField messageTextField;
    @FXML Label messagesLabel;

    private boolean startOfConversation = true;

    @FXML
    protected void sendMessage() {
        if(startOfConversation) {
            messagesLabel.setText("");
            startOfConversation = false;
        }
        String message = messageTextField.getText();
        messagesLabel.setText(messagesLabel.getText() + "\n" + message);
        messageTextField.setText("");
    }
    @FXML
    protected void onTextFieldClick() {  }
}