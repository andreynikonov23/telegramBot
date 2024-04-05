package org.company.config;

import org.apache.log4j.Logger;
import org.company.bot.TelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@ComponentScan("org.company")
@PropertySource("classpath:data.properties")
public class SpringConfig {
   private Logger logger = Logger.getLogger(SpringConfig.class);

    @Bean
    public TelegramBot telegramBot(){
        logger.debug("Creating telegramBotBean");
        TelegramBot bot = new TelegramBot();
        return bot;
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot bot) throws TelegramApiException {
        logger.debug("Creating telegramBotsApiBean");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
        return telegramBotsApi;
    }
}
