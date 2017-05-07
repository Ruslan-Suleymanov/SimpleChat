package ru.simplechat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruslaanko on 18.04.17.
 */
public class Server {
    public static void main(String[] args) {

        System.out.println("server started");
        Map<Socket, ClientStatus> clients = Collections.synchronizedMap(new HashMap<Socket, ClientStatus>());
        try {
            ServerSocket socketListener = new ServerSocket(1777);

            while (true) {
                Socket client = socketListener.accept();
                clients.put(client, ClientStatus.UNREGISTERED);
                ClientConnection connection = new ClientConnection(client, clients);
                connection.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
