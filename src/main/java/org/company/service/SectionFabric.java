package org.company.service;

import lombok.Data;
import org.apache.log4j.Logger;
import org.company.bot.TelegramBot;
import org.company.utils.QuestionsLoader;


@Data
public class SectionFabric {
    private static final Logger logger = Logger.getLogger(SectionFabric.class);
    private TelegramBot bot;
    private QuestionsLoader questionsLoader;

    public SectionFabric(TelegramBot bot, QuestionsLoader questionsLoader){
        this.bot = bot;
        this.questionsLoader = questionsLoader;
    }

    public ChiCiSectionManager getChiCiSection(long chatId) {
        logger.info("SectionFabric create ChiCiSectionManager: ChatId=" + chatId);
        return new ChiCiSectionManager(chatId, questionsLoader.getChiCiQuestionsList(), bot);
    }
    public AspiratedInitialsSectionManager getAspiratedInitialsSectionManager(long chatId){
        logger.info("SectionFabric create AspiratedInitialsSectionManager: ChatId=" + chatId);
        return new AspiratedInitialsSectionManager(chatId, questionsLoader.getAspiratedInitialsQuestionsList(), bot);
    }
    public BackLangFinalsSectionManager getBackLangFinalsSectionManager(long chatId){
        logger.info("SectionFabric create BackLangFinalsSectionManager: ChatId=" + chatId);
        return new BackLangFinalsSectionManager(chatId, questionsLoader.getBackLangFinalsQuestionsList(), bot);
    }
    public EFinalSectionManager getEFinalSectionManager(long chatId){
        logger.info("SectionFabric create EFinalSectionManager: ChatId=" + chatId);
        return new EFinalSectionManager(chatId, questionsLoader.getEFinalQuestionsList(), bot);
    }
    public IanIangSectionManager getIanIangSectionManager(long chatId){
        logger.info("SectionFabric create IanIangSectionManager: ChatId=" + chatId);
        return new IanIangSectionManager(chatId, questionsLoader.getIanIangQuestionsList(), bot);
    }
    public JqxInitialsSectionManager getJqxInitialsSectionManager(long chatId){
        logger.info("SectionFabric create JqxInitialsSectionManager: ChatId=" + chatId);
        return new JqxInitialsSectionManager(chatId, questionsLoader.getJqxInitialsQuestionsList(), bot);
    }
    public RInitialsSectionManager getRInitialsSectionManager(long chatId){
        logger.info("SectionFabric create RInitialsSectionManager: ChatId=" + chatId);
        return new RInitialsSectionManager(chatId, questionsLoader.getRInitialsQuestionsList(), bot);
    }
    public SpecialFinalSectionManager getSpecialFinalSectionManager(long chatId){
        logger.info("SectionFabric create SpecialFinalSectionManager: ChatId=" + chatId);
        return new SpecialFinalSectionManager(chatId, questionsLoader.getSpecialFinalQuestionsList(), bot);
    }
    public UFinalSectionManager getUFinalSectionManager(long chatId){
        logger.info("SectionFabric create UFinalSectionManager: ChatId=" + chatId);
        return new UFinalSectionManager(chatId, questionsLoader.getUFinalQuestionsList(), bot);
    }
}
