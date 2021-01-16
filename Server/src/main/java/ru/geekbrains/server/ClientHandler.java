package ru.geekbrains.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Locale;

public class ClientHandler {

    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private Server server;
    private String nickName;

    public String getNickName() {
        return nickName;
    }


    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        new Thread(()->{
            try {
                while (true) {
                    String msg = in.readUTF();
                    System.out.println("Сообщение от клиента: " + msg + "\n");
                    if (msg.startsWith("/auth ")) {
                        String[] token = msg.split(" ", 3);
                        String nickFromAuthManager = server.getAuthManager().getNickNameByLoginAndPassword(token[1], token[2]);
                        if (nickFromAuthManager != null) {
                            if (server.isNickBusy(nickFromAuthManager)){
                                sendMsg("Данный пользователь уже в чате");
                                continue;
                            }
                            nickName = nickFromAuthManager;
                            server.subscribe(this);
                            sendMsg("/authok " + nickName);
                            server.broadcastMsg(nickName + " " + "зашел в чат" + "\n");
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
                            server.broadcastMsg(nickName + " " + "вышел из чата" + "\n");
                            sendMsg("end_confirm");
                            break;
                        }
                            String temp = msg.split(" ")[1];
                        System.out.println(temp);
                        if (msg.startsWith("/w " + temp)) {
                            String name = msg.split(" ")[1];
                            msg = msg.replaceAll("/w", "");
                            msg = msg.replaceAll(name, "");
                            msg = msg.replaceAll("  ", " ");
                            server.unicastMesage(nickName + ": " + "wisp" + msg, name);
                        }


                    }else {
                        server.broadcastMsg(nickName + ": " + msg);
                    }
                }
            }catch (IOException e){
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

