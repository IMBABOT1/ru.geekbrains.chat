//package ru.geekbrains.server;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class MyThread implements Runnable {
//
//    private int first;
//    private int second;
//    private ArrayList<String> result;
//    private List<ClientHandler> clients;
//    private int caret;
//
//
//    public MyThread(ArrayList<String> result, List<ClientHandler> list, int first, int second) {
//        caret = 20;
//        this.first = first;
//        this.second = second;
//        this.result = new ArrayList<>();
//        this.clients = new ArrayList<>();
//    }
//
//
//    @Override
//    public void run() {
//        for (int i = (result.size() - 1  - (caret * first));  i <= (result.size() - 1  - (caret * second)); i++) {
//            for (ClientHandler o : clients) {
//                try {
//                    Thread.sleep(10);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                o.sendMsg(result.get(i));
//            }
//        }
//    }
//}
//
