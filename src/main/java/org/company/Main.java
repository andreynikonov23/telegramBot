package org.company;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.company.config.SpringConfig;
import org.company.data.ActiveTasks;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.io.IOException;

@PropertySource("classpath:/data.properties")
public class Main extends Application {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        logger.debug("Start application");
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        ActiveTasks.deserialize(applicationContext);
        applicationContext.getBean(TelegramBotsApi.class);
        logger.debug("Telegram Bot started...");
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