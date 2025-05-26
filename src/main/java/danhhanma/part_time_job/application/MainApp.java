package danhhanma.part_time_job.application;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainApp extends Application {
    private static HostServices hostServices;

    @Override
    public void start(Stage primaryStage) throws Exception {
        hostServices = getHostServices();
        double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();
        double appWidth = screenWidth * 4/5;
        double appHeight = screenHeight * 4/5;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/danhhanma/part_time_job/ApplicantView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, appWidth, appHeight);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(950);

        primaryStage.show();
    }

    public static HostServices getHostServicesInstance() {
        return hostServices;
    }

    public static void main(String[] args) {
        launch(args);
    }
}