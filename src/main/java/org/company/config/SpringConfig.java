package org.company.config;

import org.apache.log4j.Logger;
import org.company.bot.TelegramBot;
import org.company.service.*;
import org.company.utils.AnswerReceiver;
import org.company.utils.QuestionsLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;

@Configuration
@ComponentScan("org.company")
@PropertySource("classpath:data.properties")
public class SpringConfig {
   private static final Logger logger = Logger.getLogger(SpringConfig.class);
   @Autowired
   private ApplicationContext applicationContext;

    @Bean
    public QuestionsLoader questionsLoader() throws IOException {
        logger.debug("Creating questionsLoaderBean");
        Resource media = applicationContext.getResource("classpath:/media/");
        return new QuestionsLoader(media);
    }
    @Bean
    public TelegramBot telegramBot(){
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
    public SectionFabric sectionFabric() throws IOException {
        return new SectionFabric(telegramBot(), questionsLoader());
    }
    @Bean
    public AnswerReceiver receiver(){
        return new AnswerReceiver(telegramBot());
    }
}
