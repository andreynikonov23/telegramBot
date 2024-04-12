package org.company.service;

import org.company.bot.TelegramBot;
import org.company.model.Question;

import java.util.List;

public class UFinalSectionManager extends AbsSectionManager{
    public UFinalSectionManager(long chatId, List<Question> questions, TelegramBot bot) {
        super(chatId, questions, bot);
        setTag("u-final");
    }

    @Override
    public void result() {

    }
}
