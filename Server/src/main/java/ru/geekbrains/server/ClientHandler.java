package ru.geekbrains.server;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientHandler {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private Log log;
    private List<String> list;


    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    private String nickName;


    public String getNickName() {
        return nickName;
    }


    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.list = new ArrayList<>();
        new Thread(()->{
            try {
                while (true) {
                    String msg = in.readUTF();
                    System.out.println("Сообщение от клиента: " + msg + "\n");
                    if (msg.startsWith("/auth ")) {
                        String[] token = msg.split(" ", 3);
                        String nickFromAuthManager = server.getAuthManager().getNickNameByLoginAndPassword(token[1], token[2]);
                        this.log = new Log(new File("history_" + nickFromAuthManager + "." + "txt"));
                        if (nickFromAuthManager != null) {
                            if (server.isNickBusy(nickFromAuthManager)){
                                sendMsg("Данный пользователь уже в чате");
                                continue;
                            }
                            nickName = nickFromAuthManager;
                            sendMsg("/authok " + nickName);
                            server.subscribe(this);
                            server.broadcastMsg(nickName + " " + "зашел в чат" + "\n", true);

                            server.readLog();
                            server.writeLog1();
                            server.writeLog2();
                            server.writeLog3();
                            server.writeLog4();

                            break;
                        }else {
                            sendMsg("Указан неверный логин/пароль");
                        }
                    }
                }
                while (true){
                    String msg = in.readUTF();
                    System.out.println("Сообщение от клиента: " + msg + "\n");
                    if (msg.startsWith("/")){
                        if (msg.equals("/end")){
                            server.broadcastMsg(nickName + " " + "вышел из чата" + "\n", true);
                            sendMsg("end_confirm");
                            break;
                        }
                        String nick = msg.split(" ")[1];
                        if (msg.equals("/change_nick " + nick)){
                            server.getAuthManager().changeNickname(nickName, nick);
                            server.changeNick(nickName, nick);
                        }

                        String temp = msg.split(" ")[1];
                        if (msg.startsWith("/w " + temp)) {
                            String name = msg.split(" ")[1];
                            msg = msg.replaceAll("/w", "");
                            msg = msg.replaceAll(name, "");
                            msg = msg.replaceAll("  ", " ");
                            server.unicastMesage(nickName + " " + "wisp:" + msg, name);
                            server.unicastMesage(nickName +  "" + " wisp to " + name + ":" + " " + msg,  nickName);
                        }


                    }else {
                        server.broadcastMsg(nickName + ": " + msg, true);
                        log.read(msg);

                    }
                }
            }catch (IOException | SQLException e){
                e.printStackTrace();
            }finally {
                close();
            }
        }).start();
    }




    public void sendMsg(String msg){
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void close(){
        server.unsubscribe(this);
        nickName = null;
        if(in != null){
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(out != null){
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(socket != null){
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}