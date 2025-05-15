package danhhanma.part_time_job.dashboard;


import danhhanma.part_time_job.JobPost.PostController;
import danhhanma.part_time_job.Utils.LocalStorage;
import danhhanma.part_time_job.chatbox.ChatController;
import danhhanma.part_time_job.chatbox.Message;
import danhhanma.part_time_job.controllerapp.MainController;
import danhhanma.part_time_job.objects.contact.Contact;
import danhhanma.part_time_job.objects.post.Account;
import danhhanma.part_time_job.objects.post.Post;
import danhhanma.part_time_job.objects.post.PostAudience;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class FacebookController implements Initializable {

    @FXML
    private BorderPane mainContainer;

    @FXML
    private HBox topNavBar;

    @FXML
    private TextField searchField;

    @FXML
    private Button homeButton, friendsButton, watchButton, gamesButton, groupsButton;

    @FXML
    private ImageView profilePicture, phat,profilePicture2;

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

    List<Post> posts;

    private MainController mainController;

    // Danh sách người liên hệ động
    private List<Contact> contacts;

    // Lưu trữ các cửa sổ chat đang mở
    private final Map<String, VBox> openChats = new HashMap<>();

    // Map để lưu trữ lịch sử chat cho mỗi người liên hệ
    private final Map<String, List<Message>> chatHistories = new HashMap<>();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        System.out.println("MainController đã được thiết lập trong FacebookController");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        posts = new ArrayList<>(getPosts());

        try {
            for (Post post : posts) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/danhhanma/part_time_job/post.fxml"));
                VBox vbox = fxmlLoader.load();
                PostController postController = fxmlLoader.getController();
                postController.setData(post);
                postsContainer.getChildren().add(vbox);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Khởi tạo danh sách người liên hệ động
        initializeContacts();

        // Hiển thị danh sách người liên hệ trong ListView
        contactList.getItems().addAll(contacts);

        // Thiết lập để ListView chỉ hiển thị các ô cần thiết
        contactList.setFixedCellSize(62);
        contactList.setPrefHeight(contacts.size() * contactList.getFixedCellSize());

        // Ẩn thanh cuộn khi không cần thiết
        contactList.setStyle("-fx-background-insets: 0; -fx-padding: 0;");
        contactList.skinProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                contactList.lookup(".scroll-bar:vertical").setVisible(false);
            }
        });

        // Tùy chỉnh giao diện mỗi ô trong ListView
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
                    
                    try {
                        Image image = new Image(getClass().getResource(contact.getAvatarPath()).toString());
                        avatar.setImage(image);
                        avatar.getStyleClass().add("circular-avatar");
                        applyCircularClip(avatar);
                    } catch (Exception e) {
                        // Sử dụng ảnh mặc định nếu không tìm thấy ảnh
                        Image defaultImage = new Image(getClass().getResource("/img/default-avatar.png").toString());
                        avatar.setImage(defaultImage);
                        avatar.getStyleClass().add("circular-avatar");
                        applyCircularClip(avatar);
                    }
                    
                    setGraphic(hBox);
                }
            }
        });

        // Thêm sự kiện click cho ListView
        contactList.setOnMouseClicked(event -> {
            Contact selectedContact = contactList.getSelectionModel().getSelectedItem();
            if (selectedContact != null) {
                openChat(selectedContact.getName());
            }
        });

        mainContainer.widthProperty().addListener((obs, oldVal, newVal) -> {
            handleResponsiveChanges(newVal.doubleValue());
        });

        handleResponsiveChanges(mainContainer.getWidth());
        
        // Cập nhật thông tin người dùng
        updateUserInformation();
    }

    private void initializeContacts() {
        contacts = new ArrayList<>();
        contacts.add(new Contact("Phat Nguyen", "/img/393123651_1490098338498694_7534203730929247714_n.jpg", "Online"));
        contacts.add(new Contact("Hao Nguyen", "/img/haonguyen.jpg", "Online"));
        contacts.add(new Contact("Phuc", "/img/phuccuto.png", "Online"));
    }

    @FXML
    private void openChat(String contactName) {
        try {
            // Tìm contact được chọn
            Contact selectedContact = contacts.stream()
                    .filter(contact -> contact.getName().equals(contactName))
                    .findFirst()
                    .orElse(null);

            if (selectedContact == null) return;

            // Kiểm tra xem cửa sổ chat đã mở chưa
            if (openChats.containsKey(contactName)) {
                // Đưa cửa sổ chat lên trước (focus)
                VBox chatPane = openChats.get(contactName);
                chatContainer.getChildren().remove(chatPane);
                chatContainer.getChildren().add(chatPane);
                return;
            }

            // Load FXML của phần chat
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/danhhanma/part_time_job/chat.fxml"));
            VBox chatPane = loader.load();

            // Lấy controller của phần chat và cập nhật thông tin
            ChatController chatController = loader.getController();
            chatController.setChatTitle(selectedContact.getName());
            chatController.setContactAvatar(selectedContact.getAvatarPath());
            chatController.setStatus(selectedContact.getStatus());
            chatController.setHostServices(danhhanma.part_time_job.application.MainApp.getHostServicesInstance());

            // Load lịch sử chat nếu có
            List<Message> history = chatHistories.getOrDefault(contactName, new ArrayList<>());
            chatController.loadMessageHistory(history);

            // Thêm hiệu ứng xuất hiện
            chatPane.setOpacity(0);
            chatPane.setTranslateY(50);
            FadeTransition fade = new FadeTransition(Duration.millis(200), chatPane);
            fade.setToValue(1.0);
            TranslateTransition slide = new TranslateTransition(Duration.millis(200), chatPane);
            slide.setToY(0);
            fade.play();
            slide.play();

            // Thêm vào container và lưu vào map
            chatContainer.getChildren().add(chatPane);
            openChats.put(contactName, chatPane);

            // Giới hạn số lượng cửa sổ chat (ví dụ: tối đa 2)
            if (openChats.size() > 2) {
                String oldestChat = openChats.keySet().iterator().next();
                chatContainer.getChildren().remove(openChats.get(oldestChat));
                openChats.remove(oldestChat);
            }

            // Căn chỉnh vị trí
            chatContainer.setAlignment(Pos.BOTTOM_RIGHT);
            chatContainer.setSpacing(10);

            // Sự kiện đóng chat
            chatController.setOnClose(() -> {
                // Lưu lịch sử chat trước khi đóng
                chatHistories.put(contactName, chatController.getMessageHistory());
                chatContainer.getChildren().remove(chatPane);
                openChats.remove(contactName);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Post> getPosts() {
        List<Post> ls = new ArrayList<>();

        Post post1 = new Post();
        Account account1 = new Account();
        account1.setName("Hao Nguyen");
        account1.setProfileImg("/img/haonguyen.jpg");
        account1.setVerified(true);
        post1.setAccount(account1);
        post1.setDate("May 9, 2025 at 10:00 PM");
        post1.setAudience(PostAudience.PUBLIC);
        post1.setCaption("Em học sinh này dễ thương quá\nLink bên dưới IPZZ + 3 số cuối");
        post1.setImages(Arrays.asList(
                "/img/hs.jpg"
        ));
        post1.setTotalReactions(10);
        post1.setNbComments(0);
        post1.setNbShares(0);

        ls.add(post1);

        Post post2 = new Post();
        Account account2 = new Account();
        account2.setName("Nguyễn Văn Long Nhật");
        account2.setProfileImg("/img/longnhat.jpg");
        account2.setVerified(true);
        post2.setAccount(account2);
        post2.setDate("May 12, 2025 at 9:40 PM");
        post2.setAudience(PostAudience.PUBLIC);
        post2.setCaption("Xin chào mọi người, mình là Long Nhật, mọi người thấy mình đẹp trai không nào ahihi.\nCho mình 1 tym like và share nhé");
        post2.setImages(Arrays.asList(
                "/img/nhat1.jpg",
                "/img/nhat2.jpg"
        ));
        post2.setTotalReactions(5);
        post2.setNbComments(1);
        post2.setNbShares(2);
        ls.add(post2);

        Post post3 = new Post();
        Account account3 = new Account();
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
        post3.setImages(Arrays.asList(
                "/img/cc.jpg",
                "/img/cc.jpg",
                "/img/cc.jpg",
                "/img/hao.png",
                "/img/hao1.png"
        ));
        ls.add(post3);

        return ls;
    }

    private void handleResponsiveChanges(double width) {
        if (width <= 1200 && width >= 900) {
            leftSidebar.setManaged(false);
            leftSidebar.setVisible(false);

            searchField.setManaged(false);
            searchField.setVisible(false);

            homeButton.setManaged(true);
            friendsButton.setManaged(true);
            watchButton.setManaged(true);
            gamesButton.setManaged(true);
            groupsButton.setManaged(true);

            homeButton.setVisible(true);
            friendsButton.setVisible(true);
            watchButton.setVisible(true);
            gamesButton.setVisible(true);
            groupsButton.setVisible(true);

            rightSidebar.setManaged(true);
            rightSidebar.setVisible(true);
        } else if (width > 1200) {
            leftSidebar.setManaged(true);
            leftSidebar.setVisible(true);

            searchField.setManaged(true);
            searchField.setVisible(true);

            homeButton.setManaged(true);
            friendsButton.setManaged(true);
            watchButton.setManaged(true);
            gamesButton.setManaged(true);
            groupsButton.setManaged(true);

            homeButton.setVisible(true);
            friendsButton.setVisible(true);
            watchButton.setVisible(true);
            gamesButton.setVisible(true);
            groupsButton.setVisible(true);

            rightSidebar.setManaged(true);
            rightSidebar.setVisible(true);
        }
    }

    public void applyCircularClip(ImageView imageView) {
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

    private void updateUserInformation() {
        try {
            String userName = LocalStorage.loadUserName();
            if (userName != null) {
                // Cập nhật tên người dùng trong sidebar
                VBox sidebarContent = (VBox) leftSidebar.getContent();
                if (sidebarContent != null && !sidebarContent.getChildren().isEmpty()) {
                    HBox firstItem = (HBox) sidebarContent.getChildren().get(0);
                    if (firstItem != null && firstItem.getChildren().size() > 1) {
                        Label nameLabel = (Label) firstItem.getChildren().get(1);
                        nameLabel.setText(userName);
                    }
                }
            }

            // Sử dụng ảnh đại diện mặc định
            String avatarPath = "/img/user.png";
            Image avatarImage = new Image(getClass().getResource(avatarPath).toString());
            
            // Cập nhật cho tất cả các avatar
            profilePicture.setImage(avatarImage);
            profilePicture2.setImage(avatarImage);
            phat.setImage(avatarImage);

            // Áp dụng hiệu ứng tròn cho tất cả các avatar
            applyCircularClip(profilePicture);
            applyCircularClip(profilePicture2);
            applyCircularClip(phat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}