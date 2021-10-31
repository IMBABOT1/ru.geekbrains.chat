package sample;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    TextArea mainArea;

    @FXML
    TextField messageField;

    public void sendMessage(ActionEvent actionEvent) {
        if (messageField.getText().trim().length() > 0) {
            mainArea.appendText(messageField.getText() + '\n');
            messageField.clear();
            messageField.requestFocus();
        }
    }
}
