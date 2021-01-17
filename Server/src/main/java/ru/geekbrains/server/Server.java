package ru.geekbrains.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private List<ClientHandler> clients;

    public AuthManager getAuthManager() {
        return authManager;
    }

    private AuthManager authManager;
    private final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Server(int port) {
        clients = new ArrayList<>();
        authManager = new BasicAuthManager();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server start on port: " + port + " waiting for clients: ");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connect");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcastMsg(String msg) {
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        msg = String.format("[%s]%s", LocalDateTime.now().format(DTF), msg);
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
        }
    }

    public void unicastMesage(String msg, String name){
        for (ClientHandler o : clients){
            if (o.getNickName().equals(name)){
                o.sendMsg(msg);
            }
        }
    }



    public boolean isNickBusy(String nick) {
        for (ClientHandler o : clients) {
            if (o.getNickName().equals(nick)) {
                return true;
            }
        }
        return false;
    }

    public synchronized void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

}
