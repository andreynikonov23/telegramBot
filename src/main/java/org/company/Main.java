package org.company;

import org.apache.log4j.Logger;
import org.company.config.SpringConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@PropertySource("classpath:/data.properties")
public class Main {
    private static final Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        logger.debug("Start application");
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(SpringConfig.class);
        TelegramBotsApi telegramBotsApi = applicationContext.getBean(TelegramBotsApi.class);
        logger.debug("Telegram Bot started...");
    }
}