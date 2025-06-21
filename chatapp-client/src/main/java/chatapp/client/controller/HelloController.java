package chatapp.client.controller;

import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

import chatapp.client.HelloApplication;

import java.util.ArrayList;

public class HelloController {
    private void loadWindow(ActionEvent event, String window, double width, double height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(window));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        scene.getStylesheets().add("/chatapp/client/styles/style.css");
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chatterly");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void addFriends(ActionEvent event) throws IOException
    {
        loadWindow(event, "/chatapp/client/views/addFriends.fxml", 400, 300);
    }


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