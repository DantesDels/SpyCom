package server;

import java.io.*;
import java.net.*;

public class ChatServer {
    private final int port;
    private final ClientRegistry registry;
    private volatile boolean running;

    public ChatServer(int port) {
        this.port = port;
        this.registry = new ClientRegistry();
    }

    public void start() {
        running = true;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("[ Server ] En ecoute sur " + InetAddress.getLocalHost().getHostAddress() + ":" + port);
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("[ Server ] Nouvelle connexion de " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                    ClientHandler handler = new ClientHandler(clientSocket, registry);
                    Thread t = new Thread(handler, "Client-" + clientSocket.getPort());
                    t.setDaemon(true);
                    t.start();
                } catch (SocketException e) {
                    if (!running) break;
                }
            }
        } catch (IOException e) {
            System.err.println("[ Server] Erreur fatale : " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
    }
}
