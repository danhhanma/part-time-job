package danhhanma.part_time_job.dashboard;
import javafx.fxml.Initializable;
import danhhanma.part_time_job.JobPost.PostController;
import danhhanma.part_time_job.Utils.LocalStorage;
import danhhanma.part_time_job.application.MainApp;
import danhhanma.part_time_job.chatbox.ChatService;
import danhhanma.part_time_job.chatbox.ChatController;
import danhhanma.part_time_job.chatbox.Message;
import danhhanma.part_time_job.objects.contact.Contact;
import danhhanma.part_time_job.objects.post.Account;
import danhhanma.part_time_job.objects.post.Post;
import danhhanma.part_time_job.objects.post.PostAudience;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class EmployerController implements Initializable {

    @FXML
    private BorderPane mainContainer;

    @FXML
    private HBox topNavBar;

    @FXML
    private TextField searchField;

    @FXML
    private Button homeButton, friendsButton, watchButton, gamesButton, groupsButton;

    @FXML
    private ImageView profilePicture, profilePicture2;

    @FXML
    private ScrollPane mainScrollPane;

    @FXML
    private ScrollPane leftSidebar;

    @FXML
    private VBox rightSidebar;

    @FXML
    private VBox postsContainer;

    @FXML
    private HBox chatContainer;

    @FXML
    private ListView<Contact> contactList;
    @FXML
    private Label userName;
    private List<Post> posts;
    private final List<Contact> contacts = Collections.synchronizedList(new ArrayList<>());
    @FXML
    private String token;
    private long id;
    private ChatService chatService;

    private final Map<Long, VBox> openChats = new HashMap<>();
    private final Map<Long, List<Message>> chatHistories = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        posts = new ArrayList<>(getPosts());
        loadPosts();

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                initializeContacts();
                return null;
            }
        };
        task.setOnFailed(event -> {
            Throwable e = task.getException();
            System.err.println("Failed to initialize contacts: " + e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Initialization Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to initialize contacts. Please try again later.");
                alert.showAndWait();
            });
        });
        new Thread(task).start();

        // Set up responsive design listener
        mainContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            handleResponsiveChanges(newVal.doubleValue());
        });
        handleResponsiveChanges(mainContainer.getWidth());

        // Update user information
        updateUserInformation();
    }

    private void loadPosts() {
        try {
            for (Post post : posts) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/danhhanma/part_time_job/post.fxml"));
                VBox vbox = fxmlLoader.load();
                PostController postController = fxmlLoader.getController();
                postController.setData(post);
                postController.setOpenContact(() -> openChat(post.getAccount().getId()));
                postsContainer.getChildren().add(vbox);
            }
        } catch (Exception e) {
            System.err.println("Failed to load posts: " + e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to load posts. Please try again.");
                alert.showAndWait();
            });
        }
    }

    private void initializeContacts() {
        try {
            token = LocalStorage.loadToken();
            userName = new Label(LocalStorage.loadUserName());
            id = LocalStorage.loadUserId();
            chatService = new ChatService(token, id);
            chatService.setOnMessageReceived((type,sender, content) -> Platform.runLater(() -> {
                if (type.equals("Users")) {
                    List<Contact> c = content.equals("[]") ? new ArrayList<>() : chatService.parseContacts(content);
                    // Note: Removed hardcoded contact for production
                    contactList.getItems().setAll(c);
                    synchronized (contacts) {
                        contacts.clear();
                        contacts.addAll(c);
                        for(Contact contactItem : c) {
                            System.out.println(contactItem.getId());
                        }
                    }

                    // Set up ListView properties
                    contactList.setFixedCellSize(62);
                    contactList.setPrefHeight(c.size() * contactList.getFixedCellSize());
                    contactList.setStyle("-fx-background-insets: 0; -fx-padding: 0;");
                    contactList.skinProperty().addListener((obs, oldVal, newVal) -> {
                        if (newVal != null) {
                            contactList.lookup(".scroll-bar:vertical").setVisible(false);
                        }
                    });

                    // Customize ListView cells
                    contactList.setCellFactory(listView -> new ListCell<Contact>() {
                        private final ImageView avatar = new ImageView();
                        private final HBox hBox = new HBox(10);
                        private final Label label = new Label();

                        {
                            avatar.setFitHeight(50);
                            avatar.setFitWidth(50);
                            hBox.getChildren().addAll(avatar, label);
                            hBox.setAlignment(Pos.CENTER_LEFT);
                            hBox.getStyleClass().add("menu-item");
                        }

                        @Override
                        protected void updateItem(Contact contact, boolean empty) {
                            super.updateItem(contact, empty);
                            if (empty || contact == null) {
                                setGraphic(null);
                            } else {
                                label.setText(contact.getName());
                                label.setStyle("-fx-font-weight: bold;");
                                avatar.setImage(loadImage(contact.getAvatar()));
                                avatar.getStyleClass().add("circular-avatar");
                                applyCircularClip(avatar);
                                setGraphic(hBox);
                            }
                        }
                    });

                    // Handle contact selection
                    contactList.setOnMouseClicked(event -> {
                        Contact selectedContact = contactList.getSelectionModel().getSelectedItem();
                        if (selectedContact != null) {
                            openChat(Long.parseLong(selectedContact.getId()));
                        }
                    });
                } else {
                    openChat(Long.parseLong(sender));
                }
            }));
        } catch (IOException e) {
            System.err.println("Failed to load token or user information: " + e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Initialization Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to initialize the application. Please try again later.");
                alert.showAndWait();
            });
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize contacts", e);
        }
    }

    @FXML
    private void openChat(long contactID) {
        Platform.runLater(() -> {
            try {
                // Find the selected contact
                Contact selectedContact;
                synchronized (contacts) {
                    selectedContact = contacts.stream()
                            .filter(contact -> Objects.equals(contact.getId(), String.valueOf(contactID)))
                            .findFirst()
                            .orElse(null);
                }

                if (selectedContact == null) {
                    System.err.println("Contact not found for ID: " + contactID);
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Contact Not Found");
                    alert.setHeaderText(null);
                    alert.setContentText("The selected contact could not be found.");
                    alert.showAndWait();
                    return;
                }

                // Check if chat is already open
                if (openChats.containsKey(contactID)) {
                    VBox chatPane = openChats.get(contactID);
                    chatContainer.getChildren().remove(chatPane);
                    chatContainer.getChildren().add(chatPane);
                    return;
                }

                // Load chat FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/danhhanma/part_time_job/chat.fxml"));

                VBox chatPane = loader.load();
                ChatController chatController = loader.getController();

                chatController.setOpponentId(String.valueOf(selectedContact.getId()));


                chatController.setChatService(chatService);
                chatController.setChatTitle(selectedContact.getName());
                chatController.setContactAvatar(selectedContact.getAvatar());
                chatController.setStatus(selectedContact.getStatus());
                chatController.setHostServices(MainApp.getHostServicesInstance());
                // Load chat history
                List<Message> history = chatHistories.getOrDefault(contactID, new ArrayList<>());
                chatController.loadMessageHistory();

                // Add animation
                chatPane.setOpacity(0);
                chatPane.setTranslateY(50);
                FadeTransition fade = new FadeTransition(Duration.millis(200), chatPane);
                fade.setToValue(1.0);
                TranslateTransition slide = new TranslateTransition(Duration.millis(200), chatPane);
                slide.setToY(0);
                fade.play();
                slide.play();

                // Add to container and map
                chatContainer.getChildren().add(chatPane);
                openChats.put(contactID, chatPane);

                // Limit open chats to 2
                if (openChats.size() > 2) {
                    Long oldestChat = openChats.keySet().iterator().next();
                    chatContainer.getChildren().remove(openChats.get(oldestChat));
                    openChats.remove(oldestChat);
                }

                // Align chat container
                chatContainer.setAlignment(Pos.BOTTOM_RIGHT);
                chatContainer.setSpacing(10);

                // Handle chat close
                chatController.setOnClose(() -> {
                    chatContainer.getChildren().remove(chatPane);
                    openChats.remove(contactID);
                });
            } catch (Exception e) {
                System.err.println("Failed to open chat: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to open chat. Please try again.");
                alert.showAndWait();
            }
        });
    }

    private List<Post> getPosts() {
        List<Post> ls = new ArrayList<>();

        Post post1 = new Post();
        Account account1 = new Account();
        account1.setId(35);
        account1.setName("Hao Nguyen");
        account1.setProfileImg("/img/haonguyen.jpg");
        account1.setVerified(true);
        post1.setAccount(account1);
        post1.setDate("May 9, 2025 at 10:00 PM");
        post1.setAudience(PostAudience.PUBLIC);
        post1.setCaption("Em học sinh này dễ thương quá\nLink bên dưới IPZZ + 3 số cuối");
        post1.setImages(Arrays.asList("/img/hs.jpg"));
        post1.setTotalReactions(10);
        post1.setNbComments(0);
        post1.setNbShares(0);
        ls.add(post1);

        Post post2 = new Post();
        Account account2 = new Account();
        account2.setId(36);
        account2.setName("Nguyễn Văn Long Nhật");
        account2.setProfileImg("/img/longnhat.jpg");
        account2.setVerified(true);
        post2.setAccount(account2);
        post2.setDate("May 12, 2025 at 9:40 PM");
        post2.setAudience(PostAudience.PUBLIC);
        post2.setCaption("Xin chào mọi người, mình là Long Nhật, mọi người thấy mình đẹp trai không nào ahihi.\nCho mình 1 tym like và share nhé");
        post2.setImages(Arrays.asList("/img/nhat1.jpg", "/img/nhat2.jpg"));
        post2.setTotalReactions(5);
        post2.setNbComments(1);
        post2.setNbShares(2);
        ls.add(post2);

        Post post3 = new Post();
        Account account3 = new Account();
        account3.setId(37);
        account3.setName("Nguyễn Tấn Phát");
        account3.setProfileImg("/img/393123651_1490098338498694_7534203730929247714_n.jpg");
        account3.setVerified(true);
        post3.setAccount(account3);
        post3.setDate("May 10, 2025 at 12:00 AM");
        post3.setAudience(PostAudience.PUBLIC);
        post3.setCaption(
                "Xin chào các bạn\n" +
                        "\uD83E\uDD70\uD83E\uDD70 CẦN GẤP\n" +
                        "Quán Bún Chả cần tuyển 2 người phụ quán (rửa rau, chén, quét dọn, phụ bưng bê, dọn dẹp vệ sinh...) chưa biết sẽ được hướng dẫn\n" +
                        "➖Thời gian\n" +
                        "C1: 7h - 15h (1 bạn)\n" +
                        "C2: 15h - 22h (1 bạn)\n" +
                        "Tăng lương 3-6-9-12 tháng theo thâm niên gắn bó.\n" +
                        "Bao ăn theo ca, sắp xếp làm xoay ca theo tuần\n" +
                        "✖\uFE0F (lương sẽ trao đổi sau khi gặp ở quán)\n" +
                        "✔\uFE0FYêu cầu: Nhanh nhẹn, chịu khó, sạch sẽ, nhiệt tình công việc & thật sự có thiện chí làm công việc quán ăn \uD83E\uDE75\n" +
                        "Có thiện chí, có tính cầu tiến, thật thà, chăm chỉ, biết lắng nghe, ham học hỏi.\n" +
                        "LƯU Ý KĨ ĐỂ KHÔNG LÀM MẤT TIME ĐÔI BÊN \uD83E\uDEF6\uD83C\uDFFB\n" +
                        "\uD83D\uDCCCKHÔNG TUYỂN LÀM NGẮN HẠN\n" +
                        "\uD83D\uDCCCKHÔNG TUYỂN NGƯỜI VƯỚNG BẬN GIA ĐÌNH\n" +
                        "\uD83D\uDCCCKHÔNG BAO Ở\n" +
                        "\uD83D\uDCCCĐỌC KĨ TRƯỚC KHI ỨNG TUYỂN\n" +
                        "➖Địa chỉ: 147 Mai Đăng Chơn - Đà Nẵng\n" +
                        "SĐT trao đổi: 0782.534.885 (Tài) / 0782.534.885 (Bún chả quạt)\n" +
                        "Hoặc gặp trực tiếp tại quán:\n" +
                        "Bún chả quạt & Nem nướng Nha Trang - 147 Mai Đăng Chơn\n" +
                        "\uD83C\uDF40Trân quý gặp được người có thiện chí sớm ạ");
        post3.setImages(Arrays.asList("/img/cc.jpg", "/img/cc.jpg", "/img/cc.jpg", "/img/hao.png", "/img/hao1.png"));
        ls.add(post3);

        return ls;
    }

    private void handleResponsiveChanges(double width) {
        boolean isLargeScreen = width > 1200;

        leftSidebar.setManaged(isLargeScreen);
        leftSidebar.setVisible(isLargeScreen);
        searchField.setManaged(isLargeScreen);
        searchField.setVisible(isLargeScreen);

        homeButton.setManaged(true);
        homeButton.setVisible(true);
        friendsButton.setManaged(true);
        friendsButton.setVisible(true);
        watchButton.setManaged(true);
        watchButton.setVisible(true);
        gamesButton.setManaged(true);
        gamesButton.setVisible(true);
        groupsButton.setManaged(true);
        groupsButton.setVisible(true);
        rightSidebar.setManaged(true);
        rightSidebar.setVisible(true);
    }

    private void applyCircularClip(ImageView imageView) {
        double width = imageView.getFitWidth();
        double height = imageView.getFitHeight();
        if (width <= 0 || height <= 0) {
            width = 50;
            height = 50;
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
        }

        double radius = Math.min(width, height) / 2;
        double centerX = width / 2;
        double centerY = height / 2;

        Circle clip = new Circle(centerX, centerY, radius);
        imageView.setClip(clip);
    }

    private Image loadImage(String path) {
        try {
            if(path.contains("https://")) {
                return new Image(path);
            }
            else{
                return new Image(getClass().getResource(path).toString());
            }
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path);
            return new Image(getClass().getResource("/img/default-avatar.png").toString());
        }
    }

    private void updateUserInformation() {
        try {
            String userName = LocalStorage.loadUserName();
            if (userName != null) {
                VBox sidebarContent = (VBox) leftSidebar.getContent();

                if (sidebarContent != null && !sidebarContent.getChildren().isEmpty()) {
                    HBox firstItem = (HBox) sidebarContent.getChildren().get(0);
                    if (firstItem != null && firstItem.getChildren().size() > 1) {
                        Label nameLabel = (Label) firstItem.getChildren().get(1);
                        nameLabel.setText(userName);
                    }
                }
            }
            String avatarPath = LocalStorage.loadAvatarPath();
            Image avatarImage= loadImage(avatarPath);
            profilePicture.setImage(avatarImage);


            applyCircularClip(profilePicture);
            applyCircularClip(profilePicture2);

        } catch (Exception e) {
            System.err.println("Failed to update user information: " + e.getMessage());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Failed to update user information.");
                alert.showAndWait();
            });
        }
    }

    public void cleanup() {
        openChats.clear();
        chatHistories.clear();
        synchronized (contacts) {
            contacts.clear();
        }
        postsContainer.getChildren().clear();
    }
}