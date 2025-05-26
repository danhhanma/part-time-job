package danhhanma.part_time_job.dto;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import danhhanma.part_time_job.Utils.Config;
import danhhanma.part_time_job.Utils.LocalStorage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class UserInChat {
    private Long id;
    private String name;
    public UserInChat(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public UserInChat() {
    }
    // Getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public static List<UserInChat> fetchUsersInChat(Long userId) throws Exception {
        HttpResponse<String> response = null;
        try {
            String apiUrl = Config.get("api.url");
            String token = LocalStorage.loadToken();

            if (apiUrl == null || token == null) {
                throw new IllegalArgumentException("Missing api.url or token in config");
            }

            String url = apiUrl + "/chat/users?userId=" + userId;

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
                ObjectMapper mapper = new ObjectMapper();
                List<UserInChat> users = mapper.readValue(response.body(), new TypeReference<>() {});
                return users;
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
}
