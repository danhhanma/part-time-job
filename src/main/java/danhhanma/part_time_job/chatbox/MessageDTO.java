package danhhanma.part_time_job.chatbox;



import danhhanma.part_time_job.Utils.Config;
import danhhanma.part_time_job.Utils.LocalStorage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

public class MessageDTO {
    private String content;
    private boolean isSent;
    private LocalDateTime timestamp;
    private String type;
    private String fileUrl;

    public MessageDTO(String content, boolean isSent, LocalDateTime timestamp, String type, String fileUrl) {
        this.content = content;
        this.isSent = isSent;
        this.timestamp = timestamp;
        this.type = type;
        this.fileUrl = fileUrl;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return content;
    }

    public boolean isSent() {
        return isSent;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public MessageDTO() {
    }
    public static List<MessageDTO> FetchMessages(Long senderId, Long receiverId, int page) throws Exception {
        HttpResponse<String> response = null;
        try {
            String apiUrl = Config.get("api.url");
            String token = LocalStorage.loadToken();

            if (apiUrl == null || token == null) {
                throw new IllegalArgumentException("Missing api.url or token in config");
            }

            String url = apiUrl + "/chat/messages?senderId=" + senderId+"&receiverId=" + receiverId+"&page="+page;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("token", token)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpResponse<String> responses = client.send(request, HttpResponse.BodyHandlers.ofString());
            response= responses;
            if (responses.statusCode() != 200) {
                System.out.println("Failed: " + responses.statusCode() + " - " + responses.body());
            }

        } catch (Exception e) {
            System.out.println("Error creating HTTP request: " + e.getMessage());
        }



        if (response!=null) if(response.statusCode() == 200) {
            System.out.println("Response: " + response.body());
            try {
               return new MessageParser().parseMessages(response.body());
            } catch (Exception e) {
                System.out.println("Error during deserialization:");
                e.printStackTrace();
                throw new RuntimeException("Deserialization failed", e);
            }
        } else {
            throw new RuntimeException("Failed to fetch data: " + response.statusCode());
        }
        return null;
    }

    public String getMessage() {
        return "";
    }
}
