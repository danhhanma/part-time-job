package danhhanma.part_time_job.JobPost;


import danhhanma.part_time_job.Utils.Config;
import danhhanma.part_time_job.Utils.LocalStorage;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Callback;
import org.json.JSONObject;

import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobPostController {
    @FXML
    private Label addressErr;
    @FXML
    private TextField titleField;
    @FXML
    private Label titleErr;
    @FXML
    private TextArea overViewArea;

    @FXML
    private ListView<File> picturesList;

    private List<File> selectedPictures;
    @FXML
    private VBox fieldsContainer;

    @FXML
    private Label keyField;
    @FXML
    private Label overViewErr;
    @FXML
    private ArrayList<Label> errors=new ArrayList<>();
    @FXML

    private TextArea valueArea;
    @FXML
    private final Map<TextField, TextArea> descriptionMap = new HashMap<>();
    @FXML
    public void initialize() {
        // Render each file in ListView as an image thumbnail
        picturesList.setCellFactory(new Callback<>() {
            @Override
            public ListCell<File> call(ListView<File> param) {
                return new ListCell<>() {
                    private final ImageView imageView = new ImageView();

                    @Override
                    protected void updateItem(File file, boolean empty) {
                        super.updateItem(file, empty);
                        if (empty || file == null) {
                            setGraphic(null);
                        } else {
                            Image image = new Image(file.toURI().toString(), 100, 100, true, true);
                            imageView.setImage(image);
                            setGraphic(imageView);
                        }
                    }
                };
            }
        });
    }

    @FXML
    public void onSelectPictures() {
        System.out.println("Select Pictures button clicked");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Workspace Pictures");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        List<File> files = fileChooser.showOpenMultipleDialog(getWindow());
        if (files != null) {
            selectedPictures = files;
            picturesList.getItems().clear();
            picturesList.getItems().addAll(files);
        }
    }
    private byte[] buildMultipartBody(String title, JSONObject description, List<File> pictures, String boundary) throws IOException {
        var lineSep = "\r\n";
        var out = new ByteArrayOutputStream();
        var writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8), true);

// Title part
        writer.append("--").append(boundary).append(lineSep);
        writer.append("Content-Disposition: form-data; name=\"title\"").append(lineSep);
        writer.append("Content-Type: text/plain; charset=UTF-8").append(lineSep).append(lineSep);
        writer.append(title).append(lineSep);

// Description part
        writer.append("--").append(boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"description\"").append("\r\n");
        writer.append("Content-Type: application/json; charset=UTF-8").append("\r\n").append("\r\n");
        writer.append(description.toString()).append("\r\n");

// Pictures
        if (pictures != null) {
            for (File file : pictures) {
                String fileName = file.getName();
                String mimeType = Files.probeContentType(file.toPath());
                if (mimeType == null) mimeType = "application/octet-stream";

                writer.append("--").append(boundary).append(lineSep);
                writer.append("Content-Disposition: form-data; name=\"files\"; filename=\"").append(fileName).append("\"").append(lineSep);
                writer.append("Content-Type: ").append(mimeType).append(lineSep);
                writer.append("Content-Transfer-Encoding: binary").append(lineSep).append(lineSep);
                writer.flush();

                Files.copy(file.toPath(), out);  // binary content
                out.write(lineSep.getBytes(StandardCharsets.UTF_8));
                writer.flush();
            }
        }

// End boundary
        writer.append("--").append(boundary).append("--").append(lineSep);
        writer.flush();

        return out.toByteArray();

    }

    @FXML
    public void onSubmitPost() throws IOException {
        titleErr.setText("");
        overViewErr.setText("");
        addressErr.setText("");
        for (Label error : errors) {
            error.setText("");
        }
        String title = titleField.getText();
        JSONObject description = new JSONObject();
        description.put("location", overViewArea.getText());
        if(title.isEmpty()) {
            titleErr.setText("Vui lòng nhập tiêu đề bài đăng");
        }
        else if(overViewArea.getText().isEmpty()) {
            overViewErr.setText("vui lòng nhập mô tả bài đăng");
        }
        else if(valueArea.getText().isEmpty()) {
            addressErr.setText("vui lòng nhập địa chỉ");
        }
        else if(!descriptionMap.isEmpty()&&(descriptionMap.keySet().stream().toList().get(descriptionMap.size()-1).getText().isEmpty()||descriptionMap.values().stream().toList().get(descriptionMap.size()-1).getText().isEmpty())){
            errors.get(descriptionMap.size()-1).setText("Vui lòng nhập đầy đủ thông tin trước khi thêm");
        }
        else {
            String token= LocalStorage.loadToken();
            try {

                String apiUrl = Config.get("api.url")+ "/post/addPost";
                String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();

                byte[] body = buildMultipartBody(title, description, selectedPictures, boundary);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                        .header("token", token)
                        .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                        .build();
                HttpClient client = HttpClient.newHttpClient();
                System.out.println(request.uri().toString());
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> System.out.println("Response: " + response.body()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @FXML
    public void handleAddField() {
        String preValue="";
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");
        errors.add(errorLabel);
        String preKey="";
        if(descriptionMap.isEmpty()) System.out.println("wcddcwcewecewc");
        if((valueArea.getText().isEmpty())) {
            addressErr.setText("Vui lòng nhập đầy đủ thông tin trước khi thêm");
            return;
        }
        else if(!descriptionMap.isEmpty()){
            System.out.println("Vui lòng nhập đầy đủ thông tin trước khi thêm");
            preKey=descriptionMap.keySet().stream().toList().get(descriptionMap.size()-1).getText();
            preValue=descriptionMap.values().stream().toList().get(descriptionMap.size()-1).getText();
            if(preValue.isEmpty()||preKey.isEmpty()){

                errors.get(descriptionMap.size()-1).setText("Vui lòng nhập đầy đủ thông tin trước khi thêm");
                return;
            }
        }
        HBox fieldRow = new HBox(10);
        fieldRow.setPadding(new Insets(5));

        TextField keyField = new TextField();
        TextArea valueArea = new TextArea();
        descriptionMap.put(keyField, valueArea);
        valueArea.setPrefHeight(50);
        valueArea.setPrefWidth(230);
        valueArea.setPromptText("Tên thuộc tính (VD: Bằng cấp)");
        keyField.setPrefHeight(50);
        keyField.setPrefWidth(185);
        keyField.setPromptText("Giá trị thuộc tính (VD: Bằng Cử Nhân)");

        Button removeButton = new Button("Xóa");
        removeButton.setOnAction(e -> {
            fieldsContainer.getChildren().remove(fieldRow);
            fieldsContainer.getChildren().remove(errorLabel);
            descriptionMap.remove(keyField);
        });

        fieldRow.getChildren().addAll(keyField, valueArea, removeButton);

        fieldsContainer.getChildren().add(fieldRow);
        fieldsContainer.getChildren().add(errorLabel);
        keyField.clear();
        valueArea.clear();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private Window getWindow() {
        return titleField.getScene().getWindow();
    }

    public void handleSubmit(){
        if(titleField.getText().isEmpty()){
            titleErr.setText("Vui lòng nhập tiêu đề bài đăng");
        }
        else if(keyField.getText().isEmpty()||valueArea.getText().isEmpty()){}
    }
}
