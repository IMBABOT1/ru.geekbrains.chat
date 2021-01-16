package ru.geekbrains.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

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
    TextField msgField, loginField;

    @FXML
    PasswordField passwordField;

    @FXML
    HBox loginBox;

    private Network network;
    private boolean authenticated;
    private String nickname;


    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        loginBox.setVisible(!authenticated);
        loginBox.setManaged(!authenticated);
        msgField.setVisible(authenticated);
        msgField.setManaged(authenticated);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
             try {
                 setAuthenticated(false);
                 network = new Network(8189);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                String msg = network.readMsg();
                                if (msg.startsWith("/authok ")){
                                   nickname = msg.split(" ")[1];
                                   setAuthenticated(true);
                                   break;
                                }
                                textArea.appendText(msg + "\n");
                            }
                            while (true) {
                                String msg = network.readMsg();
                                if (msg.equals("end_confirm")){
                                    textArea.appendText("Сервер прекратил работу");
                                    break;
                                }
                                textArea.appendText(msg + "\n");
                            }
                        }catch (IOException e){
                            Platform.runLater(()-> {
                                Alert alert = new Alert(Alert.AlertType.WARNING, "Соединение с сервером разорванно", ButtonType.OK);
                                alert.showAndWait();
                            });
                        }finally {
                            network.close();
                            Platform.exit();
                        }
                    }
                });
                t.setDaemon(true);
                t.start();
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

    public void tryToAuth(ActionEvent actionEvent) {
        try {
            network.sendMst("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        }catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось подключиться к серверу", ButtonType.OK);
            alert.showAndWait();
        }
    }
}
