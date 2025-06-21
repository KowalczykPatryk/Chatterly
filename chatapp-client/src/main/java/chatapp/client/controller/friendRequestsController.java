package chatapp.client.controller;

import chatapp.client.HelloApplication;
import chatapp.client.model.Friend;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class friendRequestsController
{
    @FXML private VBox personList;

    private ArrayList<Friend> people = new ArrayList<>();

    private void loadWindow(ActionEvent event, String window, double width, double height) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(window));
        Scene scene = new Scene(fxmlLoader.load(), width, height);
        scene.getStylesheets().add("/chatapp/client/styles/style.css");
        Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chatterly");
        stage.setScene(scene);
        stage.show();

    }

    private void loadPeople() {
        for (Friend friend : people) {
            HBox h = new HBox();
            h.setSpacing(20);
            h.getChildren().add(new Label(friend.getUsername()));
            h.getChildren().add(new Button("Zaakceptuj zaproszenie"));
            personList.getChildren().add(h);
        }
    }

    @FXML
    public void initialize() {
        //test
        people.add(new Friend("stachu"));
        people.add(new Friend("stachu2"));
        //
        loadPeople();
    }

    @FXML
    protected void goBack(ActionEvent event) throws IOException
    {
        loadWindow(event, "/chatapp/client/views/hello-view.fxml", 400, 300);
    }
}
