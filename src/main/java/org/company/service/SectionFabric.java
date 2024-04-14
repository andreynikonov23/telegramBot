package org.company.service;

import lombok.Data;
import org.company.bot.TelegramBot;
import org.company.utils.QuestionsLoader;


@Data
public class SectionFabric {
    private TelegramBot bot;
    private QuestionsLoader questionsLoader;

    public SectionFabric(TelegramBot bot, QuestionsLoader questionsLoader){
        this.bot = bot;
        this.questionsLoader = questionsLoader;
    }

    public ChiCiSectionManager getChiCiSection(long chatId) {
        return new ChiCiSectionManager(chatId, questionsLoader.getChiCiQuestionsList(), bot);
    }
    public AspiratedInitialsSectionManager getAspiratedInitialsSectionManager(long chatId){
        return new AspiratedInitialsSectionManager(chatId, questionsLoader.getAspiratedInitialsQuestionsList(), bot);
    }
    public BackLangFinalsSectionManager getBackLangFinalsSectionManager(long chatId){
        return new BackLangFinalsSectionManager(chatId, questionsLoader.getBackLangFinalsQuestionsList(), bot);
    }
    public EFinalSectionManager getEFinalSectionManager(long chatId){
        return new EFinalSectionManager(chatId, questionsLoader.getEFinalQuestionsList(), bot);
    }
    public IanIangSectionManager getIanIangSectionManager(long chatId){
        return new IanIangSectionManager(chatId, questionsLoader.getIanIangQuestionsList(), bot);
    }
    public JqxInitialsSectionManager getJqxInitialsSectionManager(long chatId){
        return new JqxInitialsSectionManager(chatId, questionsLoader.getJqxInitialsQuestionsList(), bot);
    }
    public RInitialsSectionManager getRInitialsSectionManager(long chatId){
        return new RInitialsSectionManager(chatId, questionsLoader.getRInitialsQuestionsList(), bot);
    }
    public SpecialFinalSectionManager getSpecialFinalSectionManager(long chatId){
        return new SpecialFinalSectionManager(chatId, questionsLoader.getSpecialFinalQuestionsList(), bot);
    }
    public UFinalSectionManager getUFinalSectionManager(long chatId){
        return new UFinalSectionManager(chatId, questionsLoader.getUFinalQuestionsList(), bot);
    }
}
