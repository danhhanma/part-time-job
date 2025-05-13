package danhhanma.part_time_job.chat;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ChatMain extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        double screenWidth = javafx.stage.Screen.getPrimary().getBounds().getWidth();
        double screenHeight = javafx.stage.Screen.getPrimary().getBounds().getHeight();
        double appWidth = screenWidth * 4/5;
        double appHeight = screenHeight * 4/5;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/danhhanma/part_time_job/ChatUi.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, appWidth, appHeight);
        stage.setScene(scene);
        stage.setTitle("Chat Application");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
