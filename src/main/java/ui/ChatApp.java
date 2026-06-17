package ui;

import client.ChatClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import static ui.Theme.*;

public class ChatApp extends Application {
    private static String host = "localhost";
    private static int port = 5000;
    private static String pseudo = "Agent";

    public static void setConnectionParams(String h, int p, String name) {
        host = h; port = p; pseudo = name;
    }

    @Override
    public void start(Stage primaryStage) {
        if (!showLoginDialog(primaryStage)) {
            Platform.exit();
            return;
        }

        ChatView view = new ChatView(pseudo, host, port);
        ChatClient client = new ChatClient(host, port, pseudo);
        ChatController controller = new ChatController(view, client);

        view.setCommands(controller.getCommands());
        view.setStage(primaryStage);

        primaryStage.setTitle("Chat Secure - Agent: " + pseudo);
        primaryStage.setScene(view.getScene());
        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(450);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.toFront();

        primaryStage.setOnCloseRequest(e -> {
            controller.disconnect();
            Platform.exit();
        });

        primaryStage.show();
        controller.connect();
    }

    private boolean showLoginDialog(Stage owner) {
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Connexion");

        Label title = new Label("CONNEXION AU SERVEUR");
        title.setStyle(fg(ACCENT, FONT_STACK, SZ_LARGE) + "-fx-font-weight: bold;");

        Label hostLabel = new Label("Adresse IP du serveur:");
        hostLabel.setStyle(fg(TEXT, FONT_STACK, SZ_SMALL));
        TextField hostField = new TextField(host);
        hostField.setStyle(surface(BG_INPUT, BORDER, 4) + fg(TEXT, FONT_STACK, SZ_MEDIUM) + padding(8, 12));

        Label portLabel = new Label("Port:");
        portLabel.setStyle(fg(TEXT, FONT_STACK, SZ_SMALL));
        TextField portField = new TextField(String.valueOf(port));
        portField.setStyle(surface(BG_INPUT, BORDER, 4) + fg(TEXT, FONT_STACK, SZ_MEDIUM) + padding(8, 12));

        Label pseudoLabel = new Label("Pseudo:");
        pseudoLabel.setStyle(fg(TEXT, FONT_STACK, SZ_SMALL));
        TextField pseudoField = new TextField(pseudo);
        pseudoField.setStyle(surface(BG_INPUT, BORDER, 4) + fg(TEXT, FONT_STACK, SZ_MEDIUM) + padding(8, 12));

        Button connectBtn = new Button("SE CONNECTER");
        connectBtn.setStyle(bg(ACCENT_DIM) + fg("#000", FONT_STACK, SZ_NORMAL) + "-fx-background-radius: 4px; -fx-padding: 8 18; -fx-cursor: hand;");
        connectBtn.setOnMouseEntered(e -> connectBtn.setStyle(bg(ACCENT) + fg("#000", FONT_STACK, SZ_NORMAL) + "-fx-background-radius: 4px; -fx-padding: 8 18; -fx-cursor: hand;"));
        connectBtn.setOnMouseExited(e -> connectBtn.setStyle(bg(ACCENT_DIM) + fg("#000", FONT_STACK, SZ_NORMAL) + "-fx-background-radius: 4px; -fx-padding: 8 18; -fx-cursor: hand;"));

        Button cancelBtn = new Button("ANNULER");
        cancelBtn.setStyle(bg(BG_INPUT) + fg(TEXT, FONT_STACK, SZ_NORMAL) + "-fx-background-radius: 4px; -fx-padding: 8 18; -fx-cursor: hand;");

        final boolean[] connected = {false};

        connectBtn.setOnAction(e -> {
            try {
                host = hostField.getText().trim();
                port = Integer.parseInt(portField.getText().trim());
                pseudo = pseudoField.getText().trim();
                if (host.isEmpty() || pseudo.isEmpty()) return;
                connected[0] = true;
                dialog.close();
            } catch (NumberFormatException ex) {
                portField.setText(String.valueOf(port));
            }
        });

        cancelBtn.setOnAction(e -> dialog.close());

        pseudoField.setOnAction(e -> connectBtn.fire());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setStyle(bg(BG_PANEL) + border(BORDER, "2"));

        layout.getChildren().addAll(
            title,
            hostLabel, hostField,
            portLabel, portField,
            pseudoLabel, pseudoField,
            new HBox(10, connectBtn, cancelBtn)
        );

        ((HBox) layout.getChildren().get(layout.getChildren().size() - 1)).setAlignment(Pos.CENTER_RIGHT);

        Scene scene = new Scene(layout, 400, 350);
        dialog.setScene(scene);
        dialog.setAlwaysOnTop(true);
        dialog.showAndWait();

        return connected[0];
    }
}
