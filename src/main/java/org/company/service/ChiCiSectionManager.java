package org.company.service;

import org.company.bot.TelegramBot;
import org.company.model.Question;

import java.util.List;

public class ChiCiSectionManager extends AbsSectionManager{
    public ChiCiSectionManager(long chatId, List<Question> questions, TelegramBot bot) {
        super(chatId, questions, bot);
        setTag("chi-ci");
    }

    @Override
    public void result() {

    }
}
