package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.function.Consumer;
import protocol.Crypto;
import protocol.Message;
import protocol.MessageSerializer;
import protocol.MessageType;

public class MessageReceiver implements Runnable {
    private final BufferedReader in;
    private final Consumer<Message> callback;
    private volatile boolean running;

    public MessageReceiver(BufferedReader in, Consumer<Message> callback) {
        this.in = in;
        this.callback = callback;
    }

    private void debug(String msg) {
        System.out.println("[RECEIVER-DEBUG] " + msg);
    }

    @Override
    public void run() {
        running = true;
        debug("Receiver demarre");
        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                debug("Ligne recue: " + line);
                Message msg = MessageSerializer.deserialize(line);
                if (msg != null) {
                    if (msg.isEncrypted() && msg.getContenu() != null) {
                        String decrypted = Crypto.decrypt(msg.getContenu());
                        msg = new Message(msg.getType(), msg.getPseudo(), decrypted,
                            msg.getTimestamp(), msg.getExtra(), false);
                    }
                    debug("Message deserialise: type=" + msg.getType() + " pseudo=" + msg.getPseudo());
                    callback.accept(msg);
                } else {
                    debug("Message null apres deserialisation");
                }
            }
            debug("Fin de la boucle de reception (readLine null ou running=false)");
        } catch (IOException e) {
            debug("IOException: " + e.getMessage());
            if (running) {
                callback.accept(new Message(MessageType.SERVER_INFO, null,
                    "Connexion perdue avec le serveur", System.currentTimeMillis()));
            }
        }
        debug("Receiver termine");
    }

    public void stop() {
        debug("Stop demande");
        running = false;
    }
}
