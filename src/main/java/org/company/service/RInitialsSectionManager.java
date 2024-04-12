package org.company.service;

import org.company.bot.TelegramBot;
import org.company.model.Question;

import java.util.List;

public class RInitialsSectionManager extends AbsSectionManager{
    public RInitialsSectionManager(long chatId, List<Question> questions, TelegramBot bot) {
        super(chatId, questions, bot);
        setTag("r-initials");
    }

    @Override
    public void result() {

    }
}
