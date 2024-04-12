package org.company.service;

import org.company.bot.TelegramBot;
import org.company.model.Question;

import java.util.List;

public class SpecialFinalSectionManager extends AbsSectionManager{
    public SpecialFinalSectionManager(long chatId, List<Question> questions, TelegramBot bot) {
        super(chatId, questions, bot);
        setTag("special-final");
    }

    @Override
    public void result() {

    }
}
