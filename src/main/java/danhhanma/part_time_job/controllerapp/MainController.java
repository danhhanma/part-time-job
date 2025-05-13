package danhhanma.part_time_job.controllerapp;


import danhhanma.part_time_job.dashboard.FacebookController;
import danhhanma.part_time_job.login.LoginController;
import danhhanma.part_time_job.register.RegistrationController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private StackPane rootPane;

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    private Parent loginForm;
    private Parent registrationForm;
    private Parent dashboardForm;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            System.out.println("Đang tải các form...");

            // Tải form đăng nhập
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/danhhanma/test02/LoginForm.fxml"));
            loginForm = loginLoader.load();
            LoginController loginController = loginLoader.getController();
            loginController.setMainController(this);
            System.out.println("Form đăng nhập đã được tải");

            // Tải form đăng ký
            FXMLLoader registrationLoader = new FXMLLoader(getClass().getResource("/danhhanma/test02/RegistrationForm.fxml"));
            registrationForm = registrationLoader.load();
            RegistrationController registrationController = registrationLoader.getController();
            registrationController.setMainController(this);
            System.out.println("Form đăng ký đã được tải");

            // Tải form bảng điều khiển (Facebook.fxml)
            FXMLLoader dashboardLoader = new FXMLLoader(getClass().getResource("/danhhanma/test02/facebook.fxml"));
            dashboardForm = dashboardLoader.load();
            FacebookController facebookController = dashboardLoader.getController();
            facebookController.setMainController(this);
            System.out.println("Form bảng điều khiển đã được tải");

        } catch (IOException e) {
            System.out.println("Lỗi khi tải form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoginButton(ActionEvent event) {
        showLoginForm();
    }

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        showRegistrationForm();
    }

    public void showLoginForm() {
        System.out.println("Hiển thị form đăng nhập");
        if (loginForm != null) {
            if (!rootPane.getChildren().contains(loginForm)) {
                rootPane.getChildren().clear();
                rootPane.getChildren().add(loginForm);
            }
        } else {
            System.out.println("Lỗi: loginForm là null");
        }
    }

    public void closeLoginForm() {
        System.out.println("Đóng form đăng nhập");
        rootPane.getChildren().remove(loginForm);
    }

    public void showRegistrationForm() {
        System.out.println("Hiển thị form đăng ký");
        if (registrationForm != null) {
            if (!rootPane.getChildren().contains(registrationForm)) {
                rootPane.getChildren().clear();
                rootPane.getChildren().add(registrationForm);
            }
        } else {
            System.out.println("Lỗi: registrationForm là null");
        }
    }

    public void closeRegistrationForm() {
        System.out.println("Đóng form đăng ký");
        rootPane.getChildren().remove(registrationForm);
    }

    public void showDashboardForm() {
        System.out.println("Hiển thị form bảng điều khiển");
        if (dashboardForm != null) {
            if (!rootPane.getChildren().contains(dashboardForm)) {
                rootPane.getChildren().clear();
                rootPane.getChildren().add(dashboardForm);
            }
        } else {
            System.out.println("Lỗi: dashboardForm là null");
        }
    }

}