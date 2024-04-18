package org.company.service;

import lombok.NoArgsConstructor;
import org.company.bot.TelegramBot;
import org.company.model.Question;

import java.util.List;

@NoArgsConstructor
public class JqxInitialsSectionManager extends AbsSectionManager{
    public JqxInitialsSectionManager(long chatId, List<Question> questions) {
        super(chatId, questions);
        setTag("jqx-initials");
    }
}
