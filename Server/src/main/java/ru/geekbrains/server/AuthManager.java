package ru.geekbrains.server;

import java.sql.SQLException;

public interface AuthManager {
    String getNickNameByLoginAndPassword(String login, String password) throws SQLException;
    String changeNickname(String oldNick, String newNick);

}
