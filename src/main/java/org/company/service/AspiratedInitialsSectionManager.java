package org.company.service;

import org.company.bot.TelegramBot;
import org.company.model.Question;

import java.util.List;

public class AspiratedInitialsSectionManager extends AbsSectionManager{
    public AspiratedInitialsSectionManager(long chatId, List<Question> questions, TelegramBot bot) {
        super(chatId, questions, bot);
        setTag("aspirated-initials");
    }
}
