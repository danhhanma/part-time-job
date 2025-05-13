package danhhanma.part_time_job.chat;

import danhhanma.part_time_job.Utils.LocalStorage;
import danhhanma.part_time_job.dto.Message;
import danhhanma.part_time_job.dto.UserInChat;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.*;

public class RealChatController {

    @FXML private Button sendButton;
    @FXML private ListView<String> userListView;
    @FXML private VBox messageBox;
    @FXML private TextField inputField;
    @FXML private Label chatHeader;
    @FXML private ScrollPane chatScrollPane;
    List<UserInChat> tmp;
    private String userName; // or dynamically from login
    private long activeChatUser=0;
    private ChatService chatService;
    private String token;
    private HashMap<String,Long> usersInChat=new HashMap<>();
    private long id;
    private HashMap<Long,List<Message>> messages = new HashMap<>();
    // Messages per user/group
    private final Map<Long, ArrayList<HBox>> chatHistories = new HashMap<>();

    @FXML
    public void initialize() {

        // Dummy users
        try{
            token = LocalStorage.loadToken();
            userName= LocalStorage.loadUserName();
            id= LocalStorage.loadUserId();
            System.out.println(id);
            tmp = UserInChat.fetchUsersInChat(id);

            if(tmp!=null)tmp.forEach(userInChat -> {
                usersInChat.put(userInChat.getName(), userInChat.getId());
            });
            chatService = new ChatService(token,id);
        }catch (IOException e){} catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(usersInChat!=null) {
            userListView.getItems().addAll(usersInChat.keySet());

            userListView.getSelectionModel().selectedItemProperty().addListener((obs, old, selectedUser) -> {
                if (selectedUser != null) {
                    long activeUserId = usersInChat.get(selectedUser);
                    try {
                        switchChat(activeUserId,selectedUser);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            inputField.setOnAction(e -> sendMessage());
            sendButton.setOnAction(e -> sendMessage());
            chatService.setOnMessageReceived((from, content) -> Platform.runLater(() -> receiveMessage(from, content)));
            chatScrollPane.setFitToWidth(true);
            chatScrollPane.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                messageBox.setPrefWidth(newBounds.getWidth());
            });
            chatHeader.setText("Chat");
        }
    }

    private void switchChat(long user, String userName) throws Exception {
        activeChatUser = user;
        chatHeader.setText(userName);
        messageBox.getChildren().clear();
        List<Message> messages = Message.FetchMessages(id, user, 0);
        if(messages!=null) messages.sort(Comparator.comparing(Message::getTime));
        messages.forEach((message) -> {
            Label label = new Label(message.getMessage());

            label.setWrapText(true);
            label.maxWidthProperty().bind(messageBox.widthProperty().multiply(0.49));
            label.setFont(Font.font("arial", 20));
            HBox bubble = new HBox(label);
            if(message.getType().equals("To")){
                label.setStyle("-fx-background-color: #e4e6eb; -fx-text-fill: black; -fx-padding: 10; -fx-background-radius: 15;");
                bubble.setAlignment(Pos.CENTER_LEFT);
            }
            else{
                label.setStyle("-fx-background-color: #0084ff; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 15;");
                bubble.setAlignment(Pos.CENTER_RIGHT);
            }
            bubble.setPadding(new Insets(2,0,2,0));
            getChatHistory(id).add(bubble);
            messageBox.getChildren().add(bubble);
        });
    }

    private ArrayList<HBox> getChatHistory(long user) {
        if(!chatHistories.containsKey(user)) {
            chatHistories.put(user, new ArrayList<>());
        }
        return chatHistories.get(user);
    }

    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty() && activeChatUser != 0) {
            Label label = new Label(message);
            label.setStyle("-fx-background-color: #0084ff; -fx-text-fill: white; -fx-padding: 10; -fx-background-radius: 15;");
            label.setWrapText(true);
            label.setFont(Font.font("arial", 20));
            label.setAlignment(Pos.CENTER_RIGHT);
            label.maxWidthProperty().bind(messageBox.widthProperty().multiply(0.49));
            HBox bubble = new HBox(label);
            bubble.setAlignment(Pos.CENTER_RIGHT);  // Align to the right for sender
            bubble.setPadding(new Insets(2,0,2,0));
            getChatHistory(activeChatUser).add(bubble);
            messageBox.getChildren().add(bubble);
            inputField.clear();

            chatService.sendMessage(activeChatUser, message);
        }
    }

    public void receiveMessage(String from, String content) {
        long senderId = Long.parseLong(from);
        System.out.println("Received message from: " + senderId + " - " + content);
        Label label = new Label(content);
        label.setStyle("-fx-background-color: #e4e6eb; -fx-text-fill: black; -fx-padding: 10; -fx-background-radius: 15;");
        label.setWrapText(true);
        label.maxWidthProperty().bind(messageBox.widthProperty().multiply(0.49));
        label.setFont(Font.font("arial", 20));
        HBox bubble = new HBox(label);
        bubble.setAlignment(Pos.CENTER_LEFT);  // Align to the left for receiver
        bubble.setPadding(new Insets(2,0,2,0));
        ArrayList<HBox> dummyHistory = getChatHistory(senderId);  // You'll need to change this to ArrayList<Node>
        dummyHistory.add(bubble);  // Update chatHistories to store List<Node> instead

        messageBox.getChildren().add(bubble);
    }


}
