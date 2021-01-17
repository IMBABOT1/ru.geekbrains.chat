package ru.geekbrains.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Network {

    private Socket socket;
    private DataOutputStream out;
    private DataInputStream in;


    public Network(int port) throws IOException {
        Socket socket = new Socket("localhost", port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void sendMst(String msg) throws IOException{
        out.writeUTF(msg);
    }

    public boolean isConnected(){
        if (socket == null || socket.isClosed()){
            return false;
        }
        return true;
    }

    public String readMsg() throws IOException {
        return in.readUTF();
    }

    public void close(){
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}