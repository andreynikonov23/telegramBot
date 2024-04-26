package org.company;

import org.apache.log4j.Logger;
import org.company.config.SpringConfig;
import org.company.data.ActiveTasks;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.io.IOException;

@PropertySource("classpath:/data.properties")
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        logger.debug("Start application");
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        ActiveTasks.deserialize(applicationContext);
        applicationContext.getBean(TelegramBotsApi.class);
        logger.debug("Telegram Bot started...");
    }
}