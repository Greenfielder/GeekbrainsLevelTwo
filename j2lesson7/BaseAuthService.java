package ru.geekbrains.savenko.j2lesson7;

import java.util.ArrayList;
import java.util.List;

public class BaseAuthService implements AuthService {
    private List<User> users = new ArrayList<>();
    public BaseAuthService() {
        users.add(new User("login1", "pass1", "Monster"));
        users.add(new User("login2", "pass2", "Destroyer"));
        users.add(new User("login3", "pass3", "AngyOne"));
        users.add(new User("login4", "pass4", "MisterX"));
    }
    @Override
    public String authByLoginAndPassword(String login, String password) {
        for (User user : users) {
            if (login.equals(user.getLogin())
                    && password.equals(user.getPassword()))
                return user.getNickname();
        }
        return null;
    }
}