package danhhanma.part_time_job.chatbox;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageParser {
    public List<MessageDTO> parseMessages(String responseBody) {
        if (responseBody == null) {
            System.out.println("Response body is null");
            return Collections.emptyList();
        }

        try {
            // Initialize ObjectMapper and register JavaTimeModule
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            // Parse JSON
            JsonNode jsonNode = mapper.readTree(responseBody);

            // Initialize result list
            List<MessageDTO> messages = new ArrayList<>();

            // Deserialize messagesFrom
            JsonNode messagesFromNode = jsonNode.get("messagesFrom");
            if (messagesFromNode != null && messagesFromNode.isArray()) {
                List<MessageDTO> messagesFrom = mapper.convertValue(messagesFromNode, new TypeReference<List<MessageDTO>>() {});
                messages.addAll(messagesFrom);
            } else {
                System.out.println("messagesFrom is missing or not an array");
            }

            // Deserialize messagesTo
            JsonNode messagesToNode = jsonNode.get("messagesTo");
            if (messagesToNode != null && messagesToNode.isArray()) {
                List<MessageDTO> messagesTo = mapper.convertValue(messagesToNode, new TypeReference<List<MessageDTO>>() {});
                messages.addAll(messagesTo);
            } else {
                System.out.println("messagesTo is missing or not an array");
            }
            return messages;

        } catch (Exception e) {
            System.out.println("Error during deserialization:");
            e.printStackTrace();
            throw new RuntimeException("Deserialization failed", e);
        }
    }
}