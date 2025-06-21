package chatapp.client;

import chatapp.client.controller.HelloController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        FXMLLoader fxmlLoaderLogging = new FXMLLoader(HelloApplication.class.getResource("/chatapp/client/views/logging.fxml"));
        //Scene scene = new Scene(fxmlLoader.load(), 400, 240);
        Scene sceneLogging = new Scene(fxmlLoaderLogging.load(), 400, 240);
        //scene.getStylesheets().add("style.css");
        sceneLogging.getStylesheets().add("/chatapp/client/styles/style.css");
        stage.setTitle("Chatterly");
        stage.setScene(sceneLogging);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}