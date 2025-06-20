package chatapp.client.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.ArrayList;

public class HelloController {
    @FXML
    private TextField messageTextField;
    @FXML Label messagesLabel;

    private boolean startOfConversation = true;
    private int maxMessages = 4;
    private ArrayList<String> messages = new ArrayList<>();


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