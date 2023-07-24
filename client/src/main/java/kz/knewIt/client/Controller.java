package kz.knewIt.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller {
    @FXML
    TextField msgField, usernameField;

    @FXML
    TextArea mainArea;

    @FXML
    HBox loginPanel, msgPanel;

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;


    public void setUsername(String username) {
        this.username = username;
        if(username != null) {
            loginPanel.setVisible(false);
            loginPanel.setManaged(false);
            msgPanel.setVisible(true);
            msgPanel.setManaged(true);
        } else {
            loginPanel.setVisible(true);
            loginPanel.setManaged(true);
            msgPanel.setVisible(false);
            msgPanel.setManaged(false);
        }
    }

    public void login() {
        if(socket == null || socket.isClosed()) {
            connect();
        }

        if(usernameField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Имя пользователя не должно быть пустым", ButtonType.OK);
            alert.showAndWait();
        }

        try {
            out.writeUTF("/login " + usernameField.getText());
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        try {
            socket = new Socket("localhost", 8189);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            Thread t1 = new Thread(() -> {
                try {
                    // цикл авторизации
                    while (true) {
                        String msg = in.readUTF();

                        // client -> server /login Bob
                        // server -> client /login_ok Bob
                        if(msg.startsWith("/login_ok ")) {
                            setUsername(msg.split("\\s")[1]);
                            break;
                        }

                        if(msg.startsWith("/login_failed ")) {
                            // server -> client /login_failed because this nick is already in use
                            String cause = msg.split("\\s", 2)[1];
                            mainArea.appendText(cause + "\n");
                        }

                    }
                    // цикл общения
                    while (true) {
                        String msg = in.readUTF();
                        mainArea.appendText(msg + "\n");
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    disconnected();

                }
            });
            t1.start();


        }catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Невозможно подключиться к серверу 8189");
            alert.showAndWait();
        }
    }



    public void sendMsg() {
        try {

            out.writeUTF(msgField.getText());
            msgField.clear();
            msgField.requestFocus();
        }catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Невозможно отправить сообщение", ButtonType.OK);
            alert.showAndWait();
        }
    }


    public void disconnected() {
        setUsername(null);
        try {
            if(socket != null) {
                socket.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}