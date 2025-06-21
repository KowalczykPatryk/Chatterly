package chatapp.client.controller;

import chatapp.client.api.UserApi;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class HelloController {
    @FXML
    private TextField messageTextField;
    @FXML Label messagesLabel;
    @FXML
    ComboBox<String> chatComboBox;

    private boolean startOfConversation = true;
    private int maxMessages = 4;
    private ArrayList<String> messages = new ArrayList<>();
    private ArrayList<String> chatsTest = new ArrayList<>(); //FOR TEST PURPOSE ONLY


    private int max(int a, int b) { return (a > b) ? a : b; }

    @FXML
    public void initialize() {
        chatComboBox.getItems().addAll("aa", "bb", "cc");
    }
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

    @FXML
    protected void chatChanged(ActionEvent event) {
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chatterly: " + chatComboBox.getValue());
    }
}