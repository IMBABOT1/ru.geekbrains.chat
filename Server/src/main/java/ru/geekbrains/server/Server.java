package ru.geekbrains.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class Server {
    private List<ClientHandler> clients;

    public ArrayList<String> getResult() {
        return result;
    }

    private ArrayList<String> result;

    public AuthManager getAuthManager() {
        return authManager;
    }

    private AuthManager authManager;
    private final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Server(int port) {
        clients = new ArrayList<>();
        result = new ArrayList<>();
        authManager = new SqlAuthManager();
        try {
            authManager.connect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server start on port: " + port + " waiting for clients: ");
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Client connect");
                new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            authManager.disconnect();
        }
    }

    public void broadcastMsg(String msg, boolean withDateTime) {
        if (withDateTime) {
            msg = String.format("[%s]%s", LocalDateTime.now().format(DTF), msg);
        }
        for (ClientHandler o : clients) {
            o.sendMsg(msg);
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

    public synchronized void writeLog1(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = result.size() - 1; i >= 150; i--) {
                    for (ClientHandler o : clients) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        o.sendMsg(result.get(i));

                    }
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeLog2(){
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 149; i >= 130; i--) {
                    for (ClientHandler o : clients) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        o.sendMsg(result.get(i));

                    }
                }
            }
        });
        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void writeLog3(){
        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 148; i >= 110; i--) {
                    for (ClientHandler o : clients) {
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        o.sendMsg(result.get(i));

                    }
                }
            }
        });
        t2.start();
        try {
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
//    public synchronized void writeLog4(){
//        Thread t3 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                for (int i = result.size() - 1; i >= 90; i--) {
//                    for (ClientHandler o : clients) {
//                        try {
//                            Thread.sleep(10);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                        o.sendMsg(result.get(i));
//
//                    }
//                }
//            }
//        });
//        t3.start();
//        try {
//            t3.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }




    public void broadcastClientsList() {
        StringBuilder sb = new StringBuilder("/clients_list ");
        for (ClientHandler o : clients) {
            sb.append(o.getNickName()).append(" ");
        }
        sb.setLength(sb.length() - 1);
        String out = sb.toString();
        broadcastMsg(out, false);
    }

    public void unicastMesage(String msg, String name) {
        for (ClientHandler o : clients) {
            if (o.getNickName().equals(name)) {
                o.sendMsg(msg);
            }
        }
    }

    public void changeNick(String oldNick, String nick) {
        for (ClientHandler o : clients) {
            if (o.getNickName().equals(oldNick)) {
                o.setNickName(nick);
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
        broadcastClientsList();
    }

    public synchronized void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        broadcastClientsList();
    }
}

