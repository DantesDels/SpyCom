package ui.cmd;

import java.util.List;
import java.util.function.Consumer;

import client.ChatClient;
import protocol.Message;
import protocol.MessageType;

/**
 * Parses and executes all slash commands (/msg, /join, /create, /leave,
 * /invite, /status, /rename, /rooms, /help).
 */
public class CommandHandler {

    private static final List<String> CMDS = List.of(
        "/help", "/msg <pseudo> <texte>", "/join <salon>", "/join <salon> private",
        "/create <salon>", "/create <salon> private", "/leave",
        "/invite <pseudo>", "/status <statut>", "/rename <pseudo>", "/rooms");

    private final ChatClient client;
    private final Consumer<Message> addMessage;

    public CommandHandler(ChatClient client, Consumer<Message> addMessage) {
        this.client = client;
        this.addMessage = addMessage;
    }

    public List<String> getCommands() {
        return CMDS;
    }

    /**
     * Parse and execute a slash command.
     * Returns a Result describing any side-effect the caller's UI should react to.
     */
    public Result execute(String cmd) {
        String[] p = cmd.split(" ", 3);
        String head = p[0].toLowerCase();

        switch (head) {
            case "/msg" -> {
                if (p.length < 3) { info("Usage: /msg <pseudo> <message>"); return Result.none(); }
                client.sendPrivateMessage(p[1], p[2]);
                addMessage.accept(new Message(MessageType.PRIVATE_MSG, client.getPseudo(),
                    "(prive) " + p[2], System.currentTimeMillis(), p[1]));
                return Result.none();
            }

            case "/join" -> {
                if (p.length < 2) { info("Usage: /join <salon> ou /join <salon> private"); return Result.none(); }
                boolean pub = p.length <= 2 || !"private".equalsIgnoreCase(p[2]);
                client.joinRoom(p[1], pub);
                info("Jointure du salon: " + p[1]);
                return Result.room(p[1]);
            }

            case "/create" -> {
                if (p.length < 2) { info("Usage: /create <salon> ou /create <salon> private"); return Result.none(); }
                boolean pub = p.length <= 2 || !"private".equalsIgnoreCase(p[2]);
                client.joinRoom(p[1], pub);
                info(pub ? "Salon public cree: " + p[1] : "Salon prive cree: " + p[1]);
                return Result.room(p[1]);
            }

            case "/leave" -> {
                client.joinRoom("general");
                info("Retour au salon general");
                return Result.room("general");
            }

            case "/invite" -> {
                if (p.length < 2) { info("Usage: /invite <pseudo>"); return Result.none(); }
                client.inviteUser(p[1]);
                info("Invitation envoyee a " + p[1]);
                return Result.none();
            }

            case "/status" -> {
                if (p.length < 2) { info("Usage: /status <online|busy|afk|custom>"); return Result.none(); }
                client.changeStatus(p[1]);
                return Result.status(p[1]);
            }

            case "/rename" -> {
                if (p.length < 2) { info("Usage: /rename <nouveau_pseudo>"); return Result.none(); }
                client.changeNick(p[1]);
                info("Demande de renommage vers: " + p[1]);
                return Result.none();
            }

            case "/rooms" -> {
                client.requestRoomList();
                return Result.none();
            }

            case "/help" -> {
                info("Commandes: /msg, /join, /create, /leave, /invite, /status, /rename, /rooms, /help");
                return Result.none();
            }

            default -> {
                info("Commande inconnue: " + head + ". Tapez /help");
                return Result.none();
            }
        }
    }

    public void info(String s) {
        addMessage.accept(new Message(MessageType.SERVER_INFO, null, s, System.currentTimeMillis()));
    }

    /**
     * Lightweight result carrying an action hint for the UI layer.
     * The caller should inspect the action and apply matching view updates.
     */
    public record Result(String action, String value) {
        public static Result room(String r)   { return new Result("room", r); }
        public static Result status(String s) { return new Result("status", s); }
        public static Result none()           { return new Result(null, null); }
    }
}
