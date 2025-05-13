package danhhanma.part_time_job.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainView extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();
        double appWidth = screenWidth * 3/5;
        double appHeight = screenHeight * 3/5;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/danhhanma/part_time_job/main.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, appWidth, appHeight);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1200);
        primaryStage.setMaxWidth(1500);
        primaryStage.setMinHeight(700);
        primaryStage.setMaxHeight(1100);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}