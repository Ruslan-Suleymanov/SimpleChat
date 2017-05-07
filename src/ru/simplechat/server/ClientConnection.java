package ru.simplechat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Map;

/**
 * Created by ruslaanko on 18.04.17.
 */
public class ClientConnection extends Thread {

    private Socket client;
    private Map<Socket, ClientStatus> clients;
    private String userName;

    public ClientConnection(Socket client, Map<Socket, ClientStatus> clients) {
        this.client = client;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);

            writer.println("Введите свое имя");
            userName = reader.readLine();
            clients.put(client, ClientStatus.REGISTERED);
            System.out.println("new client " + userName);
            writer.println("Привет " + userName + ", теперь ты можешь отправлять сообщения в чат");
            broadcast("Скажем дружно: \"Привет " + userName + "\"");

            while (true) {
                String message = reader.readLine();
                switch (message) {
                    case "mute":
                        clients.put(client, ClientStatus.MUTED);
                        break;
                    case "unmute":
                        clients.put(client, ClientStatus.REGISTERED);
                        break;
                    case "exit":
                        broadcast(userName + " : покинул чат");
                        writer.println("exit");
                        clients.remove(client);
                        interrupt();
                        break;
                    default:
                        broadcast(userName + " : " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) throws IOException {
        for (Socket socket : clients.keySet()) {
            if (clients.get(socket) == ClientStatus.REGISTERED) {
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                writer.println(message);
            }
        }
    }
}
