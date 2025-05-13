package danhhanma.part_time_job.chat;


import danhhanma.part_time_job.Utils.Config;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.BiConsumer;

public class ChatService {

    private WebSocketClient client;
    private String token;
    private long id;
    private BiConsumer<String, String> onMessageReceived;

    public ChatService(String token, long id) {
        this.token = token;
        this.id = id;
        connect();
    }

    public void setOnMessageReceived(BiConsumer<String, String> callback) {
        this.onMessageReceived = callback;
    }

    private void connect() {
        try {
            URI uri = new URI(Config.get("api.ws") +"/chat?id=" + id);
            System.out.println(uri);
            client = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    System.out.println("Connected to WebSocket server");
                }

                @Override
                public void onMessage(String message) {
                    String[] parts = message.split(":", 2);
                    if (parts.length == 2 && onMessageReceived != null) {
                        String sender = parts[0];
                        String content = parts[1];
                        onMessageReceived.accept(sender, content);
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    System.out.println("Connection closed: " + reason);
                }

                @Override
                public void onError(Exception ex) {
                    ex.printStackTrace();
                }
            };

            client.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(long recipient, String message) {
        if (client != null && client.isOpen()) {
            client.send(recipient + ":" + message);
        } else {
            System.out.println("WebSocket is not connected.");
        }
    }
}