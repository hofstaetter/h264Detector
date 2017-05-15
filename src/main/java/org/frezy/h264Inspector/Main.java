package main.java.org.frezy.h264Inspector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by matthias on 05.05.17.
 */

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Home.fxml"));
        primaryStage.setTitle("h264Inspector");
        primaryStage.setScene(new Scene(root, 1650, 1080));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}