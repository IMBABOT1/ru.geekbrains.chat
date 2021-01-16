package ru.geekbrains.server;

import java.util.ArrayList;
import java.util.List;

public class BasicAuthManager implements AuthManager {
   private class Entry{
      private String password;
      private String login;
      private String nickname;

      public Entry(String login, String password, String nickname){
          this.login = login;
          this.nickname = nickname;
          this.password = password;
      }
   }

   private List<Entry> users;


   public BasicAuthManager(){
       this.users = new ArrayList<>();
       users.add(new Entry("login1", "pass1", "user1"));
       users.add(new Entry("login2", "pass2", "user2"));
       users.add(new Entry("login3", "pass3", "user3"));
   }



    @Override
    public String getNickNameByLoginAndPassword(String login, String password) {
       for (Entry e: users) {
           if (e.login.equals(login) && e.password.equals(password)) {
               return e.nickname;
           }
       }
        return null;
    }
}
