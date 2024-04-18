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
        ChiCiSectionManager chiCiSectionManager = new ChiCiSectionManager(chatId, questionsLoader.getChiCiQuestionsList());
        chiCiSectionManager.setBot(bot);
        return chiCiSectionManager;
    }
    public AspiratedInitialsSectionManager getAspiratedInitialsSectionManager(long chatId){
        logger.info("SectionFabric create AspiratedInitialsSectionManager: ChatId=" + chatId);
        AspiratedInitialsSectionManager aspiratedInitialsSectionManager = new AspiratedInitialsSectionManager(chatId, questionsLoader.getAspiratedInitialsQuestionsList());
        aspiratedInitialsSectionManager.setBot(bot);
        return aspiratedInitialsSectionManager;
    }
    public BackLangFinalsSectionManager getBackLangFinalsSectionManager(long chatId){
        logger.info("SectionFabric create BackLangFinalsSectionManager: ChatId=" + chatId);
        BackLangFinalsSectionManager backLangFinalsSectionManager = new BackLangFinalsSectionManager(chatId, questionsLoader.getBackLangFinalsQuestionsList());
        backLangFinalsSectionManager.setBot(bot);
        return backLangFinalsSectionManager;
    }
    public EFinalSectionManager getEFinalSectionManager(long chatId){
        logger.info("SectionFabric create EFinalSectionManager: ChatId=" + chatId);
        EFinalSectionManager eFinalSectionManager = new EFinalSectionManager(chatId, questionsLoader.getEFinalQuestionsList());
        eFinalSectionManager.setBot(bot);
        return eFinalSectionManager;
    }
    public IanIangSectionManager getIanIangSectionManager(long chatId){
        logger.info("SectionFabric create IanIangSectionManager: ChatId=" + chatId);
        IanIangSectionManager ianIangSectionManager = new IanIangSectionManager(chatId, questionsLoader.getIanIangQuestionsList());
        ianIangSectionManager.setBot(bot);
        return ianIangSectionManager;
    }
    public JqxInitialsSectionManager getJqxInitialsSectionManager(long chatId){
        logger.info("SectionFabric create JqxInitialsSectionManager: ChatId=" + chatId);
        JqxInitialsSectionManager jqxInitialsSectionManager = new JqxInitialsSectionManager(chatId, questionsLoader.getJqxInitialsQuestionsList());
        jqxInitialsSectionManager.setBot(bot);
        return jqxInitialsSectionManager;
    }
    public RInitialsSectionManager getRInitialsSectionManager(long chatId){
        logger.info("SectionFabric create RInitialsSectionManager: ChatId=" + chatId);
        RInitialsSectionManager rInitialsSectionManager = new RInitialsSectionManager(chatId, questionsLoader.getRInitialsQuestionsList());
        rInitialsSectionManager.setBot(bot);
        return rInitialsSectionManager;
    }
    public SpecialFinalSectionManager getSpecialFinalSectionManager(long chatId){
        logger.info("SectionFabric create SpecialFinalSectionManager: ChatId=" + chatId);
        SpecialFinalSectionManager specialFinalSectionManager = new SpecialFinalSectionManager(chatId, questionsLoader.getSpecialFinalQuestionsList());
        specialFinalSectionManager.setBot(bot);
        return specialFinalSectionManager;
    }
    public UFinalSectionManager getUFinalSectionManager(long chatId){
        logger.info("SectionFabric create UFinalSectionManager: ChatId=" + chatId);
        UFinalSectionManager uFinalSectionManager = new UFinalSectionManager(chatId, questionsLoader.getUFinalQuestionsList());
        return uFinalSectionManager;
    }
}
