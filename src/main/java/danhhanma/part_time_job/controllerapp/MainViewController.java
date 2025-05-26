package danhhanma.part_time_job.controllerapp;

import danhhanma.part_time_job.Utils.Config;
import danhhanma.part_time_job.Utils.LocalStorage;
import danhhanma.part_time_job.application.ApplicantView;
import danhhanma.part_time_job.application.EmployerView;
import danhhanma.part_time_job.dashboard.ApplicantController;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MainViewController {

    @FXML
    private StackPane rootPane;

    @FXML
    private StackPane roundedFormContainer;

    @FXML
    private VBox loginForm;

    @FXML
    private VBox registerForm;

    @FXML
    private VBox supplementaryForm;

    @FXML
    private TextField loginEmailField;

    @FXML
    private PasswordField loginPasswordField;

    @FXML
    private TextField regNameField;

    @FXML
    private TextField regEmailField;

    @FXML
    private ComboBox<String> regRoleComboBox;
    @FXML
    private ComboBox<String> regGenderComboBox;


    @FXML
    private ComboBox<String> orgTypeComboBox;

    @FXML
    private TextField otherOrgTypeField;

    @FXML
    private PasswordField regPasswordField;

    @FXML
    private PasswordField regConfirmPasswordField;

    @FXML
    private Button employerLoginButton;

    @FXML
    private Button jobSeekerLoginButton;

    @FXML
    private Button registerButton;

    @FXML
    private Button submitSupplementaryButton;

    @FXML
    private Hyperlink registerLink;

    @FXML
    private Hyperlink loginLink;

    @FXML
    private Hyperlink backToRegisterLink;

    // Biến tạm lưu thông tin đăng ký nhà tuyển dụng
    private String tempRegName;
    private String tempRegEmail;
    private String tempGender;
    private String tempRegPassword;
    private String tempRegRetypePass;

    @FXML
    private void initialize() {
        // Mặc định hiển thị loginForm
        loginForm.setVisible(true);
        loginForm.setManaged(true);
        registerForm.setVisible(false);
        registerForm.setManaged(false);
        supplementaryForm.setVisible(false);
        supplementaryForm.setManaged(false);
        setupRoleComboBox();
        setUpGenderComboBox();
        setupOrgTypeComboBox();
        setupButtonActions();
    }

    private void setupRoleComboBox() {
        regRoleComboBox.setItems(FXCollections.observableArrayList(
                "Người tìm việc",
                "Nhà tuyển dụng"
        ));
    }
    private void setUpGenderComboBox() {
        regGenderComboBox.setItems(FXCollections.observableArrayList(
                "Nam",
                "Nữ",
                "không muốn tiết lộ!"
        ));
    }
    private void setupOrgTypeComboBox() {
        orgTypeComboBox.setItems(FXCollections.observableArrayList(
                "Company","Shop","Restaurant","Supermarket","Hotel",
                "School","Hospital","Recruiter","Government","NGO",
                "Startup","Event Organizer","Construction","Transportation",
                "Salon","Gym","Farm","Entertainment","E-commerce","individual","OTHER"
        ));
        // Xử lý khi chọn loại cơ quan
        orgTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if ("OTHER".equals(newValue)) {
                otherOrgTypeField.setVisible(true);
                otherOrgTypeField.setManaged(true);
            } else {
                otherOrgTypeField.setVisible(false);
                otherOrgTypeField.setManaged(false);
                otherOrgTypeField.clear();
            }
        });
    }

    private void setupButtonActions() {
        employerLoginButton.setOnAction(event -> handleLogin("employer"));
        jobSeekerLoginButton.setOnAction(event -> handleLogin("jobseeker"));
        registerButton.setOnAction(event -> handleRegister());
        submitSupplementaryButton.setOnAction(event -> handleSupplementarySubmit());
        registerLink.setOnAction(event -> switchToRegister());
        loginLink.setOnAction(event -> switchToLogin());
        backToRegisterLink.setOnAction(event -> switchToRegisterFromSupplementary());
    }

    private void handleLogin(String role) {
        if (validateLoginInput()) {
            String email = loginEmailField.getText();
            String password = loginPasswordField.getText();
            String emailregex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if (!email.matches(emailregex)) {
                showError("Email không hợp lệ");
                return;
            }

            String apiUrl;
            if ("employer".equals(role)) {
                apiUrl = Config.get("api.url") + "/auth/login/employer";
            } else {
                apiUrl = Config.get("api.url") + "/auth/login/applicant";
            }

            try {
                JSONObject loginJson = new JSONObject();
                loginJson.put("email", email);
                loginJson.put("password", password);

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(loginJson.toString()))
                        .build();

                HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> {
                            if (response.statusCode() == 200) {
                                JSONObject responseBody = new JSONObject(response.body());

                                String token = responseBody.getString("token");
                                try {
                                    String userName;
                                    String avatarPath = "/img/user.png"; // Mặc định sử dụng ảnh mặc định
                                    JSONObject user = responseBody.getJSONObject("user");
                                    if ("employer".equals(role)) {
                                        LocalStorage.clearAll();
                                        JSONObject employer = responseBody.getJSONObject("employer");
                                        userName = user.getString("fullName");
                                        long userId = user.getLong("id");
                                        String employerId = employer.getString("id");
                                        LocalStorage.saveEmployerId(employerId);
                                        LocalStorage.saveUserId(userId);
                                        LocalStorage.saveUserName(userName);
                                        
                                        // Nếu có avatar trong response, sử dụng nó
                                        if (user.has("avatar")) {
                                            avatarPath = user.getString("avatar");
                                        }
                                    } else {
                                        LocalStorage.clearAll();
                                        JSONObject applicant = responseBody.getJSONObject("applicant");
                                        userName = user.getString("fullName");
                                        long userId = user.getLong("id");
                                        String applicantId = applicant.getString("id");
                                        LocalStorage.saveApplicantId(applicantId);
                                        LocalStorage.saveUserId(userId);
                                        LocalStorage.saveUserName(userName);

                                        // Nếu có avatar trong response, sử dụng nó
                                        if (user.has("avatar")) {
                                            avatarPath = user.getString("avatar");
                                        }
                                    }
                                    LocalStorage.saveAvatarPath(avatarPath);
                                    LocalStorage.saveToken(token);
                                    
                                    // Mở dashboard trên JavaFX thread
                                    if("applicant".equals(role))Platform.runLater(() -> {
                                        try {
                                            Stage currentStage = (Stage) rootPane.getScene().getWindow();
                                            currentStage.close();

                                            ApplicantView dashboard = new ApplicantView();
                                            Stage dashboardStage = new Stage();
                                            dashboard.start(dashboardStage);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            showError("Lỗi mở dashboard: " + e.getMessage());
                                        }
                                    });
                                    else Platform.runLater(() -> {
                                        try {
                                            Stage currentStage = (Stage) rootPane.getScene().getWindow();
                                            currentStage.close();

                                            EmployerView dashboard = new EmployerView();
                                            Stage dashboardStage = new Stage();
                                            dashboard.start(dashboardStage);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            showError("Lỗi mở dashboard: " + e.getMessage());
                                        }
                                    });
                                } catch (IOException e) {
                                    Platform.runLater(() -> showError("Lỗi lưu thông tin đăng nhập: " + e.getMessage()));
                                }
                            } else {
                                JSONObject error = new JSONObject(response.body());
                                String message = error.has("message") ? error.getString("message") : "Đăng nhập thất bại";
                                Platform.runLater(() -> showError(message));
                            }
                        })
                        .exceptionally(ex -> {
                            javafx.application.Platform.runLater(() -> showError("Lỗi kết nối: " + ex.getMessage()));
                            return null;
                        });
            } catch (Exception e) {
                showError("Lỗi: " + e.getMessage());
            }
        }
    }

    private ApplicantController getFacebookController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/danhhanma/part_time_job/ApplicantView.fxml"));
            loader.load();
            return loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void handleRegister() {
        if (!validateRegisterInput()) {
            showError("Vui lòng điền đầy đủ thông tin và đảm bảo mật khẩu khớp nhau!");
            return;
        }

        String selectedRole = regRoleComboBox.getValue();
        if ("Nhà tuyển dụng".equals(selectedRole)) {
            // Lưu tạm thông tin đăng ký
            tempRegName = regNameField.getText();
            tempRegEmail = regEmailField.getText();
            tempRegPassword = regPasswordField.getText();
            tempGender = regGenderComboBox.getValue();
            tempRegRetypePass = regConfirmPasswordField.getText();

            // Validate email format
            String emailregex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
            if (!tempRegEmail.matches(emailregex)) {
                showError("Email không hợp lệ");
                return;
            }

            // Validate password match
            if (!tempRegPassword.equals(tempRegRetypePass)) {
                showError("Mật khẩu không khớp");
                return;
            }

            // Chuyển sang form bổ sung
            switchToSupplementary();
        } else {
            tempRegName = regNameField.getText();
            tempRegEmail = regEmailField.getText();
            tempGender = regGenderComboBox.getValue();
            tempRegPassword = regPasswordField.getText();
            tempRegRetypePass = regConfirmPasswordField.getText();
            try {
                JSONObject registerJson = new JSONObject();
                registerJson.put("name", tempRegName);
                registerJson.put("email", tempRegEmail);
                registerJson.put("gender", tempGender);
                registerJson.put("password", tempRegPassword);
                registerJson.put("retypePass", tempRegRetypePass);

                String apiUrl = Config.get("api.url") + "/auth/register/applicant";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(registerJson.toString()))
                        .build();

                HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                        .thenAccept(response -> {
                            if (response.statusCode() == 200) {
                                javafx.application.Platform.runLater(() -> {
                                    showSuccess("Đăng ký thành công! Vui lòng đăng nhập.");
                                    switchToLogin();
                                });
                            } else {
                                JSONObject error = new JSONObject(response.body());
                                String message = error.has("message") ? error.getString("message") : "Đăng ký thất bại";
                                javafx.application.Platform.runLater(() -> showError(message));
                            }
                        })
                        .exceptionally(ex -> {
                            javafx.application.Platform.runLater(() -> showError("Lỗi kết nối: " + ex.getMessage()));
                            return null;
                        });
            } catch (Exception e) {
                showError("Lỗi: " + e.getMessage());
            }
        }
    }

    private void handleSupplementarySubmit() {
        // Validate loại cơ quan
        String employerType = orgTypeComboBox.getValue();
        if (employerType == null || employerType.isEmpty()) {
            showError("Vui lòng chọn loại cơ quan!");
            return;
        }
        if ("OTHER".equals(employerType)) {
            employerType = otherOrgTypeField.getText();
            if (employerType == null || employerType.isEmpty()) {
                showError("Vui lòng nhập loại cơ quan khác!");
                return;
            }
        }
        try {
            JSONObject registerJson = new JSONObject();
            registerJson.put("name", tempRegName);
            registerJson.put("email", tempRegEmail);
            registerJson.put("employerType", employerType);
            registerJson.put("gender", tempGender);
            registerJson.put("password", tempRegPassword);
            registerJson.put("retypePass", tempRegRetypePass);

            String apiUrl = Config.get("api.url") + "/auth/register/employer";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(registerJson.toString()))
                    .build();

            HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            javafx.application.Platform.runLater(() -> {
                                showSuccess("Đăng ký thành công! Vui lòng đăng nhập.");
                                switchToLogin();
                            });
                        } else {
                            JSONObject error = new JSONObject(response.body());
                            String message = error.has("message") ? error.getString("message") : "Đăng ký thất bại";
                            javafx.application.Platform.runLater(() -> showError(message));
                        }
                    })
                    .exceptionally(ex -> {
                        javafx.application.Platform.runLater(() -> showError("Lỗi kết nối: " + ex.getMessage()));
                        return null;
                    });
        } catch (Exception e) {
            showError("Lỗi: " + e.getMessage());
        }
    }

    private boolean validateLoginInput() {
        return !loginEmailField.getText().isEmpty() && !loginPasswordField.getText().isEmpty();
    }

    private boolean validateRegisterInput() {
        return !regNameField.getText().isEmpty() &&
                !regEmailField.getText().isEmpty() &&
                !regPasswordField.getText().isEmpty() &&
                regPasswordField.getText().equals(regConfirmPasswordField.getText()) &&
                regRoleComboBox.getValue() != null;
    }

    private boolean validateSupplementaryInput() {
        String orgType = orgTypeComboBox.getValue();
        if (orgType == null) {
            return false;
        }
        if ("Loại hình khác".equals(orgType) && otherOrgTypeField.getText().isEmpty()) {
            return false;
        }
        return true;
    }

    private void openDashboard(String role) {
        try {
            Stage currentStage = (Stage) rootPane.getScene().getWindow();
            currentStage.close();

            ApplicantView dashboard = new ApplicantView();
            Stage dashboardStage = new Stage();
            dashboard.start(dashboardStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchToRegister() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), loginForm);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            loginForm.setVisible(false);
            loginForm.setManaged(false);
            supplementaryForm.setVisible(false);
            supplementaryForm.setManaged(false);
            registerForm.setVisible(true);
            registerForm.setManaged(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), registerForm);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void switchToLogin() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), registerForm);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            registerForm.setVisible(false);
            registerForm.setManaged(false);
            supplementaryForm.setVisible(false);
            supplementaryForm.setManaged(false);
            loginForm.setVisible(true);
            loginForm.setManaged(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), loginForm);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void switchToSupplementary() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), registerForm);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            registerForm.setVisible(false);
            registerForm.setManaged(false);
            loginForm.setVisible(false);
            loginForm.setManaged(false);
            supplementaryForm.setVisible(true);
            supplementaryForm.setManaged(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), supplementaryForm);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void switchToRegisterFromSupplementary() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), supplementaryForm);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> {
            supplementaryForm.setVisible(false);
            supplementaryForm.setManaged(false);
            loginForm.setVisible(false);
            loginForm.setManaged(false);
            registerForm.setVisible(true);
            registerForm.setManaged(true);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), registerForm);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}