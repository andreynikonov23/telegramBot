package org.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppLauncher extends Application {
    public static void main(String[] args) {
        Application.launch();
    }
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/window.fxml"));
        Scene scene = new Scene(loader.load());
        stage.setTitle("ChineseBot");
        stage.setScene(scene);
        stage.show();
    }
}
