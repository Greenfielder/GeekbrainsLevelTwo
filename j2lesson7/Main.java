package ru.geekbrains.savenko.j2lesson7;

public class Main {
    public static void main(String[] args) {
        Controller controller = new ClientController();
        ClientUI clientUI = new Client(controller);
        controller.showUI(clientUI);
    }
}
