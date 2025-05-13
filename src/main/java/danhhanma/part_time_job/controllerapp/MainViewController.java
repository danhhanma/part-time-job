package danhhanma.part_time_job.controllerapp;


import danhhanma.part_time_job.application.FacebookView;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

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
        setupOrgTypeComboBox();
        setupButtonActions();
    }

    private void setupRoleComboBox() {
        regRoleComboBox.setItems(FXCollections.observableArrayList(
                "Người tìm việc",
                "Nhà tuyển dụng"
        ));
    }

    private void setupOrgTypeComboBox() {
        orgTypeComboBox.setItems(FXCollections.observableArrayList(
                "Công ty",
                "Xí nghiệp",
                "Loại hình khác"
        ));
        // Xử lý khi chọn loại cơ quan
        orgTypeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if ("Loại hình khác".equals(newValue)) {
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
            openDashboard(role);
        }
    }

    private void handleRegister() {
        if (validateRegisterInput()) {
            String selectedRole = regRoleComboBox.getValue();
            if (selectedRole == null) {
                // TODO: Show error message for role selection
                return;
            }
            if ("Nhà tuyển dụng".equals(selectedRole)) {
                // Chuyển sang form bổ sung
                switchToSupplementary();
            } else {
                // Người tìm việc: Hoàn tất đăng ký ngay
                System.out.println("Registration successful for role: " + selectedRole);
                switchToLogin();
            }
        }
    }

    private void handleSupplementarySubmit() {
        if (validateSupplementaryInput()) {
            String orgType = orgTypeComboBox.getValue();
            String otherOrgType = otherOrgTypeField.getText();
            System.out.println("Supplementary info submitted: Org Type = " + orgType +
                    (orgType.equals("Loại hình khác") ? ", Other Type = " + otherOrgType : ""));
            // TODO: Implement logic to save supplementary info
            switchToLogin();
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

            FacebookView dashboard = new FacebookView();
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
}