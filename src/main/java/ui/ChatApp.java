package ui;

import client.ChatClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import static ui.Theme.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class ChatApp extends Application {
    private static final Logger LOGGER = Logger.getLogger(ChatApp.class.getName());
    private static final Path PREFS_FILE = Paths.get(System.getProperty("user.home"), ".spycom_prefs");

    private static String host = "localhost";
    private static int port = 5000;
    private static String pseudo = "Agent";

    private ChatClient client;
    private ChatController controller;
    private Stage primaryStage;

    public static void setConnectionParams(String h, int p, String name) {
        host = h; port = p; pseudo = name;
    }

    @Override
    public void start(Stage primaryStage) {
        setupLogger();
        LOGGER.info("=== ChatApp.start() ===");
        this.primaryStage = primaryStage;
        
        // Charger l'icône de l'application
        try {
            InputStream iconStream = getClass().getResourceAsStream("/icon.png");
            if (iconStream != null) {
                primaryStage.getIcons().add(new Image(iconStream));
                LOGGER.info("Application icon loaded successfully");
            } else {
                LOGGER.warning("Icon file not found in resources");
            }
        } catch (Exception e) {
            LOGGER.warning("Failed to load application icon: " + e.getMessage());
        }
        
        loadPrefs();
        LOGGER.info("Prefs loaded: host=" + host + ", port=" + port);

        showLoginAndConnect(null);
    }

    private void showLoginAndConnect(String errorMsg) {
        LOGGER.info("showLoginAndConnect() called, errorMsg=" + errorMsg);
        if (!showLoginDialog(primaryStage, errorMsg)) {
            LOGGER.info("Login dialog cancelled, exiting");
            Platform.exit();
            return;
        }

        LOGGER.info("Login successful: pseudo=" + pseudo + ", host=" + host + ", port=" + port);

        ChatView view = new ChatView(pseudo, host, port);
        client = new ChatClient(host, port, pseudo);
        controller = new ChatController(view, client);

        controller.setPseudoTakenCallback(() -> Platform.runLater(() -> {
            LOGGER.warning("Pseudo taken, returning to login");
            controller.disconnect();
            primaryStage.hide();
            showLoginAndConnect("Pseudo \"" + pseudo + "\" deja utilise. Choisissez un autre pseudo.");
        }));

        view.setCommands(controller.getCommands());
        view.setStage(primaryStage);

        primaryStage.setTitle("Chat Secure - Agent: " + pseudo);
        primaryStage.setScene(view.getScene());
        primaryStage.setMinWidth(750);
        primaryStage.setMinHeight(450);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.toFront();

        primaryStage.setOnCloseRequest(e -> {
            LOGGER.info("Close request, disconnecting");
            controller.disconnect();
            Platform.exit();
        });

        LOGGER.info("Showing primary stage");
        primaryStage.show();
        LOGGER.info("Connecting to server");
        controller.connect();
        savePrefs();
        LOGGER.info("Connection initiated");
    }

    private boolean showLoginDialog(Stage owner, String errorMsg) {
        LOGGER.info("showLoginDialog() called, errorMsg=" + errorMsg);
        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setTitle("Connexion");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);
        layout.setStyle(bg(BG_PANEL) + border(BORDER, "2"));

        Label title = new Label("CONNEXION AU SERVEUR");
        title.setStyle(fg(ACCENT, FONT_STACK, SZ_LARGE) + "-fx-font-weight: bold;");

        if (errorMsg != null) {
            Label errorLabel = new Label(errorMsg);
            errorLabel.setStyle(fg(DANGER, FONT_STACK, SZ_SMALL) + "-fx-font-weight: bold;");
            errorLabel.setWrapText(true);
            layout.getChildren().add(errorLabel);
        }

        Label hostLabel = new Label("Adresse IP du serveur:");
        hostLabel.setStyle(fg(TEXT, FONT_STACK, SZ_SMALL));
        TextField hostField = new TextField(host);
        hostField.setStyle(surface(BG_INPUT, BORDER, 4) + fg(TEXT, FONT_STACK, SZ_MEDIUM) + padding(8, 12));

        Button ipconfigBtn = new Button("IPCONFIG");
        ipconfigBtn.setStyle(bg(BG_INPUT) + fg(TEXT, FONT_STACK, SZ_SMALL) + "-fx-background-radius: 4px; -fx-padding: 6 12; -fx-cursor: hand;");
        ipconfigBtn.setOnAction(e -> showIpconfigOutput(dialog));

        HBox hostRow = new HBox(8, hostField, ipconfigBtn);
        HBox.setHgrow(hostField, javafx.scene.layout.Priority.ALWAYS);
        hostRow.setAlignment(Pos.CENTER_LEFT);

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
            LOGGER.info("Connect button clicked");
            try {
                host = hostField.getText().trim();
                port = Integer.parseInt(portField.getText().trim());
                pseudo = pseudoField.getText().trim();
                LOGGER.info("Input values: host=" + host + ", port=" + port + ", pseudo=" + pseudo);
                if (host.isEmpty() || pseudo.isEmpty()) {
                    LOGGER.warning("Empty host or pseudo");
                    return;
                }
                connected[0] = true;
                dialog.close();
            } catch (NumberFormatException ex) {
                LOGGER.warning("Invalid port number: " + portField.getText());
                portField.setText(String.valueOf(port));
            }
        });

        cancelBtn.setOnAction(e -> {
            LOGGER.info("Cancel button clicked");
            dialog.close();
        });
        pseudoField.setOnAction(e -> connectBtn.fire());

        HBox btnRow = new HBox(10, connectBtn, cancelBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        layout.getChildren().addAll(
            title,
            hostLabel, hostRow,
            portLabel, portField,
            pseudoLabel, pseudoField,
            btnRow
        );

        Scene scene = new Scene(layout, 450, errorMsg != null ? 380 : 350);
        dialog.setScene(scene);
        dialog.setAlwaysOnTop(true);
        LOGGER.info("Showing login dialog");
        dialog.showAndWait();
        LOGGER.info("Login dialog closed, connected=" + connected[0]);

        return connected[0];
    }

    private void showIpconfigOutput(Stage owner) {
        LOGGER.info("showIpconfigOutput() called");
        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.WINDOW_MODAL);
        popup.initStyle(StageStyle.UNDECORATED);
        popup.setTitle("ipconfig");

        TextArea output = new TextArea();
        output.setEditable(false);
        output.setWrapText(true);
        output.setPrefHeight(300);
        output.setStyle(surface(BG_INPUT, BORDER, 4) + fg(TEXT, FONT_STACK, SZ_SMALL) +
            "-fx-control-inner-background: " + BG_INPUT + ";");

        Button closeBtn = new Button("FERMER");
        closeBtn.setStyle(bg(ACCENT_DIM) + fg("#000", FONT_STACK, SZ_NORMAL) + "-fx-background-radius: 4px; -fx-padding: 6 14; -fx-cursor: hand;");
        closeBtn.setOnAction(e -> popup.close());

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));
        layout.setStyle(bg(BG_PANEL) + border(BORDER, "2"));

        Label title = new Label("RESULTAT IPCONFIG");
        title.setStyle(fg(ACCENT, FONT_STACK, SZ_NORMAL) + "-fx-font-weight: bold;");

        HBox btnRow = new HBox(closeBtn);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        layout.getChildren().addAll(title, output, btnRow);

        Scene scene = new Scene(layout, 500, 380);
        popup.setScene(scene);
        popup.setAlwaysOnTop(true);
        popup.show();

        new Thread(() -> {
            StringBuilder sb = new StringBuilder();
            try {
                LOGGER.info("Executing ipconfig command");
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "ipconfig");
                pb.redirectErrorStream(true);
                Process p = pb.start();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream(), "cp850"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                }
                p.waitFor();
                LOGGER.info("ipconfig command completed");
            } catch (Exception e) {
                LOGGER.severe("ipconfig error: " + e.getMessage());
                sb.append("Erreur: ").append(e.getMessage());
            }
            final String result = sb.toString();
            Platform.runLater(() -> output.setText(result));
        }, "ipconfig-thread").start();
    }

    private void setupLogger() {
        try {
            Path logFile = Paths.get(System.getProperty("user.home"), "spycom_debug.log");
            FileHandler handler = new FileHandler(logFile.toString(), true);
            handler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(handler);
            LOGGER.setLevel(java.util.logging.Level.ALL);
            LOGGER.info("Logger initialized, log file: " + logFile);
        } catch (Exception e) {
            System.err.println("Failed to setup logger: " + e.getMessage());
        }
    }

    private void loadPrefs() {
        LOGGER.info("Loading preferences from " + PREFS_FILE);
        try {
            if (Files.exists(PREFS_FILE)) {
                for (String line : Files.readAllLines(PREFS_FILE)) {
                    String[] kv = line.split("=", 2);
                    if (kv.length == 2) {
                        switch (kv[0]) {
                            case "host" -> host = kv[1];
                            case "port" -> { try { port = Integer.parseInt(kv[1]); } catch (NumberFormatException e) {} }
                        }
                    }
                }
                LOGGER.info("Preferences loaded successfully");
            } else {
                LOGGER.info("No preferences file found, using defaults");
            }
        } catch (Exception e) {
            LOGGER.severe("Error loading preferences: " + e.getMessage());
        }
    }

    private void savePrefs() {
        LOGGER.info("Saving preferences to " + PREFS_FILE);
        try {
            Files.writeString(PREFS_FILE, "host=" + host + "\nport=" + port + "\n");
            LOGGER.info("Preferences saved: host=" + host + ", port=" + port);
        } catch (Exception e) {
            LOGGER.severe("Error saving preferences: " + e.getMessage());
        }
    }
}
