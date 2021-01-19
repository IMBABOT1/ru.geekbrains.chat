package ru.geekbrains.server;

import java.sql.SQLException;

public interface AuthManager {
    String getNickNameByLoginAndPassword(String login, String password) throws SQLException;
    void changeNickname(String oldNick, String newNick);
    void connect() throws ClassNotFoundException, SQLException;
    void disconnect();

}