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

public class AddFriendsController
{
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
    protected void goBack(ActionEvent event) throws IOException
    {
        loadWindow(event, "/chatapp/client/views/hello-view.fxml", 400, 300);
    }
    @FXML
    protected void search(ActionEvent event) throws IOException
    {

    }
}
