package ui;

import client.ChatClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class ChatApp extends Application {
    private static String host = "localhost";
    private static int port = 5000;
    private static String pseudo = "Agent";

    public static void setConnectionParams(String h, int p, String name) {
        host = h; port = p; pseudo = name;
    }

    @Override
    public void start(Stage primaryStage) {
        ChatView view = new ChatView(pseudo, host, port);
        ChatClient client = new ChatClient(host, port, pseudo);
        ChatController controller = new ChatController(view, client);

        view.setCommands(controller.getCommands());
        view.setStage(primaryStage);

        primaryStage.setTitle("Chat Secure - Agent: " + pseudo);
        primaryStage.setScene(view.getScene());
        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(450);

        primaryStage.setOnCloseRequest(e -> {
            controller.disconnect();
            Platform.exit();
        });

        primaryStage.show();
        controller.connect();
    }
}
