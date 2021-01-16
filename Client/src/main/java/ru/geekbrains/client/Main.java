package ru.geekbrains.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class Main extends Application  {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
        primaryStage.setTitle("Client");
        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.show();

    }

    @FXML
    public void exitApplication() {
        Platform.exit();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
