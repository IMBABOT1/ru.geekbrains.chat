package ru.geekbrains.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainApp {

    private static int port = 8189;

    public static void main(String[] args) {
       try (ServerSocket serverSocket = new ServerSocket(port)) {
           System.out.println("Server start on port: " + port + " waiting for clients: ");
           Socket socket = serverSocket.accept();
           System.out.println("Client connect");
           DataInputStream in = new DataInputStream(socket.getInputStream());
           DataOutputStream out = new DataOutputStream(socket.getOutputStream());
           while (true){
               String msg = in.readUTF();
               if (msg.equals("/end")){
                   socket.close();
               }
               System.out.println("Client send: " + msg);
               out.writeUTF("Echo: " + msg);
           }
       }catch (IOException e){
           e.printStackTrace();
       }
    }
}
