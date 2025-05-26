package danhhanma.part_time_job.chatbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import danhhanma.part_time_job.Utils.Config;
import danhhanma.part_time_job.objects.contact.Contact;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class ChatService {
    @FunctionalInterface
    public interface TriConsumer<A, B, C> {
        void accept(A a, B b, C c);
    }
    private WebSocketClient client;
    private String token;
    private long id;
    private TriConsumer<String, String, String> onMessageReceived;

    public ChatService(String token, long id) {
        this.token = token;
        this.id = id;
        connect();
    }

    public void setOnMessageReceived(TriConsumer<String,String,String> callback) {
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
                    ObjectMapper mapper = new ObjectMapper();
                    MessageDataTO messageDton = null;
                    try {
                        messageDton = mapper.readValue(message, new TypeReference<MessageDataTO>() {});
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                    String type = messageDton.getType();
                    String sender = messageDton.getSender();
                    String content = messageDton.getContent();
                    if (onMessageReceived != null) {
                        onMessageReceived.accept(type,sender, content);
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

    public void sendMessage(long recipient, String message) throws JsonProcessingException {
        if (client != null && client.isOpen()) {
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "text");
            payload.put("recipient", recipient);
            payload.put("content", message);
            String json= mapper.writeValueAsString(payload);
            client.send(json);
        } else {
            System.out.println("WebSocket is not connected.");
        }
    }
    public void sendImage(long recipient, String file) throws JsonProcessingException {
        if (client != null && client.isOpen()) {
            ObjectMapper mapper = new ObjectMapper();

            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "image");
            payload.put("recipient", recipient);
            payload.put("content", file);
            String json = mapper.writeValueAsString(payload);
            client.send(json);
        } else {
            System.out.println("WebSocket is not connected.");
        }
    }
    public List<Contact> parseContacts(String content) {
        try {

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(content, new TypeReference<List<Contact>>() {});
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}