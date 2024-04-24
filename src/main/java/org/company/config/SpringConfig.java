package org.company.config;

import org.apache.log4j.Logger;
import org.company.bot.TelegramBot;
import org.company.service.*;
import org.company.service.UpdateRecognizer;
import org.company.data.QuestionsLoader;
import org.company.data.UsersData;
import org.springframework.context.annotation.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@PropertySource("classpath:data.properties")
public class SpringConfig {
    private static final Logger logger = Logger.getLogger(SpringConfig.class);


    @Bean
    public UsersData usersData() {
        return new UsersData();
    }

    @Bean
    public QuestionsLoader questionsLoader() {
        logger.debug("Creating questionsLoaderBean");
        return new QuestionsLoader();
    }

    @Bean
    public TelegramBot telegramBot() {
        logger.debug("Creating telegramBotBean");
        return new TelegramBot();
    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBot bot) throws TelegramApiException {
        logger.debug("Creating telegramBotsApiBean");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(bot);
        return telegramBotsApi;
    }

    @Bean
    public UpdateRecognizer receiver() {
        return new UpdateRecognizer(telegramBot());
    }

    @Bean
    @Scope("prototype")
    public Test test() {
        return new Test(telegramBot(), questionsLoader());
    }
}
