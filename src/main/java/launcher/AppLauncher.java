package launcher;

import javafx.application.Application;
import server.ChatServer;
import ui.ChatApp;

public class AppLauncher {

    private AppLauncher() {}

    public static void start(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("server")) {
            launchServer(args);
        } else {
            launchClient(args);
        }
    }

    private static void launchServer(String[] args) {
        int port = args.length > 1 ? parseInt(args[1], 5000) : 5000;
        System.out.println("[Launcher] Demarrage du serveur sur le port " + port);
        new ChatServer(port).start();
    }

    private static void launchClient(String[] args) {
        String host = args.length > 0 ? args[0] : "localhost";
        int port = args.length > 1 ? parseInt(args[1], 5000) : 5000;
        String pseudo = args.length > 2 ? args[2] : "Agent" + (int)(Math.random() * 9000 + 1000);
        System.out.println("[Launcher] Demarrage du client pour " + pseudo + " -> " + host + ":" + port);
        ChatApp.setConnectionParams(host, port, pseudo);
        Application.launch(ChatApp.class, args);
    }

    private static int parseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.err.println("[Launcher] Port invalide: " + s + ", utilisation du defaut " + def);
            return def;
        }
    }
}