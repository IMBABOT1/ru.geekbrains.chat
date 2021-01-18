package ru.geekbrains.server;

import java.sql.*;

public class SqlAuthManager  {

    private static Connection connection;
    private static Statement statement;
    public static PreparedStatement ps;

    public static void connect() throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:dbmain.db");
        statement = connection.createStatement();
    }



    public static void disconnect() {
        try {
            if (statement != null) {
                statement.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public static void prepareStatement() throws SQLException {
        ps = connection.prepareStatement("INSERT INTO users (login, pass, nickname) VALUES (?,?,?)");
    }

    public static void main(String[] args) {
        try {
            connect();
            //insertInto();
            // update();
            // delete();
            //dropTable();
            //  createTable();
            // fillTableExample();
//          ResultSet rs = statement.executeQuery("SELECT NICKNAME FROM users WHERE login like 'login1' AND pass like 'pass1'");
//            while (rs.next()){
//                System.out.println(rs.getString(1));
//            }
            getNickNameByLoginAndPasswor("login2", "pass2");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            disconnect();
        }
    }

    public static String getNickNameByLoginAndPasswor(String login, String password)  {
        String s = "";
        try {
            ResultSet rs = statement.executeQuery("SELECT NICKNAME FROM users WHERE login like " + "'"+login+"'" +  "AND pass like " + "'"+password+"'");
            while (rs.next()){
                s = (rs.getString(1));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        System.out.println(s);
        return s;
    }



    private static void fillTableExample() throws SQLException {
        connection.setAutoCommit(false);
        prepareStatement();
        for (int i = 1; i < 10000; i++) {
            ps.setString(1, "login" + i);
            ps.setString(2, "pass" + i);
            ps.setString(3, "user" + i);
            ps.executeUpdate();
        }
        connection.commit();
    }

    private static void createTable() throws SQLException {
        statement.executeUpdate("CREATE TABLE users (id INTEGER PRIMARY KEY AUTOINCREMENT, login TEXT, pass TEXT, nickname TEXT, score TEXT);");
    }

    private static void dropTable() throws SQLException {
        statement.executeUpdate("DROP TABLE IF EXISTS users");
    }

    private static void delete() throws SQLException {
        statement.executeUpdate("DELETE FROM users WHERE id = 4;");
    }

    private static void update() throws SQLException {
        statement.executeUpdate("UPDATE users SET score = 100 WHERE id>0");
    }

    private static void insertInto() throws SQLException {
        statement.executeUpdate("INSERT INTO users (login, pass, nickname) VALUES ('login4', 'pass4', 'nickname4')");
    }
}

//    @Override
//    public String getNickNameByLoginAndPassword(String login, String password)  {
//       String s = "";
//        try {
//             ResultSet rs = statement.executeQuery("SELECT NICKNAME FROM users WHERE login like " + "'"+login+"'" +  "AND pass like " + "'"+password+"'");
//             while (rs.next()){
//                 s = rs.getCursorName();
//                 System.out.println(s);
//             }
//       }catch (SQLException e){
//           e.printStackTrace();
//       }
//
//        return s;
//    }
//}


