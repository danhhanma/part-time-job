package danhhanma.part_time_job.chatbox;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.*;
import javafx.util.Duration;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ChatController {

    @FXML
    private Label chatTitle;

    @FXML
    private Label statusLabel;

    @FXML
    private ImageView contactAvatar;

    @FXML
    private ScrollPane messageArea;

    @FXML
    private VBox messageContainer;

    @FXML
    private TextField messageInput;

    @FXML
    private VBox chatBox;

    @FXML
    private Button imageButton;

    @FXML
    private Button fileButton;

    @FXML
    private Button folderButton;

    private Runnable onClose;
    private List<Message> messageHistory;
    private HostServices hostServices; // Thêm để mở link

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final String CHAT_FILES_DIR = "chat_files";
    private static final Pattern URL_PATTERN = Pattern.compile(
            "^(https?://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$",
            Pattern.CASE_INSENSITIVE
    );

    public ChatController() {
        this.messageHistory = new ArrayList<>();
    }

    public void setChatTitle(String title) {
        chatTitle.setText(title);
    }

    public void setContactAvatar(String imagePath) {
        try {
            Image image = new Image(getClass().getResource(imagePath).toString());
            contactAvatar.setImage(image);
            applyCircularClip(contactAvatar);
        } catch (Exception e) {
            contactAvatar.setImage(new Image(getClass().getResource("/img/user.png").toString()));
            applyCircularClip(contactAvatar);
        }
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public void addMessage(String message, boolean isSent) {
        // Kiểm tra xem tin nhắn có phải là URL không
        Message.Type type = isValidUrl(message) ? Message.Type.LINK : Message.Type.TEXT;
        Message newMessage = new Message(message, isSent, type, null);
        messageHistory.add(newMessage);

        // Hiển thị tin nhắn
        showMessage(newMessage);
        messageArea.setVvalue(1.0);
    }

    // Phương thức để load lại lịch sử tin nhắn
    public void loadMessageHistory(List<Message> history) {
        messageHistory.clear();
        messageHistory.addAll(history);
        messageContainer.getChildren().clear();
        for (Message message : messageHistory) {
            showMessage(message);
        }
        messageArea.setVvalue(1.0);
    }

    // Getter cho lịch sử tin nhắn
    public List<Message> getMessageHistory() {
        return new ArrayList<>(messageHistory);
    }

    private void applyMessageAnimation(VBox messageWrapper, boolean isSent) {
        FadeTransition fade = new FadeTransition(Duration.millis(300), messageWrapper);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);

        TranslateTransition slide = new TranslateTransition(Duration.millis(300), messageWrapper);
        slide.setFromX(isSent ? 50 : -50);
        slide.setToX(0);

        fade.play();
        slide.play();
    }

    private boolean isValidUrl(String text) {
        return URL_PATTERN.matcher(text).matches();
    }

    @FXML
    private void closeChat() {
        if (onClose != null) {
            onClose.run();
        }
    }

    @FXML
    private void sendMessage() {
        String message = messageInput.getText().trim();
        if (!message.isEmpty()) {
            addMessage(message, true);
            messageInput.clear();

            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> {
                        addMessage("Oke", false);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @FXML
    public void initialize() {
        messageInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
    }

    public void applyCircularClip(ImageView imageView) {
        double width = imageView.getFitWidth();
        double height = imageView.getFitHeight();
        if (width <= 0 || height <= 0) {
            width = 48;
            height = 48;
            imageView.setFitWidth(width);
            imageView.setFitHeight(height);
        }

        double radius = Math.min(width, height) / 2;
        double centerX = width / 2;
        double centerY = height / 2;

        Circle clip = new Circle(centerX, centerY, radius);
        imageView.setClip(clip);
    }

    @FXML
    private void sendImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh để gửi");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        Window window = chatBox.getScene().getWindow();
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            addMessageWithType(file.getAbsolutePath(), true, Message.Type.IMAGE, file.getName());
            // Bot phản hồi lại
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> {
                        addMessageWithType("Tôi đã nhận được ảnh của bạn!", false, Message.Type.TEXT, null);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @FXML
    private void sendFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file để gửi");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tài liệu", "*.pdf", "*.doc", "*.docx", "*.xls", "*.xlsx", "*.txt", "*.ppt", "*.pptx","*.mp4"),
                new FileChooser.ExtensionFilter("Tất cả các file", "*.*")
        );
        Window window = chatBox.getScene().getWindow();
        File file = fileChooser.showOpenDialog(window);
        if (file != null) {
            try {
                File chatFilesDir = new File(CHAT_FILES_DIR);
                if (!chatFilesDir.exists()) chatFilesDir.mkdirs();
                File dest = new File(chatFilesDir, System.currentTimeMillis() + "_" + file.getName());
                Files.copy(file.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                addMessageWithType(dest.getAbsolutePath(), true, Message.Type.FILE, dest.getName());
                // Bot phản hồi lại
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            addMessageWithType("Tôi đã nhận được file của bạn!", false, Message.Type.TEXT, null);
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    private void sendFolder() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Chọn thư mục để gửi");
        Window window = chatBox.getScene().getWindow();
        File folder = directoryChooser.showDialog(window);
        if (folder != null) {
            try {
                File chatFilesDir = new File(CHAT_FILES_DIR);
                if (!chatFilesDir.exists()) chatFilesDir.mkdirs();
                File dest = new File(chatFilesDir, System.currentTimeMillis() + "_" + folder.getName());
                org.apache.commons.io.FileUtils.copyDirectory(folder, dest);
                addMessageWithType(dest.getAbsolutePath(), true, Message.Type.FOLDER, dest.getName());
                // Bot phản hồi lại
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            addMessageWithType("Tôi đã nhận được thư mục của bạn!", false, Message.Type.TEXT, null);
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void addMessageWithType(String content, boolean isSent, Message.Type type, String fileName) {
        Message newMessage = new Message(content, isSent, type, fileName);
        messageHistory.add(newMessage);
        showMessage(newMessage);
        messageArea.setVvalue(1.0);
        // Nếu là video thì bot cũng phản hồi lại
        if (isSent && type == Message.Type.VIDEO) {
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                    javafx.application.Platform.runLater(() -> {
                        addMessageWithType("Tôi đã nhận được video của bạn!", false, Message.Type.TEXT, null);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private String readableFileSize(long size) {
        if (size <= 0) return "0 B";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return String.format("%.2f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }

    private void showMessage(Message message) {
        VBox messageWrapper = new VBox(2);
        messageWrapper.setAlignment(message.isSent() ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        HBox messageBox = new HBox();
        messageBox.setAlignment(message.isSent() ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        switch (message.getType()) {
            case IMAGE:
                ImageView imgView = new ImageView(new File(message.getContent()).toURI().toString());
                imgView.setFitWidth(120);
                imgView.setPreserveRatio(true);
                imgView.setStyle("-fx-cursor: hand;");
                imgView.setOnMouseClicked(e -> showImagePopup(message.getContent()));
                messageBox.getChildren().add(imgView);
                break;
            case FOLDER:
                File folder = new File(message.getContent());
                HBox folderCard = new HBox(14);
                folderCard.setAlignment(Pos.CENTER_LEFT);
                folderCard.setStyle(
                        "-fx-background-color: #f7fafd;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 12;" +
                                "-fx-border-color: #e0e0e0;" +
                                "-fx-border-width: 1;" +
                                "-fx-effect: dropshadow(gaussian, #e0e0e0, 4, 0.2, 0, 2);" +
                                "-fx-cursor: hand;"
                );
                folderCard.setMaxWidth(220);
                URL folderIconUrl = getClass().getResource("/img/folder.png");
                ImageView folderIcon = folderIconUrl != null
                        ? new ImageView(new Image(folderIconUrl.toString()))
                        : new ImageView();
                folderIcon.setFitHeight(40);
                folderIcon.setFitWidth(40);
                VBox infoBox = new VBox(2);
                Label folderName = new Label(message.getFileName());
                folderName.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-text-overrun: ellipsis;");
                folderName.setMaxWidth(120);
                folderName.setMinWidth(60);
                folderName.setPrefWidth(120);
                folderName.setWrapText(false);
                long folderSize = folder.exists() ? org.apache.commons.io.FileUtils.sizeOfDirectory(folder) : 0;
                Label sizeLabel = new Label(readableFileSize(folderSize));
                sizeLabel.setStyle("-fx-text-fill: #888; -fx-font-size: 12;");
                HBox statusBox = new HBox(6);
                statusBox.setAlignment(Pos.CENTER_LEFT);
                Label statusLabel = new Label(folder.exists() ? "Đã có trên máy" : "Chưa tải");
                statusLabel.setStyle("-fx-text-fill: #4caf50; -fx-font-size: 12;");
                statusBox.getChildren().add(statusLabel);
                infoBox.getChildren().addAll(folderName, sizeLabel, statusBox);
                URL downloadIconUrl = getClass().getResource("/img/download.png");
                ImageView downloadIcon = downloadIconUrl != null
                        ? new ImageView(new Image(downloadIconUrl.toString(), 24, 24, true, true))
                        : new ImageView();
                Button downloadBtn = new Button();
                downloadBtn.setGraphic(downloadIcon);
                downloadBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                downloadBtn.setOnAction(e -> saveFolderToDisk(folder));
                downloadBtn.setOnMouseEntered(e -> downloadBtn.setStyle("-fx-background-color: #e3f2fd; -fx-cursor: hand;"));
                downloadBtn.setOnMouseExited(e -> downloadBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;"));
                folderCard.getChildren().addAll(folderIcon, infoBox, downloadBtn);
                folderCard.setOnMouseClicked(e -> saveFolderToDisk(folder));
                HBox.setMargin(folderCard, new Insets(0, message.isSent() ? 0 : 60, 0, message.isSent() ? 60 : 0));
                messageBox.getChildren().add(folderCard);
                break;
            case FILE:
                File file = new File(message.getContent());
                HBox fileCard = new HBox(14);
                fileCard.setAlignment(Pos.CENTER_LEFT);
                fileCard.setStyle(
                        "-fx-background-color: #f7fafd;" +
                                "-fx-border-radius: 10;" +
                                "-fx-background-radius: 10;" +
                                "-fx-padding: 12;" +
                                "-fx-border-color: #e0e0e0;" +
                                "-fx-border-width: 1;" +
                                "-fx-effect: dropshadow(gaussian, #e0e0e0, 4, 0.2, 0, 2);" +
                                "-fx-cursor: hand;"
                );
                fileCard.setMaxWidth(220);
                URL fileIconUrl = getClass().getResource("/img/file.png");
                ImageView fileIcon = fileIconUrl != null
                        ? new ImageView(new Image(fileIconUrl.toString()))
                        : new ImageView();
                fileIcon.setFitHeight(40);
                fileIcon.setFitWidth(40);
                VBox infoBox2 = new VBox(2);
                Label fileName = new Label(message.getFileName());
                fileName.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-text-overrun: ellipsis;");
                fileName.setMaxWidth(120);
                fileName.setMinWidth(60);
                fileName.setPrefWidth(120);
                fileName.setWrapText(false);
                Label fileSize = new Label(file.exists() ? readableFileSize(file.length()) : "0 B");
                fileSize.setStyle("-fx-text-fill: #888; -fx-font-size: 12;");
                HBox statusBox2 = new HBox(6);
                statusBox2.setAlignment(Pos.CENTER_LEFT);
                Label fileStatus = new Label(file.exists() ? "Đã có trên máy" : "Chưa tải");
                fileStatus.setStyle("-fx-text-fill: #4caf50; -fx-font-size: 12;");
                statusBox2.getChildren().add(fileStatus);
                HBox.setMargin(fileCard, new Insets(0, message.isSent() ? 0 : 60, 0, message.isSent() ? 60 : 0));
                messageBox.getChildren().add(fileCard);
                break;
            case LINK:
                Hyperlink link = new Hyperlink(message.getContent());
                link.getStyleClass().add(message.isSent() ? "message-bubble-me" : "message-bubble-other");
                link.setWrapText(true);
                link.setMaxWidth(240);
                link.setOnAction(e -> {
                    if (hostServices != null) {
                        String url = message.getContent();
                        if (!url.startsWith("http://") && !url.startsWith("https://")) {
                            url = "https://" + url;
                        }
                        hostServices.showDocument(url);
                    } else {
                        System.err.println("HostServices not initialized!");
                    }
                });
                messageBox.getChildren().add(link);
                break;
            default:
                Label messageLabel = new Label(message.getContent());
                messageLabel.getStyleClass().add(message.isSent() ? "message-bubble-me" : "message-bubble-other");
                messageLabel.setWrapText(true);
                messageLabel.setMaxWidth(240);
                messageBox.getChildren().add(messageLabel);
        }
        Label timestampLabel = new Label(message.getTimestamp().format(TIME_FORMATTER));
        timestampLabel.getStyleClass().add("message-timestamp");
        timestampLabel.setAlignment(message.isSent() ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        messageWrapper.getChildren().addAll(messageBox, timestampLabel);
        messageContainer.getChildren().add(messageWrapper);
        applyMessageAnimation(messageWrapper, message.isSent());
    }

    private void showImagePopup(String imagePath) {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        ImageView imageView = new ImageView(new Image(new File(imagePath).toURI().toString()));
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(600);
        imageView.setFitHeight(600);
        VBox root = new VBox(imageView);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #222;");
        Scene scene = new Scene(root, 700, 700);
        popup.setScene(scene);
        popup.setTitle("Image");
        popup.showAndWait();
    }

    private void saveFolderToDisk(File folder) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Chọn nơi lưu thư mục");
        File destDir = dirChooser.showDialog(chatBox.getScene().getWindow());
        if (destDir != null) {
            try {
                File destFolder = new File(destDir, folder.getName());
                FileUtils.copyDirectory(folder, destFolder);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}