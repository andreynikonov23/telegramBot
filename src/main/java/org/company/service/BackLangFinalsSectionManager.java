package org.company.service;

import org.company.bot.TelegramBot;
import org.company.model.Question;

import java.util.List;

public class BackLangFinalsSectionManager extends AbsSectionManager{
    public BackLangFinalsSectionManager(long chatId, List<Question> questions, TelegramBot bot) {
        super(chatId, questions, bot);
        setTag("back-lang-finals");
    }

    @Override
    public void result() {

    }
}
