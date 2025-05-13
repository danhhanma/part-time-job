package danhhanma.part_time_job.register;


import danhhanma.part_time_job.controllerapp.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {

    @FXML
    private VBox registrationPane;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button registerButton;

    @FXML
    private Button closeButton;

    @FXML
    private Hyperlink loginLink;

    private MainController mainController;

    public void setMainController(MainController controller) {
        this.mainController = controller;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Thêm các lựa chọn vai trò vào ComboBox
        roleComboBox.getItems().addAll("Nhà tuyển dụng", "Người tìm việc", "Quản trị viên");
        roleComboBox.setValue("Nhà tuyển dụng");
    }

    @FXML
    private void handleCloseButton(ActionEvent event) {
        // Đóng form đăng ký (không đóng cả ứng dụng)
        if (mainController != null) {
            mainController.closeRegistrationForm();
        }
    }

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        // Xử lý logic đăng ký ở đây
        System.out.println("Đăng ký được nhấn!");
        System.out.println("Tên: " + nameField.getText());
        System.out.println("Email: " + emailField.getText());
        System.out.println("Vai trò: " + roleComboBox.getValue());

        // Thêm logic kiểm tra và xử lý form đăng ký ở đây
    }

    @FXML
    private void handleLoginLink(ActionEvent event) {
        System.out.println("Đã nhấn vào đăng nhập từ form đăng ký");
        if (mainController != null) {
            mainController.closeRegistrationForm();
            mainController.showLoginForm();
        } else {
            System.out.println("Lỗi: mainController là null");
        }
    }
}