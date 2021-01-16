package ru.geekbrains.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    TextArea textArea;

    @FXML
    TextField msgField;

    private Network network;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
             try {
                 network = new Network(8189);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                String msg = network.readMsg();
                                textArea.appendText(msg + "\n");
                            }
                        }catch (IOException e){
                            Platform.runLater(()-> {
                                Alert alert = new Alert(Alert.AlertType.WARNING, "Соединение с сервером разорванно", ButtonType.OK);
                                alert.showAndWait();
                            });
                        }finally {
                            network.close();
                        }
                    }
                }).start();
        }catch (IOException e){
            throw new RuntimeException("Невозможно подключиться к серверу");
        }
    }


    public void sendMsg(ActionEvent actionEvent) {
        try {
            if (msgField.getText().trim().length() > 0) {
                network.sendMst(msgField.getText());
                msgField.clear();
                msgField.requestFocus();
            }
        }catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось подключиться к серверу", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
