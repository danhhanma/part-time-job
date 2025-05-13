package danhhanma.part_time_job.JobPost;


import danhhanma.part_time_job.objects.post.Account;
import danhhanma.part_time_job.objects.post.Post;
import danhhanma.part_time_job.objects.post.PostAudience;
import danhhanma.part_time_job.objects.post.Reactions;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class PostController implements Initializable {
    @FXML
    private ImageView imgProfile;

    @FXML
    private Label username;

    @FXML
    private ImageView imgVerified;

    @FXML
    private Label date;

    @FXML
    private ImageView audience;

    @FXML
    private TextFlow captionFlow;

    @FXML
    private GridPane imagesGrid;

    @FXML
    private Label nbReactions;

    @FXML
    private Label nbComments;

    @FXML
    private Label nbShares;

    @FXML
    private HBox reactionsContainer;

    @FXML
    private ImageView imgLike;

    @FXML
    private ImageView imgLove;

    @FXML
    private ImageView imgCare;

    @FXML
    private ImageView imgHaha;

    @FXML
    private ImageView imgWow;

    @FXML
    private ImageView imgSad;

    @FXML
    private ImageView imgAngry;

    @FXML
    private HBox likeContainer;

    @FXML
    private ImageView imgReaction;

    @FXML
    private Label reactionName;

    @FXML
    private HBox topReactionsContainer;

    @FXML
    private StackPane postStats;


    private long startTime = 0;
    private Map<Reactions, Integer> reactionCounts = new HashMap<>();
    private Reactions currentReaction;
    private Post post;
    private Timeline showReactionsTimeline;
    private boolean isMouseInReactionsContainer = false;

    @FXML
    public void onLikeContainerPressed(MouseEvent me) {
        startTime = System.currentTimeMillis();
    }

    @FXML
    public void onLikeContainerMouseReleased(MouseEvent me) {
        if (System.currentTimeMillis() - startTime > 500) {
            if (showReactionsTimeline != null) {
                showReactionsTimeline.stop();
            }
            showReactionsTimeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
                if (!isMouseInReactionsContainer) {
                    reactionsContainer.setVisible(true);
                }
            }));
            showReactionsTimeline.play();
        } else {
            if (reactionsContainer.isVisible()) {
                reactionsContainer.setVisible(false);
            }
            if (currentReaction == Reactions.NON) {
                setReaction(Reactions.LIKE);
            } else {
                setReaction(Reactions.NON);
            }
        }
    }

    @FXML
    public void onLikeContainerMouseExited(MouseEvent me) {
        if (showReactionsTimeline != null) {
            showReactionsTimeline.stop();
        }
    }

    @FXML
    public void onReactionsContainerMouseEntered(MouseEvent me) {
        isMouseInReactionsContainer = true;
    }

    @FXML
    public void onReactionsContainerMouseExited(MouseEvent me) {
        isMouseInReactionsContainer = false;
    }

    @FXML
    public void onReactionImgPressed(MouseEvent me) {
        switch (((ImageView) me.getSource()).getId()) {
            case "imgLove":
                setReaction(Reactions.LOVE);
                break;
            case "imgCare":
                setReaction(Reactions.CARE);
                break;
            case "imgHaha":
                setReaction(Reactions.HAHA);
                break;
            case "imgWow":
                setReaction(Reactions.WOW);
                break;
            case "imgSad":
                setReaction(Reactions.SAD);
                break;
            case "imgAngry":
                setReaction(Reactions.ANGRY);
                break;
            default:
                setReaction(Reactions.LIKE);
                break;
        }
        reactionsContainer.setVisible(false);
    }

    public void setReaction(Reactions reaction) {
        // Nếu đang có reaction và chọn lại reaction đó thì bỏ reaction
        if (currentReaction == reaction) {
            reaction = Reactions.NON;
        }

        // Cập nhật số lượt reaction
        if (currentReaction != Reactions.NON) {
            reactionCounts.put(currentReaction, reactionCounts.getOrDefault(currentReaction, 1) - 1);
        }
        if (reaction != Reactions.NON) {
            reactionCounts.put(reaction, reactionCounts.getOrDefault(reaction, 0) + 1);
        }

        // Cập nhật UI
        Image image = new Image(getClass().getResourceAsStream(reaction.getImgSrc()));
        imgReaction.setImage(image);
        reactionName.setText(reaction.getName());
        reactionName.setTextFill(Color.web(reaction.getColor()));

        // Cập nhật tổng số reactions
        int totalReactions = reactionCounts.values().stream().mapToInt(Integer::intValue).sum();
        post.setTotalReactions(totalReactions);
        
        // Cập nhật hiển thị số reactions
        if (totalReactions > 0) {
            nbReactions.setText(String.valueOf(totalReactions));
            nbReactions.setVisible(true);
        } else {
            nbReactions.setVisible(false);
        }

        currentReaction = reaction;

        // Cập nhật hiển thị 3 reactions phổ biến nhất
        updateTopReactions();
    }

    private void updateTopReactions() {
        // Lấy 3 reactions có số lượt cao nhất
        List<Map.Entry<Reactions, Integer>> topReactions = reactionCounts.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted(Map.Entry.<Reactions, Integer>comparingByValue().reversed())
                .limit(3)
                .collect(Collectors.toList());

        // Xóa các reactions cũ
        topReactionsContainer.getChildren().clear();

        // Thêm các reactions mới
        for (Map.Entry<Reactions, Integer> entry : topReactions) {
            ImageView reactionIcon = new ImageView(new Image(getClass().getResourceAsStream(entry.getKey().getImgSrc())));
            reactionIcon.setFitHeight(28);
            reactionIcon.setFitWidth(28);
            topReactionsContainer.getChildren().add(reactionIcon);
        }

        // Hiển thị hoặc ẩn container và số reactions dựa trên số lượng reactions
        boolean hasReactions = !topReactions.isEmpty();
        topReactionsContainer.setVisible(hasReactions);
        nbReactions.setVisible(hasReactions);
    }

    public void setData(Post post){
        this.post = post;
        Image img;
        img = new Image(getClass().getResourceAsStream(post.getAccount().getProfileImg()));
        imgProfile.setImage(img);
        applyCircularClip(imgProfile);
        username.setText(post.getAccount().getName());
        if(post.getAccount().isVerified()){
            imgVerified.setVisible(true);
        }else{
            imgVerified.setVisible(false);
        }

        date.setText(post.getDate());
        if(post.getAudience() == PostAudience.PUBLIC){
            img = new Image(getClass().getResourceAsStream(PostAudience.PUBLIC.getImgSrc()));
        }else{
            img = new Image(getClass().getResourceAsStream(PostAudience.FRIENDS.getImgSrc()));
        }
        audience.setImage(img);

        // Xử lý caption với TextFlow để hỗ trợ tiếng Việt và xuống dòng
        captionFlow.getChildren().clear();
        if(post.getCaption() != null && !post.getCaption().isEmpty()){
            String[] lines = post.getCaption().split("\\r?\\n");
            for (int i = 0; i < lines.length; i++) {
                Text t = new Text(lines[i]);
                t.setFont(Font.font("Segoe UI", 18));
                t.setFill(Color.web("#1C1E21"));
                captionFlow.getChildren().add(t);
                if (i < lines.length - 1) {
                    captionFlow.getChildren().add(new Text(System.lineSeparator()));
                }
            }
            captionFlow.setMaxWidth(560);
            captionFlow.setVisible(true);
            captionFlow.setManaged(true);
        } else {
            captionFlow.setVisible(false);
            captionFlow.setManaged(false);
        }

        // Xử lý danh sách ảnh
        List<String> images = post.getImages();
        imagesGrid.getChildren().clear();
        if (images != null && !images.isEmpty()) {
            if (images.size() == 1) {
                // Trường hợp 1 ảnh: Hiển thị full kích thước
                ImageView imageView = new ImageView();
                imageView.setFitWidth(560);
                imageView.setFitHeight(560);
                imageView.setPreserveRatio(false);
                imageView.setImage(new Image(getClass().getResourceAsStream(images.get(0))));
                imageView.setOnMouseClicked(event -> showFullImage(images, 0));
                imagesGrid.add(imageView, 0, 0);
            } else if (images.size() == 2) {
                // Trường hợp 2 ảnh: Lưới 2x1, mỗi ảnh 280x280px
                for (int i = 0; i < 2; i++) {
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(280);
                    imageView.setFitHeight(280);
                    imageView.setPreserveRatio(false);
                    imageView.setImage(new Image(getClass().getResourceAsStream(images.get(i))));
                    final int index = i;
                    imageView.setOnMouseClicked(event -> showFullImage(images, index));
                    imagesGrid.add(imageView, i, 0);
                }
            } else if (images.size() == 3) {
                // Trường hợp 3 ảnh: Ảnh đầu tiên full, hai ảnh còn lại 1x2 bên dưới
                ImageView firstImage = new ImageView();
                firstImage.setFitWidth(560);
                firstImage.setFitHeight(280);
                firstImage.setPreserveRatio(false);
                firstImage.setImage(new Image(getClass().getResourceAsStream(images.get(0))));
                firstImage.setOnMouseClicked(event -> showFullImage(images, 0));
                imagesGrid.add(firstImage, 0, 0, 2, 1);

                for (int i = 1; i < 3; i++) {
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(280);
                    imageView.setFitHeight(280);
                    imageView.setPreserveRatio(false);
                    imageView.setImage(new Image(getClass().getResourceAsStream(images.get(i))));
                    final int index = i;
                    imageView.setOnMouseClicked(event -> showFullImage(images, index));
                    imagesGrid.add(imageView, i - 1, 1);
                }
            } else {
                // Trường hợp 4+ ảnh: Lưới 2x2, mỗi ảnh 280x280px
                int maxImages = Math.min(images.size(), 4);
                for (int i = 0; i < maxImages; i++) {
                    int row = i / 2;
                    int col = i % 2;
                    ImageView imageView = new ImageView();
                    imageView.setFitWidth(280);
                    imageView.setFitHeight(280);
                    imageView.setPreserveRatio(false);
                    imageView.setImage(new Image(getClass().getResourceAsStream(images.get(i))));
                    final int index = i;
                    imageView.setOnMouseClicked(event -> showFullImage(images, index));
                    imagesGrid.add(imageView, col, row);
                }
                if (images.size() > 4) {
                    Label plusLabel = new Label("+" + (images.size() - 4));
                    plusLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.5); -fx-alignment: center;");
                    plusLabel.setMinSize(280, 280);
                    plusLabel.setMaxSize(280, 280);
                    plusLabel.setOnMouseClicked(event -> showFullImage(images, 3));
                    imagesGrid.add(plusLabel, 1, 1);
                }
            }
            imagesGrid.setVisible(true);
            imagesGrid.setManaged(true);
        } else {
            imagesGrid.setVisible(false);
            imagesGrid.setManaged(false);
        }

        // Reset và ẩn reactions
        reactionCounts.clear();
        currentReaction = Reactions.NON;
        topReactionsContainer.getChildren().clear();
        topReactionsContainer.setVisible(false);
        nbReactions.setVisible(false);
        
        // Set default reaction UI
        Image defaultImage = new Image(getClass().getResourceAsStream(Reactions.NON.getImgSrc()));
        imgReaction.setImage(defaultImage);
        reactionName.setText(Reactions.NON.getName());
        reactionName.setTextFill(Color.web(Reactions.NON.getColor()));

        nbComments.setText(post.getNbComments() + " comments");
        nbShares.setText(post.getNbShares() + " shares");
    }

    private void showFullImage(List<String> images, int startIndex) {
        Stage popupStage = new Stage();
        popupStage.setTitle("Xem ảnh chi tiết");

        // Tạo ImageView để hiển thị ảnh
        ImageView fullImageView = new ImageView();
        fullImageView.setFitWidth(800);
        fullImageView.setFitHeight(600);
        fullImageView.setPreserveRatio(true);

        // Tạo biến để theo dõi ảnh hiện tại
        final int[] currentIndex = {startIndex};
        fullImageView.setImage(new Image(getClass().getResourceAsStream(images.get(currentIndex[0]))));

        // Tạo Label để hiển thị chỉ số ảnh (ví dụ: 1/5)
        Label indexLabel = new Label((currentIndex[0] + 1) + "/" + images.size());
        indexLabel.setStyle("-fx-font-size: 16; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.5); -fx-padding: 5;");

        // Sử dụng StackPane để đặt ảnh và chỉ số
        StackPane root = new StackPane();
        root.getChildren().addAll(fullImageView, indexLabel);
        StackPane.setAlignment(indexLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(indexLabel, new Insets(0, 0, 10, 0)); // Cách dưới cùng 10px
        root.setStyle("-fx-background-color: black;");

        // Xử lý sự kiện bàn phím
        root.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.LEFT) {
                if (currentIndex[0] > 0) {
                    currentIndex[0]--;
                    fullImageView.setImage(new Image(getClass().getResourceAsStream(images.get(currentIndex[0]))));
                    indexLabel.setText((currentIndex[0] + 1) + "/" + images.size());
                }
            } else if (event.getCode() == KeyCode.RIGHT) {
                if (currentIndex[0] < images.size() - 1) {
                    currentIndex[0]++;
                    fullImageView.setImage(new Image(getClass().getResourceAsStream(images.get(currentIndex[0]))));
                    indexLabel.setText((currentIndex[0] + 1) + "/" + images.size());
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                popupStage.close();
            }
        });

        Scene scene = new Scene(root, 800, 600);
        popupStage.setScene(scene);
        popupStage.setOnShown(e -> root.requestFocus()); // Đảm bảo root nhận focus để xử lý phím
        popupStage.show();
    }

    private Post getPost(){
        Post post = new Post();
        Account account = new Account();
        account.setName("Nguyễn Tấn Phát");
        account.setProfileImg("/img/393123651_1490098338498694_7534203730929247714_n.jpg");
        account.setVerified(true);
        post.setAccount(account);
        post.setDate("May 9, 2025 at 10:00 PM");
        post.setAudience(PostAudience.PUBLIC);
        post.setCaption("Thấy tôi đẹp trai không mọi người ahihihi.\n Đẹp trai thế này mà ai chê cho được");
        post.setImages(Arrays.asList(
                "/img/cc.jpg",
                "/img/cc.jpg",
                "/img/cc.jpg",
                "/img/cc.jpg",
                "/img/cc.jpg"
        ));

        // Reset số lượt reactions về 0
        post.setTotalReactions(0);
        post.setNbComments(0);
        post.setNbShares(0);

        return post;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setData(getPost());

        if(nbComments.getText().equals("0 comments")){
            nbComments.setVisible(false);
        }
        if(nbShares.getText().equals("0 shares")){
            nbShares.setVisible(false);
        }

        // Thêm listener để đợi scene được tạo
        reactionsContainer.sceneProperty().addListener((observable, oldValue, newScene) -> {
            if (newScene != null) {
                // Thêm sự kiện click outside để đóng reactions container
                newScene.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                    if (!reactionsContainer.isHover() && !likeContainer.isHover()) {
                        reactionsContainer.setVisible(false);
                    }
                });
            }
        });
    }
}