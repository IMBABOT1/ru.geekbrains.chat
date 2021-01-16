package ru.geekbrains.server;

public interface AuthManager {
    String getNickNameByLoginAndPassword(String login, String password);

}
