package ru.geekbrains.savenko.j2lesson7;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class ClientHandler {
    private Server server;
    private PrintWriter pw;
    private Scanner sc;
    private String nick;
    private DataInputStream in;
    private boolean isLogin = false;

    public ClientHandler(Socket socket, Server server) {
        this.server = server;
        try {
            sc = new Scanner(socket.getInputStream());
            pw = new PrintWriter(socket.getOutputStream(), true);
            new Thread(() -> {
                do {
                    try {
                        socket.setSoTimeout(120000);
                    } catch (SocketException e) {
                        e.printStackTrace();
                    }
                    auth();
                } while (isLogin == true);

                System.out.println(nick + " handler waiting for new massages");
                while (socket.isConnected()) {
                    String s = sc.nextLine();
                    if (s != null && s.equals("/exit"))
                        server.unsubscribe(this);
                    if (s.startsWith("/")) {
                        if (s.equals("/end")) break;
                        if (s.startsWith("/w ")) {
                            String[] tokens = s.split("\\s");
                            String nick = tokens[1];
                            String msg = s.substring(4 + nick.length());
                            server.sendMsgToClient(this, nick, msg);
                        }
                    } else {
                        server.sendBroadcastMessage(nick + ": " + s);
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wait for command: "/auth login1 pass1"
     */
    private void auth() {
        while (true) {
            if (!sc.hasNextLine()) continue;
            String s = sc.nextLine();
            if (s.startsWith("/auth")) {

                String[] commands = s.split(" ");// /auth login1 pass1
                if (commands.length >= 3) {
                    String login = commands[1];
                    String password = commands[2];
                    System.out.println("Try to login with " + login + " and " + password);
                    String nick = server.getAuthService()
                            .authByLoginAndPassword(login, password);
                    if (nick == null) {
                        String msg = "Неправельный логин или пароль";
                        System.out.println(msg);
                        pw.println(msg);
                    } else if (server.isNickTaken(nick)) {
                        String msg = "Такой ник " + nick + " уже занят!";
                        System.out.println(msg);
                        pw.println(msg);
                    } else {
                        this.nick = nick;
                        String msg = "Auth ok!";
                        isLogin = true;
                        System.out.println(msg);
                        pw.println(msg);
                        server.subscribe(this);
                        break;
                    }
                }
            } else {
                pw.println("Неверная команда!");
            }
        }
    }

    public void sendMessage(String msg) {
        pw.println(msg);
    }

    public String getNick() {
        return nick;
    }
}