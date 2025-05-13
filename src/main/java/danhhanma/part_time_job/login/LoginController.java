package danhhanma.part_time_job.login;


import danhhanma.part_time_job.Utils.Config;
import danhhanma.part_time_job.Utils.LocalStorage;
import danhhanma.part_time_job.controllerapp.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private VBox loginPane;
    @FXML
    private Label emailErr;
    @FXML
    private Label passwordErr;
    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button recruiterLoginButton;

    @FXML
    private Button jobseekerLoginButton;

    @FXML
    private Button closeButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    private Hyperlink registerLink;

    private MainController mainController;

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Khởi tạo form đăng nhập nếu cần
    }

    @FXML
    private void handleCloseButton(ActionEvent event) {
        // Đóng form đăng nhập (không đóng cả ứng dụng)
        if (mainController != null) {
            mainController.closeLoginForm();
        }
    }

    @FXML
    private void handleRecruiterLoginButton(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        String emailregex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailregex)) {
            emailErr.setText("Email không hợp lệ");
            return;
        } else {
            emailErr.setText("");
        }

        if (password.isEmpty()) {
            passwordErr.setText("Mật khẩu không được để trống");
            return;
        } else {
            passwordErr.setText("");
        }

        try {
            JSONObject loginJson = new JSONObject();
            loginJson.put("email", email);
            loginJson.put("password", password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Config.get("api.url") + "/auth/login/employer"))  // Make sure this is correct
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(loginJson.toString()))
                    .build();

            // Send request asynchronously
            HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            JSONObject responseBody = new JSONObject(response.body());
                            String token = responseBody.getString("token");

                            try {
                                System.out.println("login successful");
                                String userName = responseBody.getJSONObject("user").getString("fullName");
                                long userId = responseBody.getJSONObject("user").getLong("id");
                                LocalStorage.saveUserId(userId);
                                LocalStorage.saveToken(token);
                                LocalStorage.saveUserName(userName);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            // Optionally save token for future use
                        } else {
                            JSONObject error = new JSONObject(response.body());
                            System.out.println("Login failed: " + error.getString("message"));
                        }
                    });
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void handleJobSeekerLoginButton(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();
        String emailregex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailregex)) {
            emailErr.setText("Email không hợp lệ");
            return;
        } else {
            emailErr.setText("");
        }

        if (password.isEmpty()) {
            passwordErr.setText("Mật khẩu không được để trống");
            return;
        } else {
            passwordErr.setText("");
        }

        try {
            JSONObject loginJson = new JSONObject();
            loginJson.put("email", email);
            loginJson.put("password", password);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(Config.get("api.url") + "/auth/login/applicant"))  // Make sure this is correct
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(loginJson.toString()))
                    .build();

            // Send request asynchronously
            HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(response -> {
                        if (response.statusCode() == 200) {
                            JSONObject responseBody = new JSONObject(response.body());
                            String token = responseBody.getString("token");
                            String userName = responseBody.getString("userName");
                            try {
                                LocalStorage.saveToken(token);
                                LocalStorage.saveUserName(userName);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            // Optionally save token for future use
                        } else {
                            JSONObject error = new JSONObject(response.body());
                            System.out.println("Login failed: " + error.getString("message"));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleForgotPasswordLink(ActionEvent event) {
        // Xử lý khi người dùng quên mật khẩu
        System.out.println("Quên mật khẩu được nhấn!");

        // Thêm logic xử lý quên mật khẩu ở đây (ví dụ: mở form quên mật khẩu)
    }

    @FXML
    private void handleRegisterLink(ActionEvent event) {
        // Chuyển đến màn hình đăng ký
        if (mainController != null) {
            mainController.showRegistrationForm();
            mainController.closeLoginForm();
        }
    }
}