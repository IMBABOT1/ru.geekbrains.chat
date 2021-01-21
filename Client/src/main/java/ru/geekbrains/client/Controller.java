package ru.geekbrains.client;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import ru.geekbrains.server.ClientHandler;
import ru.geekbrains.server.Server;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
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

    @FXML
    ListView clientsList;

    private Network network;
    private boolean authenticated;
    private String nickname;

    private ArrayList<String> result;
    private List<ClientHandler> clients;
    private double scrollPosition = 0;





    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
        loginBox.setVisible(!authenticated);
        loginBox.setManaged(!authenticated);
        textArea.setVisible(authenticated);
        msgField.setVisible(authenticated);
        msgField.setManaged(authenticated);
        clientsList.setVisible(authenticated);
        clientsList.setManaged(authenticated);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clients = new ArrayList<>();
        result = new ArrayList<>();
        setAuthenticated(false);
        clientsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2){
                    msgField.setText("/w " + clientsList.getSelectionModel().getSelectedItem() + " ");
                    msgField.requestFocus();
                    msgField.selectEnd();

                }
            }
        });
        readLog();
        textArea.appendText(writeLog() + "\n");
    }

    public void tryToConnect(){
        try {
            if (network != null && network.isConnected()){
                return;
            }
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
                            if (msg.startsWith("/")) {
                                if (msg.equals("end_confirm")) {
                                    textArea.appendText("Сервер прекратил работу");
                                    break;
                                }
                                if (msg.startsWith("/clients_list ")) {
                                    Platform.runLater(() -> {
                                        clientsList.getItems().clear();
                                        String[] tokens = msg.split(" ");
                                        for (int i = 1; i < tokens.length; i++) {
                                            clientsList.getItems().add(tokens[i]);
                                        }
                                    });
                                }
                            }else {
                                textArea.appendText(msg + "\n");
                            }
                        }
                    }catch (IOException e){
                        Platform.runLater(()-> {
                            Alert alert = new Alert(Alert.AlertType.WARNING, "Соединение с сервером разорванно", ButtonType.OK);
                            alert.showAndWait();
                        });
                    }finally {
                        network.close();
                        setAuthenticated(false);
                        nickname = null;
                        // textArea.clear();
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        }catch (IOException e){
            Platform.runLater(()-> {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Невозможно подключиться к серверу", ButtonType.OK);
                alert.showAndWait();
            });
        }
    }

    public List<String> readLog() {
        try (BufferedReader br = new BufferedReader(new FileReader("log.txt"))) {
            while (br.ready()) {
                result.add(br.readLine() + "\t");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }


    public String writeLog(){
        StringBuilder sb = new StringBuilder();
        for (int i = result.size() - 1; i >= result.size() - 100 ; i--) {
            sb.append(result.get(i) + "\n");
        }

        return sb.toString() + "\n";
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
            tryToConnect();
            network.sendMst("/auth " + loginField.getText() + " " + passwordField.getText());
            loginField.clear();
            passwordField.clear();
        }catch (IOException e){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Не удалось подключиться к серверу", ButtonType.OK);
            alert.showAndWait();
        }
    }
}