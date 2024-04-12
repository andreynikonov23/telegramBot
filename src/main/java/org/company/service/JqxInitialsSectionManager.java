package org.company.service;

import org.company.bot.TelegramBot;
import org.company.model.Question;

import java.util.List;

public class JqxInitialsSectionManager extends AbsSectionManager{
    public JqxInitialsSectionManager(long chatId, List<Question> questions, TelegramBot bot) {
        super(chatId, questions, bot);
        setTag("jqx-initials");
    }

    @Override
    public void result() {

    }
}
